package com.example.capsuletime.mainpages.mypage.setting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.core.preferences.LockNickNameSharedPreferences;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.mainpages.followpage.Follow;
import com.example.capsuletime.mainpages.followpage.FollowLogAdapter;
import com.example.capsuletime.mainpages.followpage.followpage;
import com.example.capsuletime.mainpages.mypage.CustomSettingDialog;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomSettingLockDialog {
    private Context context;
    private User user;
    private String nick_name;
    private String nick_name2;
    private String lock_nick_name;
    private RetrofitInterface retrofitInterface;
    private ArrayList<Locked_Capsule> arrayList;
    private TreeSet<String> result;
    private SettingFriendLogAdapter settingFriendLogAdapter;
    private List<User> userList;
    private static final String TAG = "Setting_Lock_Capsule";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    boolean isChecked;
    private CheckBox checkBox;
    private TextView textView_Date;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

    public CustomSettingLockDialog(Context context) {
        this.context = context;
    }


    public void callFunction() {

        final Dialog dlg = new Dialog(context);

        // login_bg 투명
        //dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // login_bg 투명
        //dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // login_bg 없애기
        //dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        LockNickNameSharedPreferences lockNickNameSharedPreferences = LockNickNameSharedPreferences.getInstanceOf(context);
        ArrayList<String> lockNickNameSharedPreferencesHashSet = (ArrayList<String>) lockNickNameSharedPreferences.getHashSet(
                LockNickNameSharedPreferences.NICKNAME_SHARED_PREFERENCES_KEY2,
                new ArrayList<String>()
        );
        int count2 = 0;
        for (String nick : lockNickNameSharedPreferencesHashSet) {
            if (count2 == 0){
                lock_nick_name = nick;
            }
            count2 ++;
        }

        dlg.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);

        //R.style.AppBaseTheme
        // 액티비티의 타이틀 바를 숨김
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.custum_setting_lock);
        dlg.setCancelable(true);
        dlg.show();

        Intent intent = ((Activity) context).getIntent();
        nick_name2 = intent.getStringExtra("nick_name2");

        NickNameSharedPreferences nickNameSharedPreferences = NickNameSharedPreferences.getInstanceOf(context);
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

        recyclerView = (RecyclerView) dlg.findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        result = new TreeSet<>();
        settingFriendLogAdapter = new SettingFriendLogAdapter(arrayList, result, context);
        recyclerView.setAdapter(settingFriendLogAdapter);

        RetrofitClient retrofitClient = new RetrofitClient(dlg.getContext());
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

                        retrofitInterface.requestFollow(nick_name2).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                userList = response.body();
                                Log.d(TAG, userList.toString());
                                if (userList != null) {
                                    for (User user : userList) {
                                        String nick_name3 = user.getNick_name();

                                        if(inStr.equals(nick_name3)) {
                                            Log.d(TAG, nick_name.toString() + first_name.toString() + last_name.toString() + image_url.toString() + isChecked);
                                            Locked_Capsule locked = new Locked_Capsule(image_url, nick_name2, last_name + first_name, isChecked);
                                            arrayList.add(locked);
                                            settingFriendLogAdapter.notifyDataSetChanged(); // redirect
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });

                        /*Log.d(TAG, nick_name2.toString() + first_name.toString() + last_name.toString() + image_url.toString() + isChecked);


                        Locked_Capsule locked = new Locked_Capsule(image_url, nick_name2, last_name + first_name, isChecked);
                        arrayList.add(locked);
                        settingFriendLogAdapter.notifyDataSetChanged(); // redirect*/
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        EditText et_Date = (EditText) dlg.findViewById(R.id.Date);
        et_Date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                int year = myCalendar.get(Calendar.YEAR);
                int month = myCalendar.get(Calendar.MONTH);
                int dayofmonth = myCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        et_Date.setText(year + "월 " + month + "월 " + dayOfMonth + "일");
                    }
                }, year,month,dayofmonth);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();


            }


        });

        final EditText et_time = (EditText) dlg.findViewById(R.id.Time);
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // EditText에 출력할 형식 지정
                        et_time.setText(state + " " + selectedHour + "시 " + selectedMinute + "분");
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        Button imageButton2 = (Button) dlg.findViewById(R.id.button5);
        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.d(TAG, et_Date.getText().toString() + et_time.getText().toString()+" " + lock_nick_name);

            }
        });

    }

    private void updateLabel(View v) {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);


        EditText et_date = (EditText) v.findViewById(R.id.Date);
        et_date.setText(sdf.format(myCalendar.getTime()));

    }







}
