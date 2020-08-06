package com.example.capsuletime.mainpages.userpage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capsuletime.Capsule;
import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.Content;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.login.login;
import com.example.capsuletime.mainpages.ar.UnityPlayerActivity;
import com.example.capsuletime.mainpages.capsulemap.capsulemap;
import com.example.capsuletime.mainpages.followpage.followerpage;
import com.example.capsuletime.mainpages.followpage.followpage;
import com.example.capsuletime.mainpages.mypage.CapsuleLogAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class userpage extends AppCompatActivity {

    private String user_id;
    private String nick_name;
    private String nick_name2;
    private String expire_date;
    private String lock_d_day;
    private ArrayList<CapsuleLogData> arrayList;
    private UserCapsuleLogAdapter userCapsuleLogAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RetrofitInterface retrofitInterface;
    private static final String TAG = "UserPage";
    private List<Capsule> capsuleList;
    private String drawablePath;
    private User user;
    private int fromArFlag;

    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_user_page);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        nick_name2 = intent.getStringExtra("nick_name2");

        fromArFlag = intent.getIntExtra("fromAr",0);
        Log.d(TAG, "아이디랑 닉넴 = " + user_id+nick_name2);

        ImageView iv_user = (ImageView) this.findViewById(R.id.user_image);
        TextView tv_nick = (TextView) this.findViewById(R.id.tv_nick);

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

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        drawablePath = "res:///" + R.drawable.capsule_temp;

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        userCapsuleLogAdapter = new UserCapsuleLogAdapter(arrayList,this);
        recyclerView.setAdapter(userCapsuleLogAdapter);

        iv_user.setImageResource(R.drawable.user);




        if(nick_name2 != null){
            tv_nick.setText(nick_name2);
            retrofitInterface.requestSearchUser(nick_name2).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if (response.code() == 401) {
                        Intent intent = new Intent(getApplicationContext(), login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    user = response.body();
                    if (user != null) {
                        if(user.getImage_url() == null || Objects.equals(user.getImage_url(), "")){
                            Log.d(TAG,"url null");
                            iv_user.setImageResource(R.drawable.user);
                        } else {
                            Log.d(TAG,"url not null");
                            Glide
                                    .with(getApplicationContext())
                                    .load(user.getImage_url())
                                    .into(iv_user);
                        }
                    } else {
                        iv_user.setImageResource(R.drawable.user);
                        tv_nick.setText("서버통신오류");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, "server-get-user fail");
                }
            });

        }



        String inStr = (nick_name2 != null) ? nick_name2 : user.getNick_name();
        if (inStr != null)
            retrofitInterface.requestSearchUserNick(inStr).enqueue(new Callback<List<Capsule>>() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<Capsule>> call, Response<List<Capsule>> response) {

                    if (response.code() == 401) {
                        Intent intent = new Intent(getApplicationContext(), login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    capsuleList = response.body();
                    if (capsuleList != null) {
                        for (Capsule capsule : capsuleList) {
                            int state_temp = capsule.getStatus_temp();
                            int capsule_id = capsule.getCapsule_id();
                            int state_lock = capsule.getStatus_lock();
                            int capsule_key_count = capsule.getKey_count();
                            int capsule_used_key_count = capsule.getUsed_key_count();
                            String title = capsule.getTitle() != null ? capsule.getTitle() : "";
                            String url = capsule.getContent().size() != 0 ?
                                    capsule.getContent().get(0).getUrl() : Integer.toString(R.drawable.capsule_marker_angry);
                            List<Content> contentList = capsule.getContent();
                            String created_date = capsule.getDate_created();
                            String opened_date = capsule.getDate_created();
                            String location = "Default";
                            String d_day = "0";
                            /*List<String> members = capsule.getMembers();

                            Log.d(TAG, "총 멤버들 : " + members.toString());*/
                            if(capsule.getExpire() != null) {
                                expire_date = capsule.getExpire();

                                try {
                                    // UTC -> LOCAL TIME
                                    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String created_utcDate = capsule.getDate_created();
                                    Date crt_date = getLocalTime(created_utcDate);

                                    String localDate = fm.format(crt_date);
                                    created_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                            "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";



                                    String opened_utcDate = expire_date;
                                    Date opn_date = getLocalTime(opened_utcDate);
                                    localDate = fm.format(opn_date);
                                    opened_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                            "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";

                                    Date date = new Date();

                                    long diff = date.getTime() - opn_date.getTime();
                                    if(diff > 0) {
                                        lock_d_day = "D +" + Long.toString(diff / (1000 * 60 * 60 * 24));
                                    } else {
                                        lock_d_day = "D " + Long.toString(diff / (1000 * 60 * 60 * 24));
                                    }
                                    Log.d(TAG,"만료기간 디데이: " + d_day.toString());

                                    //Log.d(TAG,created_date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "asdasd");
                                }
                            }
                            String text = capsule.getText();
                            int likes = capsule.getLikes();
                            // UTC Time control


                            try {
                                // UTC -> LOCAL TIME
                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String created_utcDate = capsule.getDate_created();
                                Date crt_date = getLocalTime(created_utcDate);

                                String localDate = fm.format(crt_date);
                                created_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                        "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";

                                String opened_utcDate = capsule.getDate_opened();
                                Date opn_date = getLocalTime(opened_utcDate);
                                localDate = fm.format(opn_date);
                                opened_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                        "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";

                                Date date = new Date();

                                long diff = date.getTime() - opn_date.getTime();
                                d_day = "D - " + Long.toString( diff/ (1000 * 60 * 60 * 24) );

                                //Log.d(TAG,created_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.d(TAG, "asdasd");
                            }

                            try {
                                DecimalFormat df = new DecimalFormat();
                                df.setMaximumFractionDigits(3);
                                double lat = Double.parseDouble(df.format(capsule.getLat()));
                                double lng = Double.parseDouble(df.format(capsule.getLng()));

                                List<Address> list = geocoder.getFromLocation(lat, lng, 3);
                                if (list != null) {
                                    if (list.size() == 0){
                                        // no
                                    } else {
                                        location = list.get(0).getAddressLine(0);
                                        Log.d(TAG, "location good");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG,url+" "+title+" "+created_date+" "+opened_date+" "+location+" "+state_temp);

                            int viewType = 0;

                            if(state_temp == 0 && state_lock == 0){
                                viewType = 0;

                                CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                        url, title, text,likes, "#절친 #평생친구", created_date,
                                        opened_date, location, state_temp, state_lock, contentList, viewType);
                                arrayList.add(capsuleLogData);
                            } else if(state_temp == 1 && state_lock == 0) {
                                viewType = 1;

                            } else if(state_lock == 1) {

                                if(capsule_key_count == capsule_used_key_count){
                                    viewType = 0;

                                    CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                            url, title, text,likes, "#절친 #평생친구", created_date,
                                            opened_date, location, state_temp, state_lock, contentList, viewType);
                                    arrayList.add(capsuleLogData);
                                } else {
                                    List<String> members = capsule.getMembers();
                                    for(int i =0; i < members.size(); i ++){
                                    if(members.get(i).equals(nick_name)) {
                                        viewType = 2;
                                        Log.d(TAG,"멤버: " + members.get(i));
                                        CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                                url, lock_d_day, text, likes, "#절친 #평생친구", created_date,
                                                opened_date, location, state_temp, state_lock, contentList, 2);
                                        arrayList.add(capsuleLogData);
                                    }
                                    }
                                }
                            }
                            userCapsuleLogAdapter.notifyDataSetChanged(); // redirect
                        }
                    }
                }


                @Override
                public void onFailure(Call<List<Capsule>> call, Throwable t) {
                    Log.d(TAG, "fail");
                }
            });


        Button imageButton = (Button) findViewById(R.id.button2);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), userpage_map.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        Button imageButton2 = (Button) findViewById(R.id.button5);
        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button imageButton3 = (Button) findViewById(R.id.button6);
        imageButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followerpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button imageButton4 = (Button) findViewById(R.id.button4);
        imageButton4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }




    private Date getLocalTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(time);
        long utctime = date.getTime();
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getOffset(utctime);
        long localtime = utctime + offset;
        Date local = new Date();
        local.setTime(localtime);

        return local;
    }
}
