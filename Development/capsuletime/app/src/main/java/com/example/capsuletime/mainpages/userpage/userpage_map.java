package com.example.capsuletime.mainpages.userpage;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.capsuletime.Capsule;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.login.login;
import com.example.capsuletime.mainpages.ar.UnityPlayerActivity;
import com.example.capsuletime.mainpages.capsulemap.PopUpActivity;
import com.example.capsuletime.mainpages.followpage.followerpage;
import com.example.capsuletime.mainpages.followpage.followpage;
import com.example.capsuletime.mainpages.mypage.mypage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class userpage_map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "capsulemap";
    private String user_id;
    private String nick_name;
    private User user;
    private RetrofitInterface retrofitInterface;
    private List<Capsule> capsuleList;
    private String drawablePath;
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private double cur_lng;
    private double cur_lat;
    private Marker curMarker;
    private boolean firstFlag = false;
    private List<Integer> capsuleMarkerImageIdList;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_user_page_map);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        user_id = intent.getStringExtra("user_id");
        nick_name = intent.getStringExtra("nick_name");

        ImageView iv_user = (ImageView) this.findViewById(R.id.user_image);
        TextView tv_id = (TextView) this.findViewById(R.id.tv_nick);

        if(user_id != null)
            tv_id.setText(user_id);

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        initCapsuleMarkerImageIdList();

        iv_user.setImageResource(R.drawable.user);

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(user == null) {
            retrofitInterface.requestSearchUser(nick_name).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if (response.code() == 401) {
                        Intent intent = new Intent(getApplicationContext(), login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    user = response.body();
                    if (user != null) {
                        if (user.getImage_url() == null || Objects.equals(user.getImage_url(), "")) {
                            Log.d(TAG, "url null");
                            iv_user.setImageResource(R.drawable.user);
                        } else {
                            Log.d(TAG, "url not null");
                            Glide
                                    .with(getApplicationContext())
                                    .load(user.getImage_url())
                                    .into(iv_user);
                        }

                        tv_id.setText(user.getUser_id());
                    } else {
                        iv_user.setImageResource(R.drawable.user);
                        tv_id.setText("서버통신오류");
                    }
                }


                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, "server-get-user fail");
                }
            });


        }else {
                if(user.getImage_url() == null || Objects.equals(user.getImage_url(), "")){
                    Log.d(TAG,"user not null url null");
                    iv_user.setImageResource(R.drawable.user);
                } else {
                    Log.d(TAG,"user not null url not null");
                    Glide
                            .with(getApplicationContext())
                            .load(user.getImage_url())
                            .into(iv_user);
                }
                tv_id.setText(user.getUser_id());
        }

        Button imageButton = (Button) findViewById(R.id.button);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), userpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user_id", user_id);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        Button imageButton2 = (Button) findViewById(R.id.button5);
        imageButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button imageButton3 = (Button) findViewById(R.id.button6);
        imageButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followerpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button imageButton4 = (Button) findViewById(R.id.button4);
        imageButton4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            return;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, LocationListener);

        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

        } else {

            latitude = 37.52487;
            longitude = 126.92723;
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거됨", Toast.LENGTH_SHORT).show();
        }
    };

    final android.location.LocationListener LocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //double altitude = location.getAltitude();
            Log.d(TAG, "좌표 ->" + longitude + latitude);


            userCurrentMark();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public void userCurrentMark() {

        if (curMarker != null){
            Log.d("TAG","curMarker remove");
            curMarker.remove();
        }

        LatLng SEOUL = new LatLng(latitude, longitude);
        cur_lat = latitude;
        cur_lng = longitude;

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(SEOUL);
        markerOptions2.title("user");
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.cur_user);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
        markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        if (firstFlag == false){
            mMap.animateCamera(CameraUpdateFactory.newLatLng(SEOUL));
            firstFlag = true;
        }

        curMarker = mMap.addMarker(markerOptions2);
        Log.d(TAG,curMarker.toString());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        drawablePath = "res:///" + R.drawable.capsule_temp;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        String inStr = (nick_name != null) ? nick_name : user.getNick_name();
        if (inStr != null)
        retrofitInterface.requestSearchUserNick(inStr).enqueue(new Callback<List<Capsule>>() {
            @Override
            public void onResponse(Call<List<Capsule>> call, Response<List<Capsule>> response) {

                if (response.code() == 401) {
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                capsuleList = response.body();
                Log.d(TAG, response.body().toString() + "curMarkerAdd");

                for (int i = 0; i < capsuleList.size(); i++) {
                    Log.d(TAG, capsuleList.get(i).toString());
                }

                setUpCapsulesOnMap();
            }

            @Override
            public void onFailure(Call<List<Capsule>> call, Throwable t) {

            }
        });

    }
    private void setUpCapsulesOnMap() {
        //mMap.clear();
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String location = "Default";
        List<Capsule> capsules = capsuleList;
        Log.i(TAG, "캡슐 정보 " + capsules);
        int idCount = 0;
        for (int i = 0; i < capsules.size(); i++) {
            Log.i(TAG, "start() ");
            if (capsules.get(i).getStatus_temp() == 1 ){
                continue;
            }

            LatLng latLng = new LatLng(capsules.get(i).getLat(), capsules.get(i).getLng());

            Log.d(TAG, latLng.toString());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            try {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(3);
                double lat = Double.parseDouble(df.format(capsules.get(i).getLat()));
                double lng = Double.parseDouble(df.format(capsules.get(i).getLng()));

                List<Address> list = geocoder.getFromLocation(lat, lng, 3);
                if (list != null) {
                    if (list.size() == 0){
                        // no
                    } else {
                        location = list.get(0).getAddressLine(0);
                        Log.d(TAG, "location good");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            markerOptions.title(location);
            //markerOptions.snippet(capsules.get(i).getCapsule_id());

            Log.d(TAG, markerOptions.getTitle());


            if(capsuleMarkerImageIdList.size() <= idCount)
                idCount = 0;

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(capsuleMarkerImageIdList.get(idCount));
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            Marker marker = mMap.addMarker(markerOptions);

            idCount++;
        }
        mMap.setOnMarkerClickListener(this);
        //OnclickMarker();
        userCurrentMark();

    }

    /*
    private void OnclickMarker () {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커 클릭시 호출되는 콜백 메서드

                Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
                intent.putExtra("user_id",marker.getTitle());
                startActivityForResult(intent, 1);

                //Toast.makeText(getApplicationContext(),
                //" 클릭했음" + capsuleList.get(1)
                //, Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }
    */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getId().equals(curMarker.getId())) {

            return false;
        } else {

            return false;
        }

    }

    public void initCapsuleMarkerImageIdList() {
        capsuleMarkerImageIdList = new ArrayList<>();

        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_angry);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_blue);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_gray);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_green);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_mustard);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_pupple);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_rainbow);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_red);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_stone);
        capsuleMarkerImageIdList.add(R.drawable.capsule_marker_yellow);
    }
}




