package com.example.ppg_proto_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Information extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd kk:mm");

    Button btn_wol_start;
    Button btn_wol_end;
    TextView txt_wol_start;
    TextView txt_wol_end;
    TextView txt_start_time;

    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 23457;//라파 파이선 포트번호
    private int port2 = 23458; // shutdown_pc 대상 포트번호
    private String rasp_ip = "192.168.0.81";  // 라파 ip 넣어줘야함
    private String pc_ip = "0.0.0.0";
    private static String TAG = "SEND_PHP";
    private MyHandler myHandler;
    private MyThread myThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        TextView textview_id_number = (TextView)findViewById(R.id.textview_id_number);
        TextView textview_pc_number = (TextView)findViewById(R.id.textview_pc_number);
        txt_start_time = (TextView)findViewById(R.id.txt_start_time);
        txt_wol_start = (TextView)findViewById(R.id.txt_wol_start);
        txt_wol_end = (TextView)findViewById(R.id.txt_wol_end);
        btn_wol_start = (Button)findViewById(R.id.btn_wol_start);
        btn_wol_end = (Button)findViewById(R.id.btn_wol_end);

        Intent intent = getIntent();

        //맥주소~
        final String info_mac_address = intent.getExtras().getString("info_macaddress");

        //학번~
        final String info_id_number = intent.getExtras().getString("info_stu_id2");
        textview_id_number.setText(info_id_number);

        //피씨실, 피씨번호
        final String info_pc_number = intent.getExtras().getString("info_pc");
        textview_pc_number.setText(info_pc_number);

        // 아래로 TCP 통한 wol 부분을 라파로 보내주는 부분이다.
        // 꺼주는 부분도 겹침 policy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        try {
//            clientSocket = new Socket(rasp_ip, port);
//            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        myHandler = new MyHandler();
//        myThread = new MyThread();
//        myThread.start();

        btn_wol_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clientSocket = new Socket(rasp_ip, port);
                    socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    socketOut = new PrintWriter(clientSocket.getOutputStream(), true);

                    myHandler = new MyHandler();
                    myThread = new MyThread();
                    myThread.start();

                    String replacemac = info_mac_address.replace("-",":");
                    socketOut.println(replacemac); //mac 주소값 넣어주기.
                    txt_start_time.setText(getTime());

                    String num = info_id_number;
                    String mac = info_mac_address;
                    String start = txt_start_time.getText().toString();

                    InsertData task = new InsertData();
                    task.execute("http://" + rasp_ip + "/insert.php", num,mac,start);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btn_wol_end.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                try{
                    String mac2 = info_mac_address;
                    getIP task2 = new getIP();
                    task2.execute(mac2);

                    clientSocket = new Socket(pc_ip, port2);
                    socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    socketOut = new PrintWriter(clientSocket.getOutputStream(), true);

                    myHandler = new MyHandler();
                    myThread = new MyThread();
                    myThread.start();

                    //String replacemac = info_mac_address.replace("-",":");

                    //String mac2 = "00:11:22:33:44:55";
                    socketOut.println(mac2);

                }catch (Exception e){
                    e.printStackTrace();
                }
        }
        }
        );
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Information.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String num = (String)params[1];
            String mac = (String)params[2];
            String start = (String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "num=" + num + "&mac=" + mac +"&start_login=" + start;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate =new Date(mNow);
        return mFormat.format(mDate);
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {

                    String data = socketIn.readLine();

                    Message msg = myHandler.obtainMessage();
                    msg.obj = data;
                    myHandler.sendMessage(msg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class getIP extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String mac_getip = arg0[0];

                String link = "http://" + rasp_ip + "/test1.php?mac=" + mac_getip ;
                //String encodedurl = URLEncoder.encode(link, "UTF-8");

                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
        @Override
        protected void onPostExecute(String result){
            //txtview.setText("Login Successful");
            //txtview.setText(result);
            txt_wol_end.setText(result);
            pc_ip = result;
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            txt_wol_end.setText(msg.obj.toString());
        }
    }
}
