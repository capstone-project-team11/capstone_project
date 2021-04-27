package com.example.capsuletime.mainpages.searchpage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.R;
import com.example.capsuletime.mainpages.mypage.Comment;
import com.example.capsuletime.mainpages.mypage.dialogs.ViewCapsuleDialog;
import com.example.capsuletime.mainpages.userpage.userpage;
import com.google.android.gms.common.server.converter.StringToIntConverter;;

import java.util.ArrayList;


public class SearchLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<User> arrayList;
    private Context context;
    private static final String TAG = "CapsuleLogAdapter";
    private ViewCapsuleDialog viewCapsuleDialog;
    private SearchLogAdapter searchLogAdapter;

    public SearchLogAdapter(ArrayList<User> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.searchLogAdapter = this;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_log,parent,false);
            return new SearchUserViewHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hashtag_log,parent,false);
            return new HashTagViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_log,parent,false);
            return new ContentViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == 0){

            Glide
                    .with(context)
                    .load(arrayList.get(position).getProfile())
                    .centerCrop()
                    .into(((SearchUserViewHolder) holder).iv_profile);



            ((SearchUserViewHolder) holder).tv_nick.setText(arrayList.get(position).getNick_name());
            ((SearchUserViewHolder) holder).tv_name.setText(arrayList.get(position).getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), userpage.class);
                    intent.putExtra("nick_name2", arrayList.get(position).getNick_name());
                    intent.putExtra("user_id", arrayList.get(position).getUser_id());
                    context.startActivity(intent);
                }
            });




        } else if (getItemViewType(position) == 1) {

            ((HashTagViewHolder) holder).tv_hashtag.setText(arrayList.get(position).getHashtag());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), searchpage.class);
                    intent.putExtra("hashtag", arrayList.get(position).getHashtag());
                    Log.d(TAG,"해시태그 = " + arrayList.get(position).getHashtag());
                    context.startActivity(intent);
                }
            });

        } else {
            /*Glide
                    .with(context)
                    .load(arrayList.get(position).getProfile())
                    .centerCrop()
                    .into(((SearchUserViewHolder) holder).iv_profile);*/



            ((ContentViewHolder) holder).tv_nick.setText(arrayList.get(position).getNick_name());
            ((ContentViewHolder) holder).tv_text.setText(arrayList.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), userpage.class);
                    intent.putExtra("nick_name", arrayList.get(position).getNick_name());
                    intent.putExtra("user_id", arrayList.get(position).getUser_id());
                    context.startActivity(intent);
                }
            });


        }


    }

    @Override
    public int getItemViewType(int position) {
        return this.arrayList.get(position).getViewtype();
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class SearchUserViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_profile;
        protected TextView tv_nick;
        protected TextView tv_name;


        public SearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.tv_nick = itemView.findViewById(R.id.tv_nick);
            this.tv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {

        /*protected ImageView iv_profile;*/
        protected TextView tv_nick;
        protected TextView tv_text;


        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            /*this.iv_profile = itemView.findViewById(R.id.iv_profile);*/
            this.tv_nick = itemView.findViewById(R.id.tv_nick);
            this.tv_text = itemView.findViewById(R.id.tv_name);
        }
    }

    public class HashTagViewHolder extends RecyclerView.ViewHolder {


        protected TextView tv_hashtag;

        public HashTagViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_hashtag = itemView.findViewById(R.id.tv_hashtag);
        }
    }


}
