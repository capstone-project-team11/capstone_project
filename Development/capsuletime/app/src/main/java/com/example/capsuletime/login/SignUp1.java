package com.example.capsuletime.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capsuletime.R;

public class SignUp1 extends AppCompatActivity {

    private TextView tv_sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        //Intent intent = getIntent();
        //String idStr = intent.getStringExtra("id"); // 보내는 곳과 받는 곳의 name을 맞춤


        final TextView tv_id = findViewById(R.id.tv_id);
        final TextView tv_pw = findViewById(R.id.tv_pw);
        final TextView tv_email = findViewById(R.id.tv_email);

        Button bt_next = findViewById(R.id.bt_next);

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent next = new Intent(SignUp1.this, SignUp2.class);
                if ( tv_id.getText().length() > 0 &&  tv_pw.getText().length() > 0 && tv_email.getText().length() > 0){
                    // space 처리 작업 추가 필요

                    String id = tv_id.getText().toString();
                    String pw = tv_pw.getText().toString();
                    String email = tv_email.getText().toString();
                    next.putExtra("id",id);
                    next.putExtra("pw",pw);
                    next.putExtra("email",email);
                    startActivity(next);

                } else {
                    Toast.makeText(v.getContext(), "빈칸을 다 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
