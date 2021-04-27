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
import android.view.ViewGroup;
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
import com.example.capsuletime.CapsuleOneOfAll;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.User;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.login.login;
import com.example.capsuletime.mainpages.ar.UnityPlayerActivity;
import com.example.capsuletime.mainpages.capsulemap.CapsuleMark;
import com.example.capsuletime.mainpages.capsulemap.PopUpActivity;
import com.example.capsuletime.mainpages.followpage.followerpage;
import com.example.capsuletime.mainpages.followpage.followpage;
import com.example.capsuletime.mainpages.mypage.mypage;
import com.example.capsuletime.mainpages.mypage.mypage_map;
import com.example.capsuletime.mainpages.mypage.setting.settingpage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
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
    private String nick_name2;
    private User user;
    private int follow_status;
    private RetrofitInterface retrofitInterface;
    private List<Capsule> capsuleList;
    private List<User> userList;
    private String drawablePath;
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private double cur_lng;
    private double cur_lat;
    private ClusterManager<CapsuleMark> clusterManager;
    private Marker curMarker;
    private boolean firstFlag = false;
    private List<Integer> capsuleMarkerImageIdList;
    private int idCount;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_user_page_map);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        user_id = intent.getStringExtra("user_id");
        nick_name2 = intent.getStringExtra("nick_name2");

        idCount = 0;
        ImageView iv_user = (ImageView) this.findViewById(R.id.user_image);
        TextView tv_id = (TextView) this.findViewById(R.id.tv_nick);

        TextView follow = (TextView) this.findViewById(R.id.follow2);
        TextView follower = (TextView) this.findViewById(R.id.follower2);

        ImageView imageView1 = (ImageView) findViewById(R.id.setting);
        imageView1.setImageResource(R.drawable.userpage_follow_botton);

        NickNameSharedPreferences nickNameSharedPreferences = NickNameSharedPreferences.getInstanceOf(getApplicationContext());
        HashSet<String> nickNameSharedPrefer = (HashSet<String>) nickNameSharedPreferences.getHashSet(
                NickNameSharedPreferences.NICKNAME_SHARED_PREFERENCES_KEY,
                new HashSet<String>()
        );
        int count = 0;
        for (String nick : nickNameSharedPrefer) {
            if (count == 0){
                nick_name = nick;
            }
            count ++;
        }

        if(nick_name2.equals(nick_name)){
            imageView1.setVisibility(View.INVISIBLE);
        } else {
            imageView1.setImageResource(R.drawable.userpage_follow_botton);
        }

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
            retrofitInterface.requestSearchUser(nick_name2).enqueue(new Callback<User>() {
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
                                    .circleCrop()
                                    .into(iv_user);
                        }

                        tv_id.setText(user.getNick_name());
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

        ImageView imageButton = (ImageView) findViewById(R.id.capsule);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), userpage.class);
                intent.putExtra("nick_name2", user.getNick_name());
                Log.d(TAG, user.toString());
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        TextView imageButton10 = (TextView) findViewById(R.id.follow);
        imageButton10.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user_id", user_id);
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        TextView imageButton3 = (TextView) findViewById(R.id.follower);
        imageButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), followerpage.class);
                intent.putExtra("nick_name", user.getNick_name());
                intent.putExtra("user_id", user_id);
                intent.putExtra("user", user);
                Log.d(TAG, user.toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });



        Button imageButton5 = (Button) findViewById(R.id.button4);
        imageButton5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if(nick_name2 != null){
            tv_id.setText(nick_name2);

            retrofitInterface.requestFollow(nick_name2).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    userList = response.body();
                    Log.d(TAG, userList.toString());
                    if (userList != null) {
                        for (User user : userList) {
                            String nick_name3 = user.getNick_name();
                            Log.d(TAG,"닉넴 : " + nick_name3.toString());
                            if(nick_name.equals(nick_name3)) {
                                ImageView imageView1 = (ImageView) findViewById(R.id.setting);
                                imageView1.setImageResource(R.drawable.userpage_follow_botton2);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });

            ImageView imageButton2 = (ImageView) findViewById(R.id.setting);
            imageButton2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String inStr = (nick_name != null) ? nick_name : user.getNick_name();
                    retrofitInterface.requestFollow(inStr).enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            userList = response.body();
                            Log.d(TAG, userList.toString());
                            if (userList != null) {
                                for (User user : userList) {
                                    String nick_name3 = user.getNick_name();
                                    if(nick_name2.equals(nick_name3)){
                                        follow_status = 1;
                                    } else {
                                        follow_status = 0;
                                    }
                                    if(follow_status == 1) {
                                        retrofitInterface.requestDeleteFollow(nick_name,nick_name2).enqueue(new Callback<Success>() {
                                            @Override
                                            public void onResponse(Call<Success> call, Response<Success> response) {
                                                ImageView imageView1 = (ImageView) findViewById(R.id.setting);
                                                imageView1.setImageResource(R.drawable.userpage_follow_botton);
                                                retrofitInterface.requestFollower(nick_name2).enqueue(new Callback<List<User>>() {
                                                    @Override
                                                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                                        userList = response.body();
                                                        Log.d(TAG, userList.toString());
                                                        if (userList != null) {
                                                            int count = 0;
                                                            for (User user : userList) {
                                                                count++;
                                                            }
                                                            follow_status = 0;
                                                            String count_follow = String.valueOf(count);
                                                            follower.setText(count_follow);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<List<User>> call, Throwable t) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<Success> call, Throwable t) {
                                                Toast.makeText(view.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        retrofitInterface.requestCreateFollow(nick_name,nick_name2).enqueue(new Callback<Success>() {
                                            @Override
                                            public void onResponse(Call<Success> call, Response<Success> response) {
                                                ImageView imageView1 = (ImageView) findViewById(R.id.setting);
                                                imageView1.setImageResource(R.drawable.userpage_follow_botton2);
                                                retrofitInterface.requestFollower(nick_name2).enqueue(new Callback<List<User>>() {
                                                    @Override
                                                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                                        userList = response.body();
                                                        Log.d(TAG, userList.toString());
                                                        if (userList != null) {
                                                            int count = 0;
                                                            for (User user : userList) {
                                                                count++;
                                                            }
                                                            follow_status = 1;
                                                            String count_follow = String.valueOf(count);
                                                            follower.setText(count_follow);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<List<User>> call, Throwable t) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<Success> call, Throwable t) {
                                                Toast.makeText(view.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {

                        }
                    });
                }
            });

            retrofitInterface.requestSearchUser(nick_name2).enqueue(new Callback<User>() {
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
                        if(user.getImage_url() == null || Objects.equals(user.getImage_url(), "")){
                            Log.d(TAG,"url null");
                            iv_user.setImageResource(R.drawable.user);
                        } else {
                            Log.d(TAG,"url not null");
                            Glide
                                    .with(getApplicationContext())
                                    .load(user.getImage_url())
                                    .circleCrop()
                                    .into(iv_user);
                        }
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
            retrofitInterface.requestFollow(nick_name2).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    userList = response.body();
                    Log.d(TAG, userList.toString());
                    if (userList != null) {
                        int count = 0;
                        for (User user : userList) {
                            count++;
                        }
                        String count_follow = String.valueOf(count);
                        follow.setText(count_follow);
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
            retrofitInterface.requestFollower(nick_name2).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    userList = response.body();
                    Log.d(TAG, userList.toString());
                    if (userList != null) {
                        int count = 0;
                        for (User user : userList) {
                            count++;
                        }
                        String count_follow = String.valueOf(count);
                        follower.setText(count_follow);
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
        }



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

        clusterManager = new ClusterManager<CapsuleMark>(this, mMap);


        /* 맵이 로드될때 카메라 초기화*/
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                moveToCurrentPosition();
            }
        });

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        drawablePath = "res:///" + R.drawable.capsule_temp;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        String inStr = (nick_name2 != null) ? nick_name2 : user.getNick_name();
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
                addItems();
            }

            @Override
            public void onFailure(Call<List<Capsule>> call, Throwable t) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        setClusterManager();
    }
    private void moveToCurrentPosition() {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
    }

    private void addItems() {
        //mMap.clear();
        for (Capsule item : capsuleList) {
            if (item.getStatus_temp() == 1 || item.getStatus_lock() == 1)
                continue ;
            CapsuleOneOfAll capsule = new CapsuleOneOfAll(item.getCapsule_id(), item.getUser_id(), item.getNick_name(), item.getTitle(),
                    item.getText(), item.getViews(), item.getLikes(), item.getDate_created(),  item.getDate_opened(),
                    item.getStatus_temp(), item.getLat(), item.getLng(), item.getStatus_lock());
            CapsuleMark capsuleMark = new CapsuleMark(capsule);
            clusterManager.addItem(capsuleMark);
        }
        userCurrentMark();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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
    private void setClusterManager(){

        clusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, clusterManager));
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<CapsuleMark>() {
            @Override
            public boolean onClusterClick(Cluster<CapsuleMark> cluster) {
                LatLngBounds.Builder builder_c = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder_c.include(item.getPosition());
                }
                LatLngBounds bounds_c = builder_c.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds_c, 9));
                float zoom = mMap.getCameraPosition().zoom - 0.5f;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
                return false;
            }
        });
    }

    class MarkerClusterRenderer extends DefaultClusterRenderer<CapsuleMark> {
        private static final int MARKER_DIMENSION_X = 70; // Setting the constant value for the single marker size.
        private static final int MARKER_DIMENSION_Y = 70;

        private final IconGenerator iconGenerator;
        private final ImageView markerImageView;
        private final Geocoder geocoder;

        public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<CapsuleMark> clusterManager) {
            super(context, map, clusterManager);
            iconGenerator = new IconGenerator(context);
            //iconGenerator.setBackground(getResources().getDrawable(R.drawable.marker_bg));
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            markerImageView = new ImageView(context);
            markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION_X, MARKER_DIMENSION_Y));
            iconGenerator.setContentView(markerImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(CapsuleMark item, MarkerOptions markerOptions) {

            //BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(capsuleMarkerImageIdList.get(idCount++));
            //Bitmap b = bitmapDraw.getBitmap();
            //Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            if (idCount >= capsuleMarkerImageIdList.size())
                idCount = 0;
            markerImageView.setImageResource(capsuleMarkerImageIdList.get(idCount++));
            Bitmap icon = iconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            markerOptions.snippet(item.getSnippet());
            try {
                List<Address> list = geocoder.getFromLocation(item.getPosition().latitude,
                        item.getPosition().longitude, 3);
                if (list.size() > 0)
                    markerOptions.title(list.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }
}




