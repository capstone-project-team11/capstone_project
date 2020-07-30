package com.example.capsuletime.mainpages.capsulemap;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.capsuletime.CapsuleOneOfAll;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.core.preferences.NickNameSharedPreferences;
import com.example.capsuletime.login.login;
import com.example.capsuletime.mainpages.ar.UnityPlayerActivity;
import com.example.capsuletime.mainpages.mypage.mypage;
import com.example.capsuletime.mainpages.searchpage.searchpage;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class capsulemap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "capsulemap";
    private String nick_name;
    private User user;
    private RetrofitInterface retrofitInterface;
    private List<CapsuleOneOfAll> capsuleList;
    private String drawablePath;
    private GoogleMap mMap;
    private ClusterManager<CapsuleMark> clusterManager;
    private double longitude;
    private double latitude;
    private double cur_lng;
    private double cur_lat;
    private Marker curMarker;
    private boolean firstFlag = false;
    private List<Integer> capsuleMarkerImageIdList;
    private int idCount = 0;
    private List<User> userList;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_capsulemap);

        idCount = 0;

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

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        initCapsuleMarkerImageIdList();

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(nick_name != null){
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
                    Log.d(TAG, user.toString());
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, "server-get-user fail");
                }
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set mypage Selected
        bottomNavigationView.setSelectedItemId(R.id.capsulemap);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.mypage: {
                        Intent intent = new Intent(getApplicationContext(), mypage.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    }

                    case R.id.capsulesearch:{
                        Intent intent = new Intent(getApplicationContext(), searchpage.class);
                        Log.d(TAG, user.toString());
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    }

                    case R.id.capsulemap:
                        return true;

                    case R.id.capsulear: {

                        Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        //return true;
                    }
                }
                return false;

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
            latitude = 37.5512;
            longitude = 126.9882;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setCheckBox();

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

    public void setCheckBox(){

        CheckBox checkBoxFollow = (CheckBox) findViewById(R.id.cb_follow);
        CheckBox checkBoxFollower = (CheckBox)findViewById(R.id.cb_follower);

        checkBoxFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxFollower.isChecked() && isChecked){
                    if (nick_name != null){
                        // only friends
                        retrofitInterface.requestF4F(nick_name).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                if (response.code() == 404) {
                                    // error
                                    Log.d(TAG,"server 404 Error");
                                    return;
                                }
                                userList = response.body();
                                resetClusterItem(false);
                                refreshCamera();
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
                    }
                } else if (checkBoxFollower.isChecked() && !isChecked){
                    // only follower
                    if (nick_name != null){
                        retrofitInterface.requestFollower(nick_name).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                if (response.code() == 404) {
                                    // error
                                    Log.d(TAG,"server 404 Error");
                                    return;
                                }
                                userList = response.body();
                                resetClusterItem(false);
                                refreshCamera();
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
                    }
                } else if (!checkBoxFollower.isChecked() && isChecked) {
                    // only follow
                    if (nick_name != null){
                        retrofitInterface.requestFollow(nick_name).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                if (response.code() == 404) {
                                    // error
                                    Log.d(TAG,"server 404 Error");
                                    return;
                                }
                                userList = response.body();
                                resetClusterItem(false);
                                refreshCamera();
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    userList.clear();
                    resetClusterItem(true);
                    refreshCamera();
                }
            }

        });

        checkBoxFollower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBoxFollow.isChecked() && isChecked){
                    // only friends
                    if (nick_name != null){
                        retrofitInterface.requestF4F(nick_name).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                if (response.code() == 404) {
                                    // error
                                    Log.d(TAG,"server 404 Error");
                                    return;
                                }
                                userList = response.body();
                                resetClusterItem(false);
                                refreshCamera();
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
                    }
                } else if (checkBoxFollow.isChecked() && !isChecked){
                    // only follow
                    if (nick_name != null){
                        retrofitInterface.requestFollow(nick_name).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                if (response.code() == 404) {
                                    // error
                                    Log.d(TAG,"server 404 Error");
                                    return;
                                }
                                userList = response.body();
                                resetClusterItem(false);
                                refreshCamera();
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
                    }
                } else if (!checkBoxFollow.isChecked() && isChecked) {
                    // only follower
                    if (nick_name != null){
                        retrofitInterface.requestFollower(nick_name).enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                if (response.code() == 404) {
                                    // error
                                    Log.d(TAG,"server 404 Error");
                                    return;
                                }
                                userList = response.body();
                                resetClusterItem(false);
                                refreshCamera();
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    userList.clear();
                    resetClusterItem(true);
                    refreshCamera();
                }
            }

        });
    }

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
        if (!firstFlag){
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

        retrofitInterface.requestAllCapsules().enqueue(new Callback<List<CapsuleOneOfAll>>() {
            @Override
            public void onResponse(Call<List<CapsuleOneOfAll>> call, Response<List<CapsuleOneOfAll>> response) {

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
            public void onFailure(Call<List<CapsuleOneOfAll>> call, Throwable t) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        setClusterManager();
    }

    private void refreshCamera(){
        float zoom = mMap.getCameraPosition().zoom - 0.001f;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    private void moveToCurrentPosition() {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
    }

    private void addItems() {
        //mMap.clear();
        for (CapsuleOneOfAll item : capsuleList) {
            if (item.getStatus_temp() == 1 || item.getStatus_lock() == 1)
                continue ;
            CapsuleMark capsuleMark = new CapsuleMark(item);
            clusterManager.addItem(capsuleMark);
        }
        userCurrentMark();
    }

    private void resetClusterItem(Boolean allUserFlag) {
        clusterManager.clearItems();
        for (CapsuleOneOfAll item : capsuleList){
            if (item.getStatus_temp() == 1 || item.getStatus_lock() == 1)
                continue;
            if (allUserFlag) {
                CapsuleMark capsuleMark = new CapsuleMark(item);
                clusterManager.addItem(capsuleMark);
            } else {
                Log.d(TAG, userList.toString());
                for (User user : userList){
                    if (user.getNick_name().equals(item.getNick_name())){
                        CapsuleMark capsuleMark = new CapsuleMark(item);
                        clusterManager.addItem(capsuleMark);
                        break;
                    }
                }
            }

        }
        userCurrentMark();
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
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<CapsuleMark>() {
            @Override
            public boolean onClusterItemClick(CapsuleMark capsuleMark) {
                Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
                intent.putExtra("nick_name2", capsuleMark.getNickName());
                startActivityForResult(intent, 1);
                return false;
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    class MarkerClusterRenderer extends DefaultClusterRenderer<CapsuleMark> {
        private static final int MARKER_DIMENSION_X = 70; // Setting the constant value for the single marker size.
        private static final int MARKER_DIMENSION_Y = 70;

        private final IconGenerator iconGenerator;
        private final ImageView markerImageView;

        public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<CapsuleMark> clusterManager) {
            super(context, map, clusterManager);
            iconGenerator = new IconGenerator(context);
            //iconGenerator.setBackground(getResources().getDrawable(R.drawable.marker_bg));
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
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

}