package com.example.capsuletime.mainpages.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.capsuletime.CommnetLogData;
import com.example.capsuletime.Content;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.core.preferences.CookieSharedPreferences;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.login.login;
import com.example.capsuletime.mainpages.ar.UnityPlayerActivity;
import com.example.capsuletime.mainpages.capsulemap.PopUpActivity;
import com.example.capsuletime.mainpages.capsulemap.capsulemap;
import com.example.capsuletime.mainpages.followpage.Follow;
import com.example.capsuletime.mainpages.followpage.followerpage;
import com.example.capsuletime.mainpages.followpage.followpage;
import com.example.capsuletime.mainpages.mypage.setting.settingpage;
import com.example.capsuletime.mainpages.searchpage.searchpage;
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

public class mypage extends AppCompatActivity {

    private String user_id;
    private String expire_date;
    private String nick_name;
    private String url;
    private String lock_d_day;
    private int lock_D_day;
    private int capsule_id;
    private ArrayList<CapsuleLogData> arrayList;
    private ArrayList<Comment> arrayList2;
    private CapsuleLogAdapter capsuleLogAdapter;
    private CommentLogAdapter commentLogAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RetrofitInterface retrofitInterface;
    private static final String TAG = "MyPage";
    private List<Capsule> capsuleList;
    private List<CommnetLogData> commentList;
    private List<User> userList;
    private String drawablePath;
    private User user;
    private CommnetLogData commnetLogData;
    private int fromArFlag;

    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_mypage);
        Log.d(TAG,"FFFF is mypage");
        Intent intent = getIntent();
        fromArFlag = intent.getIntExtra("fromAr",0);
        Log.d("Hello","mypage");

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

        Log.d(TAG,nick_name.toString() + "닉네임");

        ImageView iv_user = (ImageView) this.findViewById(R.id.user_image);
        TextView tv_nick = (TextView) this.findViewById(R.id.tv_nick);

        TextView follow = (TextView) this.findViewById(R.id.follow2);
        TextView follower = (TextView) this.findViewById(R.id.follower2);

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        drawablePath = "res:///" + R.drawable.capsule_temp;

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        capsuleLogAdapter = new CapsuleLogAdapter(arrayList,this);
        recyclerView.setAdapter(capsuleLogAdapter);

        iv_user.setImageResource(R.drawable.user);


        if(nick_name != null){
            tv_nick.setText(nick_name);
            retrofitInterface.requestSearchUser(nick_name).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    // 세션 만료시 로그인 창으로 이동
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
                                    .circleCrop()
                                    .into(iv_user);

                        }

                    } else {
                        iv_user.setImageResource(R.drawable.user);
                        tv_nick.setText("");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, "server-get-user fail");
                }
            });
            String inStr = (nick_name != null) ? nick_name : user.getNick_name();
            retrofitInterface.requestFollow(inStr).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    userList = response.body();
                    if (userList != null) {
                        int count = 0;
                        for (User user : userList) {
                            count++;
                        }
                        String count_follow = String.valueOf(count);
                        follow.setText(count_follow);
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });

            String inStr2 = (nick_name != null) ? nick_name : user.getNick_name();
            retrofitInterface.requestFollower(inStr2).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    userList = response.body();
                    if (userList != null) {
                        int count = 0;
                        for (User user : userList) {
                            count++;
                        }
                        String count_follow = String.valueOf(count);
                        follower.setText(count_follow);
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });

        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set mypage Selected
        bottomNavigationView.setSelectedItemId(R.id.mypage);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.mypage:
                        return true;

                    case R.id.capsulesearch:{
                        Intent intent = new Intent(getApplicationContext(), searchpage.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    }

                    case R.id.capsulemap: {
                        if (Build.VERSION.SDK_INT >= 23 &&
                                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mypage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    0);
                            break;
                        } else if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                            Intent intent = new Intent(getApplicationContext(), capsulemap.class);
                            Log.d(TAG, user.toString());
                            startActivity(intent);
                            overridePendingTransition(0, 0);

                            return true;

                        }
                    }
                    case R.id.capsulear: {
                        //bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        //return true;
                    }
                }

                bottomNavigationView.setSelectedItemId(R.id.mypage);
                return false;
            }
        });




        String inStr = (nick_name != null) ? nick_name : nick_name;
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
                            int state_lock = capsule.getStatus_lock();
                            int capsule_id = capsule.getCapsule_id();
                            int capsule_key_count = capsule.getKey_count();
                            int capsule_key_used = capsule.getUsed_key_count();
                            String title = capsule.getTitle() != null ? capsule.getTitle() : "";
                            String url = capsule.getContent().size() != 0 ?
                                    capsule.getContent().get(0).getUrl() : Integer.toString(R.drawable.no_image);
                            List<Content> contentList = capsule.getContent();
                            String created_date = capsule.getDate_created();
                            String opened_date = capsule.getDate_created();
                            String location = "Default";
                            String d_day = "0";
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
                                        lock_D_day = 1;
                                        if(diff / (1000 * 60 * 60 * 24) == 0){
                                            lock_d_day = "D-Day";
                                            lock_D_day = 1;
                                        }
                                    } else {
                                        if(diff / (1000 * 60 * 60 * 24) == 0){
                                            lock_d_day = "D-Day";
                                            lock_D_day = 1;
                                        } else {
                                            lock_d_day = "D " + Long.toString(diff / (1000 * 60 * 60 * 24));
                                            lock_D_day = 0;
                                        }
                                    }
                                    Log.d(TAG,"만료기간 디데이: " + diff / (1000 * 60 * 60 * 24));

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
                                d_day = "D - " + Long.toString(diff / (1000 * 60 * 60 * 24));

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
                                    if (list.size() == 0) {
                                        // no
                                    } else {
                                        location = list.get(0).getAddressLine(0);
                                        Log.d(TAG, "location good");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG, url + " " + title + " " + created_date + " " + opened_date + " " + location + " " + state_lock + " " + state_temp);

                            int viewType = 0;

                            if(state_temp == 0 && state_lock == 0){
                                viewType = 0;

                                CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                        url, title, text,likes, "#절친 #평생친구", created_date,
                                        opened_date, location, state_temp, state_lock, contentList, viewType);
                                arrayList.add(capsuleLogData);
                            } else if(state_temp == 1 && state_lock == 0) {
                                viewType = 1;

                                CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                        url, title, text,likes, "#절친 #평생친구", created_date,
                                        opened_date, location, state_temp, state_lock, contentList, 1);
                                arrayList.add(capsuleLogData);
                            } else if(state_lock == 1) {

                                if(capsule_key_count == capsule_key_used){
                                    viewType = 4;

                                    CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, lock_d_day,
                                            url, title, text,likes, "#절친 #평생친구", created_date,
                                            opened_date, location, state_temp, state_lock, contentList, viewType);
                                    arrayList.add(capsuleLogData);
                                } else {
                                    if(lock_D_day == 1){
                                        viewType = 3;

                                        String u = String.valueOf(capsule_key_count);
                                        String v = String.valueOf(capsule_key_used);
                                        String key_count;
                                        key_count =v + " / " + u;
                                        Log.d(TAG, "키 카운트 : " + key_count.toString());

                                        CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                                url, lock_d_day, key_count,likes, "#절친 #평생친구", created_date,
                                                opened_date, location, state_temp, state_lock, contentList, viewType);
                                        arrayList.add(capsuleLogData);
                                    } else {

                                        viewType = 2;
                                        CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                                url, lock_d_day, text, likes, "#절친 #평생친구", created_date,
                                                opened_date, location, state_temp, state_lock, contentList, 2);
                                        arrayList.add(capsuleLogData);
                                    }
                                }
                            }

                            capsuleLogAdapter.notifyDataSetChanged(); // redirect
                        }
                    }
                }


                @Override
                public void onFailure(Call<List<Capsule>> call, Throwable t) {
                    Log.d(TAG, "fail");
                }
            });
        ImageView imageButton = (ImageView) findViewById(R.id.capsule_map);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mypage_map.class);
                intent.putExtra("nick_name", user.getNick_name());
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        TextView imageButton2 = (TextView) findViewById(R.id.follow);
        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user_id", user_id);
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        TextView imageButton3 = (TextView) findViewById(R.id.follower);
        imageButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followerpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user_id", user_id);
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        ImageView imageButton4 = (ImageView) findViewById(R.id.setting);
        imageButton4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), settingpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user_id", user.getUser_id());
                intent.putExtra("image_url", user.getImage_url());
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
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