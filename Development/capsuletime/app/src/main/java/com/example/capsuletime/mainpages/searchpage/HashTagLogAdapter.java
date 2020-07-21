package com.example.capsuletime.mainpages.searchpage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capsuletime.R;
import com.example.capsuletime.cap;
import com.example.capsuletime.mainpages.userpage.userpage;

import java.util.ArrayList;

;


public class HashTagLogAdapter extends RecyclerView.Adapter<HashTagLogAdapter.FolloewLogAdapter> {

    private  ArrayList<cap> arrayList;
    private Context context;
    private static final String TAG = "FollowLogAdapterPage";


    public HashTagLogAdapter(ArrayList<cap> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public FolloewLogAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hashtag_log,parent,false);

        FolloewLogAdapter holder = new FolloewLogAdapter(view);


        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull FolloewLogAdapter holder, int position) {

        /*Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .into(holder.iv_profile);*/

        /*holder.tv_nick.setText(arrayList.get(position).getNick_name());*/
        holder.tv_name.setText(arrayList.get(position).getText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), userpage.class);
                /*intent.putExtra("nick_name", arrayList.get(position).getNick_name());*/
                intent.putExtra("text", arrayList.get(position).getText());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class FolloewLogAdapter extends RecyclerView.ViewHolder {

        /*ImageView iv_profile;*/
        /*TextView tv_nick;*/
        TextView tv_name;

        public FolloewLogAdapter(@NonNull View itemView) {
            super(itemView);
            /*this.iv_profile = itemView.findViewById(R.id.iv_profile);*/
            /*this.tv_nick = itemView.findViewById(R.id.tv_nick);*/
            this.tv_name = itemView.findViewById(R.id.tv_name);
        }

    }
}
