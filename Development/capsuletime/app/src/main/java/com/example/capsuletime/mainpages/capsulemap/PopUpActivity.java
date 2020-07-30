package com.example.capsuletime.mainpages.capsulemap;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.User;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.mainpages.followpage.Follow;
import com.example.capsuletime.mainpages.userpage.userpage;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopUpActivity extends AppCompatActivity {
    private ImageView ivImage;
    private TextView textView;
    private RetrofitInterface retrofitInterface;
    private User user;
    private List<User> userList;
    private static final String TAG = "Mypage";
    private String user_id;
    private String nick_name2;
    private String nick_name;
    private String imageUrl;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 제거 ( 전체화면 모드 )
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.custom_infowindow);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        nick_name2 = intent.getStringExtra("nick_name2");

        ImageView imageView1 = (ImageView) findViewById(R.id.follow) ;
        imageView1.setImageResource(R.drawable.follow_icon1) ;


        search();

        Button imageButton = (Button) findViewById(R.id.button3);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), userpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user_id", user_id);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

    }



    public void search() {

        NickNameSharedPreferences nickNameSharedPreferences = NickNameSharedPreferences.getInstanceOf(getApplicationContext());
        HashSet<String> nickNameSharedPrefer = (HashSet<String>) nickNameSharedPreferences.getHashSet(
                NickNameSharedPreferences.NICKNAME_SHARED_PREFERENCES_KEY,
                new HashSet<String>()
        );
        int count = 0;
        for (String nick : nickNameSharedPrefer) {
            if (count == 0){
                nick_name = nick;
            }
            count ++;
        }

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        retrofitInterface.requestSearchUser(nick_name2).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                Log.d(TAG+"dialog", response.body().toString());

                input();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {


                /*user.getImage_url();*/
                /*Log.d(TAG+"이미지 url ", user.getImage_url());*/
            }
        });


    }

    private void input(){

        ivImage = findViewById(R.id.clientPic);



        if(user!=null){
            if(user.getImage_url() == null) {
                Log.d("nullP","FFFF");
                String imgeUrl = "http://118.44.168.218:7070/contents/1591794397030.jpg";
                Glide.with(this).load(imgeUrl).into(ivImage);
            } else {
                Glide.with(this).load(user.getImage_url()).into(ivImage);
            }
        } else {
            Log.d(TAG,"user exist not");
        }


        TextView tv_user_id = (TextView)findViewById(R.id.tv_user_id);
        TextView tv_name = (TextView)findViewById(R.id.tv_name);
        tv_user_id.setText(user.getUser_id());
        String name = user.getLast_name() + user.getFirst_name();
        tv_name.setText(name);

        String inStr = (nick_name != null) ? nick_name : user.getNick_name();
        retrofitInterface.requestFollow(inStr).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userList = response.body();
                Log.d(TAG, userList.toString());
                if (userList != null) {
                    for (User user : userList) {
                        String nick_name3 = user.getNick_name();
                        if(nick_name2.equals(nick_name3)) {
                            ImageView imageView1 = (ImageView) findViewById(R.id.follow);
                            imageView1.setImageResource(R.drawable.follow_icon2);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        ImageView imageButton2 = (ImageView) findViewById(R.id.follow);
        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String inStr = (nick_name != null) ? nick_name : user.getNick_name();
                retrofitInterface.requestFollow(inStr).enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        userList = response.body();
                        Log.d(TAG, userList.toString());
                        if (userList != null) {
                            for (User user : userList) {
                                String nick_name3 = user.getNick_name();
                                if(nick_name2.equals(nick_name3)) {
                                    retrofitInterface.requestDeleteFollow(nick_name,nick_name2).enqueue(new Callback<Success>() {
                                        @Override
                                        public void onResponse(Call<Success> call, Response<Success> response) {
                                            ImageView imageView1 = (ImageView) findViewById(R.id.follow);
                                            imageView1.setImageResource(R.drawable.follow_icon1);
                                        }

                                        @Override
                                        public void onFailure(Call<Success> call, Throwable t) {
                                            Toast.makeText(view.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    retrofitInterface.requestCreateFollow(nick_name,nick_name2).enqueue(new Callback<Success>() {
                                        @Override
                                        public void onResponse(Call<Success> call, Response<Success> response) {
                                            ImageView imageView1 = (ImageView) findViewById(R.id.follow);
                                            imageView1.setImageResource(R.drawable.follow_icon2);
                                        }

                                        @Override
                                        public void onFailure(Call<Success> call, Throwable t) {
                                            Toast.makeText(view.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {

                    }
                });
            }
        });

    }


}
