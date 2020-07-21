package com.example.capsuletime.mainpages.mypage;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.capsuletime.mainpages.mypage.ModifyCapsule;
import com.example.capsuletime.R;

public class CustomDialogMypage {
    private Context context;

    public CustomDialogMypage(Context context){
        this.context = context;
    }

    @SuppressLint("PrivateResource")
    public void callFunction() {

        final Dialog dlg = new Dialog(context);
        // login_bg 투명
        //dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // login_bg 투명
        //dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // login_bg 없애기
        //dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dlg.getWindow().setGravity(Gravity.BOTTOM);

        //R.style.AppBaseTheme
        // 액티비티의 타이틀 바를 숨김
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.custom_dialong_mypage);
        dlg.setCancelable(true);
        dlg.show();

        ConstraintLayout cl_camera = (ConstraintLayout) dlg.findViewById(R.id.camera);
        ConstraintLayout cl_gallery = (ConstraintLayout) dlg.findViewById(R.id.gallery);
        ConstraintLayout cl_outside = (ConstraintLayout) dlg.findViewById(R.id.outside);

        cl_camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((ModifyCapsule) context).doTakePhotoAction();
                dlg.dismiss();
            }
        });

        cl_gallery.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ((ModifyCapsule) context).doTakeAlbumAction();
                dlg.dismiss();
            }
        });
        cl_outside.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }


}