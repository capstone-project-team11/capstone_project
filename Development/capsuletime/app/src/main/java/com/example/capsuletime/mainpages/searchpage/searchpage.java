package com.example.capsuletime.mainpages.searchpage;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capsuletime.Capsule;
import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.FollowLogData;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Setting;
import com.example.capsuletime.User;
import com.example.capsuletime.cap;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.mainpages.ar.UnityPlayerActivity;
import com.example.capsuletime.mainpages.capsulemap.capsulemap;
import com.example.capsuletime.mainpages.followpage.Follow;
import com.example.capsuletime.mainpages.followpage.FollowLogAdapter;
import com.example.capsuletime.mainpages.mypage.CapsuleLogAdapter;
import com.example.capsuletime.mainpages.mypage.mypage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class searchpage extends AppCompatActivity {
    private User user;
    private String nick_name;
    private String hashtag;
    private RetrofitInterface retrofitInterface;
    private ArrayList<com.example.capsuletime.mainpages.searchpage.User> arrayList;
    private ArrayList<HashTag> arrayList3;
    private SearchLogAdapter searchLogAdapter;
    private HashTagLogAdapter hashTagLogAdapter;
    private List<User> userList;
    private List<cap> capList;
    private List<Follow> list;
    //private ArrayList<String> arraylist;
    private EditText editSearch;
    private static final String TAG = "SearchPage";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Follow> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchpage);

        SearchView searchView = findViewById(R.id.editSearch);

        Intent intent = getIntent();
        //hashtag = intent.getStringExtra("hashtag");

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

        if(hashtag != null){
            searchView.setQuery(hashtag,true);
            hash(hashtag);
        }

        Log.d(TAG, "서치페이지 해태 = " + hashtag);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        searchLogAdapter = new SearchLogAdapter(arrayList, this);
        recyclerView.setAdapter(searchLogAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                arrayList.clear();
                String query = searchView.getQuery().toString();
                search(query);
                return true;

            }
        });
        arrayList.clear();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set mypage Selected
        bottomNavigationView.setSelectedItemId(R.id.capsulesearch);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.mypage: {
                        Intent intent = new Intent(getApplicationContext(), mypage.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    }
                    case R.id.capsulesearch:
                        return true;

                    case R.id.capsulemap:{
                        if (Build.VERSION.SDK_INT >= 23 &&
                                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(searchpage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    0);
                            break;
                        } else if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                            Intent intent = new Intent(getApplicationContext(), capsulemap.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);

                            return true;

                        }
                    }

                    case R.id.capsulear: {

                        Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        //return true;
                    }
                }

                bottomNavigationView.setSelectedItemId(R.id.capsulesearch);
                return false;
            }
        });

    }

    private void search(String query) {

        if(query.isEmpty() || query.equals("")){arrayList.clear();}

        result(query);

        arrayList.clear();
        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        retrofitInterface.requestAllUser().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userList = response.body();
                if (userList != null) {
                    for (User user : userList) {
                        if(query.isEmpty() || query.equals("")){arrayList.clear();}
                        String nick_name = user.getNick_name();
                        String first_name = user.getFirst_name();
                        String last_name = user.getLast_name();
                        String image_url = user.getImage_url();

                        String name = last_name+first_name;

                        if(nick_name.contains(query)) {
                            if(query.isEmpty() || query.equals("")){arrayList.clear();}
                            com.example.capsuletime.mainpages.searchpage.User search = new com.example.capsuletime.mainpages.searchpage.User("",image_url, nick_name, last_name + first_name,"",0);
                            arrayList.add(search);
                            searchLogAdapter.notifyDataSetChanged(); // redirect
                        }
                        else if(name.contains(query)){
                            if(query.isEmpty() || query.equals("")){arrayList.clear();}
                            com.example.capsuletime.mainpages.searchpage.User search = new com.example.capsuletime.mainpages.searchpage.User("",image_url, nick_name, last_name + first_name,"",0);
                            arrayList.add(search);
                            searchLogAdapter.notifyDataSetChanged(); // redirect
                        }
                        }
                    }
                }


            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

    private void result(String query) {

        Log.d(TAG, "Result query = " + query);

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        Call<List<cap>> call = retrofitInterface.requestAllCapsules();

        call.enqueue(new Callback<List<cap>>() {
            @Override
            public void onResponse(Call<List<cap>> call, Response<List<cap>> response) {
                capList = response.body();
                if (call != null) {
                    for (cap user : capList) {
                        if(query.isEmpty() || query.equals("")){arrayList.clear();}
                        String text = user.getText();
                        String nick_name = user.getNick_name();

                        /*if(text != null && text.contains(query)) {
                            com.example.capsuletime.mainpages.searchpage.User hashTag2 = new com.example.capsuletime.mainpages.searchpage.User("", "", nick_name, text, "", 2);
                            arrayList.add(hashTag2);
                            searchLogAdapter.notifyDataSetChanged();
                        }*/
                        if(text != null && text.contains("#") && text.contains(query)) {
                            if(query.isEmpty() || query.equals("")){arrayList.clear();}
                            String[] hashtag2 = text.split("\\s");
                            for (int i = 0; i < hashtag2.length; i++) {
                                Log.d(TAG, "해시태그 저장 ["    +  i  + "]  = " + hashtag2[i].toString());
                                com.example.capsuletime.mainpages.searchpage.User hashTag = new com.example.capsuletime.mainpages.searchpage.User("","", "", "", hashtag2[i], 1);

                                if (hashtag2[i].contains(query)) {
                                    if(query.isEmpty() || query.equals("")){arrayList.clear();}
                                    com.example.capsuletime.mainpages.searchpage.User hashTag2 = new com.example.capsuletime.mainpages.searchpage.User("","", "", "", hashtag2[i], 1);
                                    arrayList.add(hashTag2);

                                    searchLogAdapter.notifyDataSetChanged();

                                }  /*else if (hashtag2[i].contains(hashtag))  {
                                    if(query.isEmpty() || query.equals("")){arrayList.clear();}
                                    com.example.capsuletime.mainpages.searchpage.User hashTag3 = new com.example.capsuletime.mainpages.searchpage.User("", "", nick_name,text, "", 2);
                                    arrayList.add(hashTag3);
                                    searchLogAdapter.notifyDataSetChanged();
                                }*/
                            }

                            /*com.example.capsuletime.mainpages.searchpage.User hashTag3 = new com.example.capsuletime.mainpages.searchpage.User("", "", nick_name,text, "", 2);
                            arrayList.add(hashTag3);
                            searchLogAdapter.notifyDataSetChanged();*/
                        }

                    }
                }
            }


            @Override
            public void onFailure(Call<List<cap>> call, Throwable t) {

            }
        });
    }

    private void hash(String query) {

        Log.d(TAG, "Result query = " + query);

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        Call<List<cap>> call = retrofitInterface.requestAllCapsules();

        call.enqueue(new Callback<List<cap>>() {
            @Override
            public void onResponse(Call<List<cap>> call, Response<List<cap>> response) {
                capList = response.body();
                if (call != null) {
                    for (cap user : capList) {
                        if(query.isEmpty() || query.equals("")){arrayList.clear();}
                        String text = user.getText();
                        String nick_name = user.getNick_name();

                        if(text != null && text.contains("#") && text.contains(query)) {
                            if(query.isEmpty() || query.equals("")){arrayList.clear();}
                             if (hashtag != null && query == hashtag)  {
                                    if(query.isEmpty() || query.equals("")){arrayList.clear();}
                                    com.example.capsuletime.mainpages.searchpage.User hashTag3 = new com.example.capsuletime.mainpages.searchpage.User("", "", nick_name,text, "", 2);
                                    arrayList.add(hashTag3);
                                    searchLogAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }
                }



            @Override
            public void onFailure(Call<List<cap>> call, Throwable t) {

            }
        });
    }
}


