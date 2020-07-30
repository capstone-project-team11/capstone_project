package com.example.capsuletime.mainpages.userpage;

import android.content.Context;
import android.content.Intent;
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
import com.example.capsuletime.User;
import com.example.capsuletime.mainpages.mypage.ModifyCapsule;
import com.example.capsuletime.mainpages.mypage.dialogs.ViewCapsuleDialog;

import java.util.ArrayList;

;


public class UserCapsuleLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CapsuleLogData> arrayList;
    private Context context;
    private static final String TAG = "CapsuleLogAdapter";
    private ViewUserCapsuleDialog viewUserCapsuleDialog;
    private UserCapsuleLogAdapter userCapsuleLogAdapter;
    private User user;
    //private modify modifyCapsule
    //private

    public UserCapsuleLogAdapter(ArrayList<CapsuleLogData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.userCapsuleLogAdapter = this;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        int position ;


        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_log,parent,false);
            return new CapsuleViewHolder(view);
            /*if(arrayList.get(position).getStatus_lock() == 1 ){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_lock_log,parent,false);
                return new LockCapsuleViewHolder(view);
            }*/
        } else /*if (viewType == 1)*/ { // (viewType == 1)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_temp_log,parent,false);
            return new TempCapsuleViewHolder(view);
        } /*else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_lock_log,parent,false);
            return new LockCapsuleViewHolder(view);
        }*/

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if(getItemViewType(position) == 0){

                if (arrayList.get(position).getIv_url().length() == 10) {
                    Glide
                            .with(context)
                            .load(Integer.parseInt(arrayList.get(position).getIv_url()))
                            .centerCrop()
                            .into(((CapsuleViewHolder) holder).iv_thumb);
                } else {
                    Glide
                            .with(context)
                            .load(arrayList.get(position).getIv_url())
                            .centerCrop()
                            .into(((CapsuleViewHolder) holder).iv_thumb);
                }


                //holder.iv_thumb.setImageResource(arrayList.get(position).getIv_thumb());
                ((CapsuleViewHolder) holder).tv_title.setText(arrayList.get(position).getTv_title());
                //((CapsuleViewHolder) holder).tv_tags.setText(arrayList.get(position).getTv_tags());
                ((CapsuleViewHolder) holder).tv_d_day.setText(arrayList.get(position).getD_day());
                ((CapsuleViewHolder) holder).tv_location.setText(arrayList.get(position).getTv_location());
                //((CapsuleViewHolder) holder).tv_opened_date.setText(arrayList.get(position).getTv_opened_date());
                //((CapsuleViewHolder) holder).tv_created_date.setText(arrayList.get(position).getTv_created_date());

                ((CapsuleViewHolder) holder).cl_capsule.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

                //((CapsuleViewHolder) holder).cl_capsule.setBackground(ContextCompat.getDrawable(context,R.drawable.radius_capsule_log));
                //((CapsuleViewHolder) holder).cl_capsule.setBackgroundColor(Color.parseColor("#3Fa9c8fd"));

                ((CapsuleViewHolder) holder).itemView.setTag(position);
                ((CapsuleViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ModifyCapsule.class);
                        intent.putExtra("user", user);
                        String curName = ((CapsuleViewHolder) holder).tv_title.getText().toString();
                        //Toast.makeText(v.getContext(), curName, Toast.LENGTH_SHORT).show();
                        viewUserCapsuleDialog = new ViewUserCapsuleDialog(context, arrayList.get(position), userCapsuleLogAdapter, position);
                        viewUserCapsuleDialog.call();
                    }
                });
            /*if (arrayList.get(position).getStatus_lock() == 1) {
                ;
            }*/


        } if(getItemViewType(position) == 1){
            ((TempCapsuleViewHolder) holder).tv_location.setText(arrayList.get(position).getTv_location());
            //((TempCapsuleViewHolder) holder).tv_opened_date.setText(arrayList.get(position).getTv_opened_date());
            //((TempCapsuleViewHolder) holder).tv_created_date.setText(arrayList.get(position).getTv_created_date());

            ((TempCapsuleViewHolder) holder).cl_capsule.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

            //((TempCapsuleViewHolder) holder).cl_capsule.setBackground(ContextCompat.getDrawable(context,R.drawable.radius_capsule_log));

            /*
            ((TempCapsuleViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String curName = "Temp!";
                    Toast.makeText(v.getContext(), curName, Toast.LENGTH_SHORT).show();

                }
            });
            */
            ((TempCapsuleViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ModifyCapsule.class);
                    intent.putExtra("capsule_id", arrayList.get(position).getCapsule_id());
                    intent.putExtra("user_id", arrayList.get(position).getUser_id());
                    intent.putExtra("user", user);
                    v.getContext().startActivity(intent);

                    //remove(((TempCapsuleViewHolder) holder).getAdapterPosition());
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        return this.arrayList.get(position).getState_temp();
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

        protected ImageView iv_thumb;
        protected TextView tv_title;
        //protected TextView tv_tags;
        protected TextView tv_d_day;
        protected TextView tv_location;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        protected ConstraintLayout cl_capsule;

        public CapsuleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_thumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            //this.tv_tags = (TextView) itemView.findViewById(R.id.tv_tags);
            this.tv_d_day = (TextView) itemView.findViewById(R.id.tv_d_day);
            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            //this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            //this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);

        }
    }

    public class TempCapsuleViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_location;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        protected ConstraintLayout cl_capsule;

        public TempCapsuleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            //this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            //this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);

        }
    }

    public class LockCapsuleViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        /*protected ConstraintLayout cl_capsule;*/

        public LockCapsuleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            //this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            //this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            /*this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);*/

        }
    }


}
