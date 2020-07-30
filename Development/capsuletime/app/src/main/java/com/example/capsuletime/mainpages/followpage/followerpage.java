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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class followerpage extends AppCompatActivity {
    private User user;
    private String nick_name;
    private RetrofitInterface retrofitInterface;
    private ArrayList<Follow> arrayList;
    private FollowLogAdapter followLogAdapter;
    private List<User> userList;
    private static final String TAG = "FollowerPage";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followerpage);

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

        String inStr = (nick_name != null) ? nick_name : user.getNick_name();
        retrofitInterface.requestFollower(inStr).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userList = response.body();
                Log.d(TAG, userList.toString());
                if (userList != null) {
                    for (User user : userList) {
                        String nick_name2 = user.getNick_name();
                        String first_name = user.getFirst_name();
                        String last_name = user.getLast_name();
                        String image_url = user.getImage_url();


                        retrofitInterface.requestFollow(inStr).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                userList = response.body();

                                Log.d(TAG, userList.toString());
                                if (userList != null) {
                                    for (User user : userList) {
                                        String nick_name3 = user.getNick_name();

                                        if(nick_name2.equals(nick_name3)) {
                                            Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 0);
                                            arrayList.add(follow);
                                            followLogAdapter.notifyDataSetChanged(); // redirect

                                        }
                                    }

                                }

                            }


                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });

                        Follow follow = new Follow(image_url, nick_name2, last_name + first_name, 1);
                        arrayList.add(follow);
                        followLogAdapter.notifyDataSetChanged(); // redirect
                    }
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
                finish();
            }
        });



    }
}
