package com.example.ppg_proto_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Read_QR extends AppCompatActivity {
    private Button scan_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read__qr);

        scan_btn = (Button)findViewById(R.id.scan_button);
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(true);

                integrator.initiateScan();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Intent intent_from_main = getIntent();   // 데이터 수신
        String info_id_number = intent_from_main.getExtras().getString("info_stu_id1");
        //Toast.makeText(this, info_id_number, Toast.LENGTH_SHORT).show();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);
        if (result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                String str1 =result.getContents();
                String word1 = str1.split(";")[0];
                String word2 = str1.split(";")[1];

                //word1 : MAC 주소
                //word2 : 강의실번호 , PC번호

                Intent intent2=new Intent(Read_QR.this,Information.class);
                intent2.putExtra("info_macaddress", word1);
                intent2.putExtra("info_stu_id2", info_id_number);
                intent2.putExtra("info_pc", word2);
                startActivity(intent2);

            }
        }

        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
