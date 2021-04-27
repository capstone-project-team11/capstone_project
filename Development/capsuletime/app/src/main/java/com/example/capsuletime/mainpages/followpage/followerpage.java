package com.example.capsuletime.mainpages.followpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.mainpages.mypage.mypage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class followerpage extends AppCompatActivity {
    private User user;
    private String nick_name;
    private String nick_name2;
    private String nick_name3;
    private String nick_name5;
    private String first_name;
    private String last_name;
    private String image_url;
    private ArrayList nick_name_list = new ArrayList();
    /*private ArrayList nick_name_list2 = new ArrayList();*/
    private ArrayList nick_name_list3 = new ArrayList();
    private int follow_status;
    private RetrofitInterface retrofitInterface;
    private ArrayList<Follow> arrayList;
    private FollowLogAdapter followLogAdapter;
    private List<User> userList;
    private List<User> userList2;
    private static final String TAG = "FollowerPage";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followerpage);

        ArrayList nick_name_list2 = new ArrayList();

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        nick_name = intent.getStringExtra("nick_name");

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        followLogAdapter = new FollowLogAdapter(arrayList,this);
        recyclerView.setAdapter(followLogAdapter);


        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;


        retrofitInterface.requestF4F(nick_name).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userList2 = response.body();
                Log.d(TAG, "맞팔리스트 = "+userList2.toString());
                if (userList2 != null) {
                    for (User user : userList2) {
                        nick_name3 = user.getNick_name();
                        nick_name5 = user.getNick_name();

                            Log.d(TAG,"닉네임 = " + nick_name + " 맞팔 = " + nick_name3);
                            String image_url = user.getImage_url();
                            String last_name = user.getLast_name();
                            String first_name = user.getFirst_name();

                            nick_name_list2.add(nick_name3);
                            Log.d(TAG, "닉넴3 = " + nick_name_list2);

                            Follow follow = new Follow(image_url, nick_name3, last_name + first_name, 0);

                            arrayList.add(follow);


                    }

                        Log.d(TAG, "닉넴4 = " + nick_name_list2);
                        retrofitInterface.requestFollower(nick_name).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                userList = response.body();
                                Log.d(TAG, "팔로워 리스트 " + userList.toString());
                                if (userList != null) {
                                    for (User user : userList) {

                                        nick_name2 = user.getNick_name();



                                        nick_name_list.add(nick_name2);

                                        first_name = user.getFirst_name();
                                        last_name = user.getLast_name();
                                        image_url = user.getImage_url();
                                        follow_status = 0;


                                        Log.d(TAG, "닉넴5 = " + nick_name_list);
                                        Log.d(TAG, "닉넴6 = " + nick_name_list2);


                                        for(int i=0; i < nick_name_list.size();i++){
                                            for(int j=0; j< nick_name_list2.size(); j++){
                                                if(nick_name2.equals(nick_name_list2.get(j))){
                                                   follow_status = 1;
                                                    break;
                                                } else {
                                                    follow_status =0;

                                                }

                                            }

                                            if(follow_status == 0){
                                                Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 1);

                                                arrayList.add(follow);
                                                break;
                                            }

                                        }

                                        /*Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 1);

                                        arrayList.add(follow);*/
                                        Log.d(TAG, "닉넴2 = " + nick_name2 + " " + nick_name3);





                                        /*Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 1);

                                        arrayList.add(follow);*/


                                        /*Log.d(TAG,"nick name = " + nick_name2 + " status" + follow_status);*/

                        /*if(follow_status == 0) {

                            Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 1);
                            arrayList.add(follow);

                        } else {

                            Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 0);
                            arrayList.add(follow);
                        }*/
                        /*for(int i = 0; i < userList.size(); i++) {
                            if (nick_name2.equals(nick_name3)) {
                                Log.d(TAG,"2 : " + nick_name2 + " 3: " + nick_name3 );
                                arrayList.remove(nick_name2);
                                followLogAdapter.notifyDataSetChanged(); // redirect
                            }
                        }*/

                                    }

                                    /*if(nick_name_list2.isEmpty()){

                                        Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 1);

                                        arrayList.add(follow);
                                    }*/







                                        /*Log.d(TAG, "리스트 닉넴 = " + nick_name2 + " " + nick_name3);
                                        Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 1);

                                        arrayList.add(follow);
                                        break;*/



                                    followLogAdapter.notifyDataSetChanged(); // redirect
                                }
                            }


                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }

                        });


                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }


        });



        Button imageButton4 = (Button) findViewById(R.id.button4);
        imageButton4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), mypage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });



    }
}
