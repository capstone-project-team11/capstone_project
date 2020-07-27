package com.example.capsuletime.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.capsuletime.Logined;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.User;
import com.example.capsuletime.core.preferences.CookieSharedPreferences;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.mainpages.mypage.mypage;

import java.io.IOException;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// alt + enter : auto

public class login extends AppCompatActivity {

    private RetrofitInterface retrofitInterface;
    private EditText idText;
    private EditText pwText;
    private Button btn_sign_in;
    private Button btn_sign_up;
    private TextView signUp;
    private String nick_name;
    private String temp;
    private User user;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RetrofitClient retrofit_Client = new RetrofitClient(getApplicationContext());

        retrofitInterface = retrofit_Client.retrofitInterface;

        idText = findViewById(R.id.et_id);
        pwText = findViewById(R.id.et_pw);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_up = findViewById(R.id.btn_sign_up);

        btn_sign_in.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                final String id = idText.getText().toString();
                final String pw = pwText.getText().toString();
                Log.d("TAG",id+ " "+pw);
                temp = id;
                retrofitInterface.requestLogin(id, pw).enqueue(new Callback<Logined>() {
                    @Override
                    public void onResponse(Call<Logined> call, Response<Logined> response) {
                        if (response.code() == 200) {
                            Logined auth = response.body();
                            nick_name = auth.getNick_name();
                            Log.d(TAG, auth.getNick_name().toString());
                            // Header Code 확인
                            //Log.d("TAG",Integer.toString(response.code()));

                            if (auth.getNick_name() != null) {

                                NickNameSharedPreferences nickNameSharedPreferences = NickNameSharedPreferences.getInstanceOf(getApplicationContext());
                                HashSet<String> nickName = new HashSet<String>();
                                nickName.add(auth.getNick_name());
                                nickNameSharedPreferences.putHashSet(NickNameSharedPreferences.NICKNAME_SHARED_PREFERENCES_KEY, nickName);

                                Toast.makeText(v.getContext(),"정상적으로 로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), mypage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(v.getContext(),"Id와 Password를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(v.getContext(),"Id와 Password를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Logined> call, Throwable t) {
                        Toast.makeText(v.getContext(),"서버와 통신이 불가능합니다.",Toast.LENGTH_SHORT).show();
                    }
                });

            }


        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, SignUp1.class ); //(현재액티비티, 이동할액티비티)
                //intent.putExtra("id", temp); // 인텐트 값을 전달
                startActivity(intent); // 액티비티 이동
            }
        });
        /*
        Call<Logined> call = retrofitInterface.requestSessionAuth();
        try {
            Response<Logined> response = call.execute();
            int code = response.code();
            Logined auth = response.body();
            Log.d(TAG, Integer.toString(code));
            if (code == 200 && auth.getNick_name() != null){
                NickNameSharedPreferences nickNameSharedPreferences = NickNameSharedPreferences.getInstanceOf(getApplicationContext());
                HashSet<String> nickName = new HashSet<String>();
                nickName.add(auth.getNick_name());
                nickNameSharedPreferences.putHashSet(NickNameSharedPreferences.NICKNAME_SHARED_PREFERENCES_KEY, nickName);
                //Toast.makeText(getApplicationContext(),"정상적으로 로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), mypage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */




        /*
        signUp = findViewById(R.id.SignUp);
        Spannable span = (Spannable)signUp.getText();
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.GRAY);
        span.setSpan(foregroundSpan, 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View v) {
                Intent intent = new Intent(login.this, SignUp1.class ); //(현재액티비티, 이동할액티비티)
                intent.putExtra("id", temp); // 인텐트 값을 전달
                startActivity(intent); // 액티비티 이동
            }
            @Override
            public void updateDrawState(final TextPaint textPaint) { // 색 정의
                textPaint.setColor(login.this.getResources().getColor(R.color.gray));
                textPaint.setUnderlineText(true);
            }
        },0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());
    */
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"OnStart");

    }

    @Override
    public void onResume(){
        super.onResume();
        retrofitInterface.requestSessionAuth().enqueue(new Callback<Logined>() {
            @Override
            public void onResponse(Call<Logined> call, Response<Logined> response) {
                Logined auth = response.body();
                Log.d(TAG,"response");
                // 인증된 세션
                if (response.code() == 200 ){//&& auth.getNick_name() != null) {
                    NickNameSharedPreferences nickNameSharedPreferences = NickNameSharedPreferences.getInstanceOf(getApplicationContext());
                    HashSet<String> nickName = new HashSet<String>();
                    nickName.add(auth.getNick_name());
                    nickNameSharedPreferences.putHashSet(NickNameSharedPreferences.NICKNAME_SHARED_PREFERENCES_KEY, nickName);

                    //Toast.makeText(getApplicationContext(),"정상적으로 로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), mypage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Logined> call, Throwable t) {
                Log.d(TAG,t.toString());
            }
        });
    }
}