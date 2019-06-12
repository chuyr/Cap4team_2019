package com.example.ppg_proto_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AlertDialog.Builder personal=new AlertDialog.Builder(this);
        personal.setTitle("개인정보수집 동의");
        personal.setMessage("PPG 는 아래의 목적으로 개인정보를 수집 및 이용하며, 회원의 개인정보를 안전하게 취급하는데 최선을 다합니다.\n" +
                "1. 수집목적\n" +
                "• 이용자 식별 \n" +
                "• 공용 PC실 이용 시 PC 이용 기록 수집 \n" +
                "• 서비스 이용 기록 및 통계 분석을 통한 쾌적한 공용 PC실 환경 조성 \n" +
                "• 신규 서비스 개발, 이벤트 행사 시 정보 전달, 마케팅 및 광고 등에 활용 \n" +
                "• 프라이버시 보호 측면의 서비스 환경 구축 \n" +
                "2. 수집항목\n" +
                "(필수) 이용자 식별 번호, 공용 PC 이용 데이터\n" +
                "3. 보유기간\n" +
                "수집된 정보는 졸업 후 지체없이 파기됩니다. 다만 내부 방침에 의해 서비스 부정이용기록은 부정 가입 및 이용 방지를 위하여 회원 탈퇴 시점으로부터 최대 1년간 보관 후 파기하며, 관계법령에 의해 보관해야 하는 정보는 법령이 정한 기간 동안 보관한 후 파기합니다. \n" +
                "\n" +
                "서비스 제공을 위해 필요한 최소한의 개인정보이므로 동의를 해 주셔야 서비스 이용이 가능합니다. \n" +
                "더 자세한 내용에 대해서는 개인정보처리방침을 참고하시기 바랍니다.");
        personal.setPositiveButton("동의합니다.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "동의확인", Toast.LENGTH_LONG).show();
            }
        });

        personal.setNegativeButton("동의 안합니다.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        /*
        personal.setNeutralButton("모름", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"모름",Toast.LENGTH_LONG).show();
            }
        });*/

        personal.setCancelable(false);
        personal.show();
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.loginButton) ;
        final EditText editText_id = (EditText)findViewById(R.id.edit_id);

        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info_id_number = editText_id.getText().toString();
                // TODO : click event
                Intent intent=new Intent(MainActivity.this,Read_QR.class);
                intent.putExtra("info_stu_id1", info_id_number);
                startActivity(intent);
            }
        });
    }
}
