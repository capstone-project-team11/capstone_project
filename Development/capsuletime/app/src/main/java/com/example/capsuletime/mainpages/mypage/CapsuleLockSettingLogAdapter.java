package com.example.capsuletime.mainpages.mypage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capsuletime.CapsuleLockSettingLogData;
import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.CommnetLogData;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.User;
import com.example.capsuletime.mainpages.mypage.dialogs.ViewCapsuleDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;


public class CapsuleLockSettingLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CapsuleLockSettingLogData> arrayList;
    private CapsuleLockSettingLogAdapter capsuleLockSettingLogAdapter;
    private Context context;
    private static final String TAG = "CapsuleLogAdapter";
    private CapsuleLockSettingLogAdapter capsuleLogAdapter;
    private RetrofitInterface retrofitInterface;
    //private modify modifyCapsule
    //private

    public CapsuleLockSettingLogAdapter(ArrayList<CapsuleLockSettingLogData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.capsuleLogAdapter = this;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;




            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_lock_setting_log,parent,false);
            return new CapsuleViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



                ((CapsuleViewHolder) holder).tv_title.setText(arrayList.get(position).getTv_title());

                if(position >= 2){
                    for(int i = 0; i < position; i++){
                        if(2/position == 0){
                            ((CapsuleViewHolder) holder).tv_bg.setImageResource(R.drawable.radius_capsule_lock_log3);
                        } else {
                            ((CapsuleViewHolder) holder).tv_bg.setImageResource(R.drawable.radius_capsule_lock_log2);
                        }

                    }

                }

        }




    @Override
    public int getItemViewType(int position) {
        return this.arrayList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public class CapsuleViewHolder extends RecyclerView.ViewHolder {


        protected TextView tv_title;
        protected ImageView tv_bg;



        public CapsuleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.tv_bg = (ImageView) itemView.findViewById(R.id.lock);



        }
    }







}
