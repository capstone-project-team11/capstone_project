package com.example.capsuletime.mainpages.mypage;

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
import com.example.capsuletime.mainpages.mypage.dialogs.ViewCapsuleDialog;
import com.google.android.gms.common.server.converter.StringToIntConverter;;

import java.util.ArrayList;


public class CommentLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Comment> arrayList;
    private Context context;
    private static final String TAG = "CapsuleLogAdapter";
    private ViewCapsuleDialog viewCapsuleDialog;
    private CommentLogAdapter commentLogAdapter;

    public CommentLogAdapter(ArrayList<Comment> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.commentLogAdapter = this;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_log,parent,false);
            return new CommentViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_replies_log,parent,false);
            return new RepliesViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == 0){

                Glide
                        .with(context)
                        .load(arrayList.get(position).getProfile())
                        .centerCrop()
                        .into(((CommentViewHolder) holder).iv_profile);



            ((CommentViewHolder) holder).tv_nick.setText(arrayList.get(position).getNick_name());
            ((CommentViewHolder) holder).tv_comment.setText(arrayList.get(position).getComment());
            ((CommentViewHolder) holder).tv_day.setText(arrayList.get(position).getDay());




        } else {
            Glide
                    .with(context)
                    .load(arrayList.get(position).getProfile())
                    .centerCrop()
                    .into(((RepliesViewHolder) holder).iv_profile);

            ((RepliesViewHolder) holder).tv_nick.setText(arrayList.get(position).getNick_name());
            ((RepliesViewHolder) holder).tv_comment.setText(arrayList.get(position).getComment());
            ((RepliesViewHolder) holder).tv_day.setText(arrayList.get(position).getDay());

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

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_profile;
        protected TextView tv_nick;
        protected TextView tv_comment;
        protected TextView tv_day;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.tv_nick = itemView.findViewById(R.id.tv_nick);
            this.tv_comment = itemView.findViewById(R.id.tv_comment);
            this.tv_day = itemView.findViewById(R.id.tv_day);
        }
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_profile;
        protected TextView tv_nick;
        protected TextView tv_comment;
        protected TextView tv_day;

        public RepliesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.tv_nick = itemView.findViewById(R.id.tv_nick);
            this.tv_comment = itemView.findViewById(R.id.tv_comment);
            this.tv_day = itemView.findViewById(R.id.tv_day);
        }
    }


}
