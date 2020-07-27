package com.example.capsuletime.mainpages.mypage.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.CommnetLogData;
import com.example.capsuletime.Commnetwo;
import com.example.capsuletime.User;
import com.example.capsuletime.mainpages.followpage.Follow;
import com.example.capsuletime.mainpages.mypage.CapsuleLogAdapter;

import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.mainpages.mypage.Comment;
import com.example.capsuletime.mainpages.mypage.CommentLogAdapter;
import com.example.capsuletime.mainpages.mypage.ViewPagerAdapterOnlyView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCapsuleDialog {
    private Context context;
    private CapsuleLogData capsuleLogData;
    private RetrofitInterface retrofitInterface;
    private CapsuleLogAdapter capsuleLogAdapter;
    private List<Uri> listUri;
    private int position;
    private ArrayList<Comment> arrayList2;
    private CommentLogAdapter commentLogAdapter;
    private List<User> userList;

    private CommnetLogData commnetLogData;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<CommnetLogData> commentList;
    private static final String TAG = "ViewCapsulePage";


    public ViewCapsuleDialog (Context context, CapsuleLogData capsuleLogData, CapsuleLogAdapter capsuleLogAdapter, int position){
        this.context = context;
        this.capsuleLogData = capsuleLogData;
        this.capsuleLogAdapter = capsuleLogAdapter;
        this.position = position;
    }

    public void call() {
        final Dialog dlg = new Dialog(context);

        //dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_view_capsule);
        dlg.setCancelable(true);

        recyclerView = (RecyclerView)dlg.findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList2 = new ArrayList<>();
        commentLogAdapter = new CommentLogAdapter(arrayList2,context);
        recyclerView.setAdapter(commentLogAdapter);

        dlg.show();
        RetrofitClient retrofitClient = new RetrofitClient(context);
        retrofitInterface = retrofitClient.retrofitInterface;

        TextView tv_title = (TextView) dlg.findViewById(R.id.tv_title);
        TextView tv_location = (TextView) dlg.findViewById(R.id.tv_location);
        TextView tv_text = (TextView) dlg.findViewById(R.id.tv_text);
        TextView tv_d_day = (TextView) dlg.findViewById(R.id.tv_d_day);
        ImageView iv_delete = (ImageView) dlg.findViewById(R.id.btn_delete);

        ViewPager viewPager = (ViewPager) dlg.findViewById(R.id.const_vp);
        TabLayout tabLayout = (TabLayout) dlg.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        listUri = new ArrayList<>();

        if (capsuleLogData.getContentList() != null){
            Log.d("list", capsuleLogData.getContentList().toString());
            Uri uri;

            if (capsuleLogData.getContentList().size() == 0){

                uri = Uri.parse("android.resource://com.example.capsuletime/drawable/capsule_marker_yellow");
                listUri.add(uri);
            } else {
                for(int i = 0; i < capsuleLogData.getContentList().size(); i++){
                    uri = Uri.parse(capsuleLogData.getContentList().get(i).getUrl());
                    listUri.add(uri);
                }
            }

        } else {
            String drawablePath = "res:///" + R.drawable.capsule_temp;
            Uri uri = Uri.parse(drawablePath);
            listUri.add(uri);
        }

        ViewPagerAdapterOnlyView viewPagerAdapterOnlyView = new ViewPagerAdapterOnlyView(context,listUri, 0, tabLayout);
        viewPager.setAdapter(viewPagerAdapterOnlyView);

        tv_title.setText(capsuleLogData.getTv_title());
        tv_location.setText(capsuleLogData.getTv_location());
        tv_text.setText(capsuleLogData.getTv_text() != null ? capsuleLogData.getTv_text() : "");
        tv_d_day.setText(capsuleLogData.getD_day());



        retrofitInterface.requestComment(capsuleLogData.getCapsule_id()).enqueue(new Callback<List<CommnetLogData>>() {
            @Override
            public void onResponse(Call<List<CommnetLogData>> call, Response<List<CommnetLogData>> response) {
                commentList = response.body();
                Log.d(TAG, commentList.toString());
                if (commentList != null) {
                    for (CommnetLogData commnetLogData : commentList) {
                        List<Commnetwo> replyies = commnetLogData.getReplies();
                        String nick_name = commnetLogData.getNick_name();
                        String comment = commnetLogData.getComment();
                        String profile = commnetLogData.getUser_image_url();

                        if(replyies.isEmpty()){

                            Comment commnet = new Comment(profile,nick_name,comment, replyies,0);
                            arrayList2.add(commnet);

                        }else{

                            Comment commnet2 = new Comment(profile,nick_name,comment, replyies,0);
                            arrayList2.add(commnet2);

                            for(int j = 0; j < replyies.size(); j++) {
                                String rp_nick_name = replyies.get(j).getNick_name();
                                String rp_comment = replyies.get(j).getComment();
                                String rp_profile = replyies.get(j).getUser_image_url();
                                Comment comment3 = new Comment(rp_profile,rp_nick_name,rp_comment,commnetLogData.getReplies(),1);
                                arrayList2.add(comment3);
                            }
                        }
                    }
                    commentLogAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<CommnetLogData>> call, Throwable t) {

            }
        });

        iv_delete.setOnClickListener(new View.OnClickListener() {
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
                                retrofitInterface.requestDeleteCapsule(capsuleLogData.getCapsule_id()).enqueue(new Callback<Success>() {
                                    @Override
                                    public void onResponse(Call<Success> call, Response<Success> response) {
                                        capsuleLogAdapter.remove(position);
                                        capsuleLogAdapter.notifyDataSetChanged();
                                        dlg.dismiss();
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
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();



            }
        });
    }

}
