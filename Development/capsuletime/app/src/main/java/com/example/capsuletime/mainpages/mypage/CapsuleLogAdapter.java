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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.CommnetLogData;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.User;
import com.example.capsuletime.mainpages.mypage.dialogs.ViewCapsuleDialog;
import com.example.capsuletime.mainpages.userpage.UserCapsuleLogAdapter;
;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CapsuleLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CapsuleLogData> arrayList;
    private CapsuleLogData capsuleLogData;
    private List<CommnetLogData> commentList;
    private int comment_int;
    private Context context;
    private static final String TAG = "CapsuleLogAdapter";
    private ViewCapsuleDialog viewCapsuleDialog;
    private CapsuleLogAdapter capsuleLogAdapter;
    private User user;
    private RetrofitInterface retrofitInterface;
    //private modify modifyCapsule
    //private

    public CapsuleLogAdapter(ArrayList<CapsuleLogData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.capsuleLogAdapter = this;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;




        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_log,parent,false);
            return new CapsuleViewHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_temp_log,parent,false);
            return new TempCapsuleViewHolder(view);
        } else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_lock_log,parent,false);
            return new LockCapsuleViewHolder(view);
        } else if (viewType ==3){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_lock_log2,parent,false);
            return new LockCapsuleViewHolder2(view);
        } else if (viewType ==4){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_unlock_log,parent,false);
            return new UnLockCapsuleViewHolder(view);
        }

        return null;
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
                        if(arrayList.get(position).getCapsule_id() > 0) {
                            retrofitInterface.requestComment(arrayList.get(position).getCapsule_id()).enqueue(new Callback<List<CommnetLogData>>() {
                                @Override
                                public void onResponse(Call<List<CommnetLogData>> call, Response<List<CommnetLogData>> response) {
                                    commentList = response.body();
                                    Log.d(TAG, commentList.toString());
                                    if (commentList != null) {
                                        for (CommnetLogData commnetLogData : commentList) {
                                            String comment = commnetLogData.getComment();

                                            if (comment.isEmpty()) {
                                                comment_int = 0;
                                            } else {
                                                comment_int = 1;
                                                break;
                                            }
                                            Log.d(TAG, "코멘트 : " + comment_int);

                                        }
                                    }
                                    viewCapsuleDialog = new ViewCapsuleDialog(context, arrayList.get(position), capsuleLogAdapter, position, comment_int);
                                    viewCapsuleDialog.call();
                                    comment_int = 0;
                                }

                                @Override
                                public void onFailure(Call<List<CommnetLogData>> call, Throwable t) {

                                }
                            });
                        }

                    }
                });

            ((CapsuleViewHolder) holder).iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete 통신
                    // if 문으로 재확인 구문 필요
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE: {
                                    // server 에서 delete 시
                                    retrofitInterface.requestDeleteCapsule(arrayList.get(position).getCapsule_id()).enqueue(new Callback<Success>() {
                                        @Override
                                        public void onResponse(Call<Success> call, Response<Success> response) {
                                            capsuleLogAdapter.remove(position);
                                            capsuleLogAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(Call<Success> call, Throwable t) {
                                            Toast.makeText(v.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("정말 삭제하시겠습니까?").setPositiveButton("네", dialogClickListener)
                            .setNegativeButton("아니요", dialogClickListener).show();



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
            RetrofitClient retrofitClient = new RetrofitClient(context);
            retrofitInterface = retrofitClient.retrofitInterface;

            ((TempCapsuleViewHolder) holder).iv_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete 통신
                    // if 문으로 재확인 구문 필요
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE: {
                                    // server 에서 delete 시
                                    retrofitInterface.requestDeleteCapsule(arrayList.get(position).getCapsule_id()).enqueue(new Callback<Success>() {
                                        @Override
                                        public void onResponse(Call<Success> call, Response<Success> response) {
                                            capsuleLogAdapter.remove(position);
                                            capsuleLogAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(Call<Success> call, Throwable t) {
                                            Toast.makeText(v.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("정말 삭제하시겠습니까?").setPositiveButton("네", dialogClickListener)
                            .setNegativeButton("아니요", dialogClickListener).show();



                }
            });
        } if(getItemViewType(position) == 2){
            ((LockCapsuleViewHolder) holder).tv_title.setText(arrayList.get(position).getTv_title());
            ((LockCapsuleViewHolder) holder).tv_location.setText(arrayList.get(position).getTv_location());

            ((LockCapsuleViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("잠금 캡슐 위치 확인")// TestActivity 부분에는 현재 Activity의 이름 입력.
                            .setMessage(((LockCapsuleViewHolder) holder).tv_location.getText())     // 제목 부분 (직접 작성)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                                public void onClick(DialogInterface dialog, int which){
                                    Toast.makeText(context, "위치 확인", Toast.LENGTH_SHORT).show(); // 실행할 코드
                                }
                            })
                            .show();

                }
            });




        } if(getItemViewType(position) == 3){
            ((LockCapsuleViewHolder2) holder).tv_title.setText(arrayList.get(position).getTv_title());
            ((LockCapsuleViewHolder2) holder).tv_text.setText(arrayList.get(position).getTv_text());
            ((LockCapsuleViewHolder2) holder).tv_location.setText(arrayList.get(position).getTv_location());

            ((LockCapsuleViewHolder2) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("잠금 캡슐 위치 확인")// TestActivity 부분에는 현재 Activity의 이름 입력.
                            .setMessage(((LockCapsuleViewHolder2) holder).tv_location.getText())     // 제목 부분 (직접 작성)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                                public void onClick(DialogInterface dialog, int which){
                                    Toast.makeText(context, "위치 확인", Toast.LENGTH_SHORT).show(); // 실행할 코드
                                }
                            })
                            .show();
                }
            });




        } if(getItemViewType(position) == 4){

            if (arrayList.get(position).getIv_url().length() == 10) {
                Glide
                        .with(context)
                        .load(Integer.parseInt(arrayList.get(position).getIv_url()))
                        .centerCrop()
                        .into(((UnLockCapsuleViewHolder) holder).iv_thumb);
            } else {
                Glide
                        .with(context)
                        .load(arrayList.get(position).getIv_url())
                        .centerCrop()
                        .into(((UnLockCapsuleViewHolder) holder).iv_thumb);
            }


            //holder.iv_thumb.setImageResource(arrayList.get(position).getIv_thumb());
            ((UnLockCapsuleViewHolder) holder).tv_title.setText(arrayList.get(position).getTv_title());
            //((CapsuleViewHolder) holder).tv_tags.setText(arrayList.get(position).getTv_tags());
            ((UnLockCapsuleViewHolder) holder).tv_d_day.setText(arrayList.get(position).getD_day());
            ((UnLockCapsuleViewHolder) holder).tv_location.setText(arrayList.get(position).getTv_location());
            //((CapsuleViewHolder) holder).tv_opened_date.setText(arrayList.get(position).getTv_opened_date());
            //((CapsuleViewHolder) holder).tv_created_date.setText(arrayList.get(position).getTv_created_date());

            ((UnLockCapsuleViewHolder) holder).cl_capsule.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

            //((CapsuleViewHolder) holder).cl_capsule.setBackground(ContextCompat.getDrawable(context,R.drawable.radius_capsule_log));
            //((CapsuleViewHolder) holder).cl_capsule.setBackgroundColor(Color.parseColor("#3Fa9c8fd"));

            ((UnLockCapsuleViewHolder) holder).itemView.setTag(position);
            ((UnLockCapsuleViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(arrayList.get(position).getCapsule_id() > 0) {
                        retrofitInterface.requestComment(arrayList.get(position).getCapsule_id()).enqueue(new Callback<List<CommnetLogData>>() {
                            @Override
                            public void onResponse(Call<List<CommnetLogData>> call, Response<List<CommnetLogData>> response) {
                                commentList = response.body();
                                Log.d(TAG, commentList.toString());
                                if (commentList != null) {
                                    for (CommnetLogData commnetLogData : commentList) {
                                        String comment = commnetLogData.getComment();

                                        if (comment.isEmpty()) {
                                            comment_int = 0;
                                        } else {
                                            comment_int = 1;
                                        }

                                    }
                                }
                                viewCapsuleDialog = new ViewCapsuleDialog(context, arrayList.get(position), capsuleLogAdapter, position, comment_int);
                                viewCapsuleDialog.call();
                                comment_int = 0;
                            }

                            @Override
                            public void onFailure(Call<List<CommnetLogData>> call, Throwable t) {

                            }
                        });
                    }
                }
            });
            ((UnLockCapsuleViewHolder) holder).iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete 통신
                    // if 문으로 재확인 구문 필요
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE: {
                                    // server 에서 delete 시
                                    retrofitInterface.requestDeleteCapsule(arrayList.get(position).getCapsule_id()).enqueue(new Callback<Success>() {
                                        @Override
                                        public void onResponse(Call<Success> call, Response<Success> response) {
                                            capsuleLogAdapter.remove(position);
                                            capsuleLogAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(Call<Success> call, Throwable t) {
                                            Toast.makeText(v.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("정말 삭제하시겠습니까?").setPositiveButton("네", dialogClickListener)
                            .setNegativeButton("아니요", dialogClickListener).show();



                }
            });


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

        protected ImageView iv_thumb;
        protected TextView tv_title;
        //protected TextView tv_tags;
        protected TextView tv_d_day;
        protected TextView tv_location;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        protected ConstraintLayout cl_capsule;
        protected ImageView iv_delete;

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
            this.iv_delete = (ImageView) itemView.findViewById(R.id.btn_delete);

        }
    }

    public class TempCapsuleViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_location;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        protected ConstraintLayout cl_capsule;
        protected ImageView iv_thumb;

        public TempCapsuleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            //this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            //this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);
            this.iv_thumb = (ImageView) itemView.findViewById(R.id.btn_delete);

        }
    }

    public class LockCapsuleViewHolder extends RecyclerView.ViewHolder {

        protected TextView tv_title;
        protected TextView tv_location;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        /*protected ConstraintLayout cl_capsule;*/

        public LockCapsuleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            //this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            //this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            /*this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);*/

        }
    }
    public class LockCapsuleViewHolder2 extends RecyclerView.ViewHolder {

        protected TextView tv_title;
        protected TextView tv_text;
        protected TextView tv_location;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        /*protected ConstraintLayout cl_capsule;*/

        public LockCapsuleViewHolder2(@NonNull View itemView) {
            super(itemView);

            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.tv_text = (TextView) itemView.findViewById(R.id.key);
            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            //this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            //this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            /*this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);*/

        }
    }
    public class UnLockCapsuleViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_thumb;
        protected TextView tv_title;
        //protected TextView tv_tags;
        protected TextView tv_d_day;
        protected TextView tv_location;
        //protected TextView tv_opened_date;
        //protected TextView tv_created_date;
        protected ConstraintLayout cl_capsule;
        protected ImageView iv_delete;

        public UnLockCapsuleViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_thumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            this.iv_delete = (ImageView) itemView.findViewById(R.id.btn_delete);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            //this.tv_tags = (TextView) itemView.findViewById(R.id.tv_tags);
            this.tv_d_day = (TextView) itemView.findViewById(R.id.tv_d_day);
            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            //this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            //this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);

        }
    }

}
