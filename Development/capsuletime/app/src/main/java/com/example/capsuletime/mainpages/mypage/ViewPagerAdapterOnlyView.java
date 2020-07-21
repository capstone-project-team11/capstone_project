package com.example.capsuletime.mainpages.mypage;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.capsuletime.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ViewPagerAdapterOnlyView extends PagerAdapter {

    private static final String TAG = "ViewPagerAdapter";
    private Context context;
    private List<Uri> list;
    private int temp;
    private TabLayout tabLayout;

    public ViewPagerAdapterOnlyView(Context context, List<Uri> list, int temp, TabLayout tabLayout){
        this.context = context;
        this.list = list;
        this.temp = temp;
        this.tabLayout = tabLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (list.size() <= 1)
            tabLayout.setVisibility(View.GONE);
        else
            tabLayout.setVisibility(View.VISIBLE);

        View view = null;

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.image_view, container, false);
        // videoView imageView 구분 구문 필요
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_pager);
        Log.d(TAG, "glide Test");

        Glide
                .with(context)
                .load(list.get(position))
                .centerCrop()
                //.override(50,50)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
        container.addView(view);
        return view;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }



}
