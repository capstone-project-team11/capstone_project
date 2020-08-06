package com.example.capsuletime.mainpages.userpage;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.CommnetLogData;
import com.example.capsuletime.Commnetwo;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.User;
import com.example.capsuletime.mainpages.mypage.CapsuleLogAdapter;
import com.example.capsuletime.mainpages.mypage.Comment;
import com.example.capsuletime.mainpages.mypage.CommentLogAdapter;
import com.example.capsuletime.mainpages.mypage.ViewPagerAdapterOnlyView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewUserCapsuleDialog {
    private Context context;
    private CapsuleLogData capsuleLogData;
    private RetrofitInterface retrofitInterface;
    private UserCapsuleLogAdapter userCapsuleLogAdapter;
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


    public ViewUserCapsuleDialog(Context context, CapsuleLogData capsuleLogData, UserCapsuleLogAdapter userCapsuleLogAdapter, int position){
        this.context = context;
        this.capsuleLogData = capsuleLogData;
        this.userCapsuleLogAdapter = userCapsuleLogAdapter;
        this.position = position;
    }


    public void call() {
        final Dialog dlg = new Dialog(context);

        //dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_view_user_capsule);
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
        TextView tv_likes = (TextView) dlg.findViewById(R.id.tv_likes);
        ImageView iv_delete = (ImageView) dlg.findViewById(R.id.btn_delete);
        ImageView imageView = (ImageView) dlg.findViewById(R.id.imageView3);

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
                    /*uri = Uri.parse(capsuleLogData.getContentList().get(i).getUrl());*/


                    Glide.with(context).load(capsuleLogData.getContentList().get(i).getUrl())
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 2)))
                            .into(imageView);

                    uri = Uri.parse(imageView.toString());
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

        String likes = String.valueOf(capsuleLogData.getLikes());

        tv_likes.setText(likes);



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
    }

}
