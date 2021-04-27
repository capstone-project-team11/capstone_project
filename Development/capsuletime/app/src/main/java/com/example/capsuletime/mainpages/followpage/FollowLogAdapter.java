package com.example.capsuletime.mainpages.followpage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.mainpages.userpage.userpage;

import java.util.ArrayList;
import java.util.HashSet;

;import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FollowLogAdapter extends RecyclerView.Adapter<FollowLogAdapter.FolloewLogAdapter> {

    private  ArrayList<Follow> arrayList;
    private Context context;
    private static final String TAG = "FollowLogAdapterPage";
    private RetrofitInterface retrofitInterface;
    private String nick_name;


    public FollowLogAdapter(ArrayList<Follow> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public FolloewLogAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_log,parent,false);

        FolloewLogAdapter holder = new FolloewLogAdapter(view);


        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull FolloewLogAdapter holder, int position) {

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

        RetrofitClient retrofitClient = new RetrofitClient(context);
        retrofitInterface = retrofitClient.retrofitInterface;

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .into(holder.iv_profile);

        holder.tv_nick.setText(arrayList.get(position).getNick_name());
        holder.tv_name.setText(arrayList.get(position).getName());

        if(arrayList.get(position).getStatus_follow() == 0) {
            Glide.with(holder.itemView)
                    .load(R.drawable.follow_icon2)
                    .into(holder.iv_follow);
        } else {
            Glide.with(holder.itemView)
                    .load(R.drawable.follow_icon1)
                    .into(holder.iv_follow);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), userpage.class);
                intent.putExtra("nick_name2", arrayList.get(position).getNick_name());
                intent.putExtra("user_id", arrayList.get(position).getName());
                context.startActivity(intent);
            }
        });

        holder.iv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.get(position).getStatus_follow() == 0){
                    Log.d(TAG,nick_name.toString());
                    retrofitInterface.requestDeleteFollow(nick_name,arrayList.get(position).getNick_name()).enqueue(new Callback<Success>() {
                        @Override
                        public void onResponse(Call<Success> call, Response<Success> response) {
                            holder.iv_follow.setImageResource(R.drawable.follow_icon1);
                            arrayList.get(position).setStatus_follow(1);



                            /*Glide.with(holder.itemView)
                                    .load(R.drawable.follow_icon1)
                                    .into(holder.iv_follow);*/
                        }

                        @Override
                        public void onFailure(Call<Success> call, Throwable t) {
                            Toast.makeText(v.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if(arrayList.get(position).getStatus_follow() == 1){
                    retrofitInterface.requestCreateFollow(nick_name,arrayList.get(position).getNick_name()).enqueue(new Callback<Success>() {
                        @Override
                        public void onResponse(Call<Success> call, Response<Success> response) {
                            holder.iv_follow.setImageResource(R.drawable.follow_icon2);
                            arrayList.get(position).setStatus_follow(0);
                            /*Glide.with(holder.itemView)
                                    .load(R.drawable.follow_icon2)
                                    .into(holder.iv_follow);*/
                        }

                        @Override
                        public void onFailure(Call<Success> call, Throwable t) {
                            Toast.makeText(v.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class FolloewLogAdapter extends RecyclerView.ViewHolder {

        ImageView iv_profile;
        TextView tv_nick;
        TextView tv_name;
        ImageView iv_follow;

        public FolloewLogAdapter(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.tv_nick = itemView.findViewById(R.id.tv_nick);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.iv_follow = itemView.findViewById(R.id.follow);
        }

    }
}
