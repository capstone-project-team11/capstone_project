package com.example.capsuletime.mainpages.mypage;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.google.android.material.tabs.TabLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyCapsule extends AppCompatActivity {

    private static final String TAG = "ModifyCapsule";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private int capsule_id;
    private List<Uri> list;
    private Boolean EmptyUriListFlag;
    private Uri photoUri;
    private Uri cropUri;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RetrofitInterface retrofitInterface;
    private String imageFilePath;
    private String user_id;
    private CardView btn_imageClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_capsule);


        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitInterface = retrofitClient.retrofitInterface;

        list = new ArrayList<>();

        Intent intent = getIntent();
        capsule_id = intent.getIntExtra("capsule_id",-1);
        user_id = intent.getStringExtra("user_id");
        /*
        list.add("http://118.44.168.218:7070/contents/1.jpeg");
        list.add("http://118.44.168.218:7070/contents/2.jpeg");
        list.add("http://118.44.168.218:7070/contents/1.mp4");
        */
        btn_imageClose = (CardView) findViewById(R.id.btn_imageClose);
        Button btn_cancel = (Button) findViewById(R.id.btn_delete);
        Button btn_set = (Button)findViewById(R.id.btn_set);
        EditText tv_title = (EditText)findViewById(R.id.tv_title);
        EditText tv_text = (EditText)findViewById(R.id.tv_text);

        viewPager = (ViewPager)findViewById(R.id.const_vp);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);

        btn_imageClose.setVisibility(View.GONE);
        /*
        Uri uri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.plus)
                + '/' + getResources().getResourceTypeName(R.drawable.plus) + '/' + getResources().getResourceEntryName(R.drawable.plus) );

        */
        Uri test = Uri.parse("");
        list.add(test);
        EmptyUriListFlag = true;
        tabLayout.setupWithViewPager(viewPager, true);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 1, tabLayout);
        viewPager.setAdapter(viewPagerAdapter);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do clear UriList and set viewPage to '+' image
                fileDelete(cropUri);
                list.clear();
                list.add(test);
                EmptyUriListFlag = true;
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(v.getContext(), list, 1, tabLayout);
                viewPager.setAdapter(viewPagerAdapter);
                btn_imageClose.setVisibility(View.GONE);

            }
        });

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이미지 등록
                String title = Objects.requireNonNull(tv_title.getText()).toString(); // null -> ""
                String text = Objects.requireNonNull(tv_text.getText()).toString();
                if (title.equals("")){
                    Toast.makeText(ModifyCapsule.this,"제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestBody id_body = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(capsule_id));
                RequestBody title_body = RequestBody.create(MediaType.parse("text/plain"), title);
                RequestBody text_body = RequestBody.create(MediaType.parse("text/plain"), text);



                /*
                File file = new File("/storage/emulated/0/Download/google.jpg");
                String type = getMimeType(file);
                RequestBody requestFile = RequestBody.create(MediaType.parse(type), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                */

                // Will need

                if( capsule_id != -1 && !EmptyUriListFlag) {

                    List<MultipartBody.Part> parts = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++){

                        String absolutePath = getPath(v.getContext(),list.get(i));
                        if (absolutePath != null)
                        {
                            File file = new File(absolutePath);
                            String type = getMimeType(file);
                            RequestBody requestFile = RequestBody.create(MediaType.parse(type), file);
                            parts.add(MultipartBody.Part.createFormData("file",file.getName(), requestFile));
                        }
                    }

                    retrofitInterface.requestPutCapsuleWithImages(id_body, title_body, text_body, parts).enqueue(new Callback<Success>() {
                        @Override
                        public void onResponse(Call<Success> call, Response<Success> response) {
                            Log.d(TAG,"success with images");

                            fileDelete(cropUri);

                            Toast.makeText(ModifyCapsule.this,"캡슐 수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ModifyCapsule.this, mypage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("user_id",user_id);
                            startActivity(intent);

                        }

                        @Override
                        public void onFailure(Call<Success> call, Throwable t) {
                            Log.d(TAG,"fail");

                            fileDelete(cropUri);
                            list.clear();
                            list.add(test);
                            EmptyUriListFlag = true;
                            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(v.getContext(), list, 1, tabLayout);
                            viewPager.setAdapter(viewPagerAdapter);
                            btn_imageClose.setVisibility(View.GONE);

                            Toast.makeText(ModifyCapsule.this,"캡슐 내용을 수정해주세요",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if ( capsule_id != -1 && EmptyUriListFlag) {
                    retrofitInterface.requestPutCapsule(capsule_id, title, text).enqueue(new Callback<Success>() {
                        @Override
                        public void onResponse(Call<Success> call, Response<Success> response) {
                            Toast.makeText(ModifyCapsule.this,"캡슐 수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ModifyCapsule.this, mypage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("user_id",user_id);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Success> call, Throwable t) {
                            Log.d(TAG,"fail");
                            Toast.makeText(ModifyCapsule.this,"캡슐 내용을 수정해주세요",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ModifyCapsule.this,"잘못된 캡슐 ID",Toast.LENGTH_SHORT).show();
                }




            }
        });
    }

    private void fileDelete(Uri uri) {
        File fDelete;
        if (uri != null && !uri.equals(Uri.EMPTY))
            fDelete = new File(Objects.requireNonNull(uri.getPath()));
        else
            return;

        if (fDelete.exists()) {
            if (fDelete.delete()){
                Log.d(TAG,uri.getPath() + " - file is deleted");
            }
        } else {
            Log.d(TAG,uri.getPath() + "- file exist not");
        }
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
        }
    };

    // 카메라에서 사진 촬영
    public void doTakePhotoAction() { // 카메라 촬영후 이미지 가져오기

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { // Camera Denied
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);

        } else { // Camera Accept


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {

                }

                if(photoFile != null) {
                    photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }

        }
    }

    private void launchImageCrop(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(480,480)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(this);
        Log.d("getPath", photoUri.toString());
    }

    /*
    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(imageFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
     */

    // 앨범에서 이미지 가져오기
    public void doTakeAlbumAction() {

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return ;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
        /*
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        */

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("AAA","RESULT_OK = "+ RESULT_OK);
        Log.d("AAA","resultCode = "+resultCode);


        if (resultCode != RESULT_OK) {
            Log.d(TAG,"request Not OK");
            return;
        }


        switch (requestCode) {

            case PICK_FROM_ALBUM: {

                final List<Bitmap> bitmaps = new ArrayList<>();
                ClipData clipData = data.getClipData();

                if (clipData != null) {// 이미지 여러개 클
                    list.clear();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        list.add(imageUri);
                    }

                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 0, tabLayout);
                    viewPager.setAdapter(viewPagerAdapter);
                    EmptyUriListFlag = false;
                    btn_imageClose.setVisibility(View.VISIBLE);

                } else { // 이미지 한개 클릭
                    Uri imageUri = data.getData();

                    list.clear();
                    list.add(imageUri);
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 0, tabLayout);
                    viewPager.setAdapter(viewPagerAdapter);
                    EmptyUriListFlag = false;
                    btn_imageClose.setVisibility(View.VISIBLE);
                }
                break;

            }
            case PICK_FROM_CAMERA: {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                ExifInterface exif = null;

                Log.d("getPath", photoUri.toString());
                launchImageCrop(photoUri);
                /*
                try {
                    File albumFile = null;
                    //albumFile = createImageFile();
                    //photoUri =
                    //albumUri = Uri.fromFile(albumFile);
                    Log.d("CropTAG","CROPCROP");


                    //cropImage();
                    //exif = new ExifInterface(imageFilePath);
                } catch (IOException e){
                    e.printStackTrace();
                }
                */
                /*
                int exifOrientation;
                int exifDegree;

                if (exif != null){
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    //exifDegree = exifOrientationToDegress(exifOrientation);
                } else {
                    exifDegree = 0;
                }
                */


                //uri 버전
                /*
                if (data.getData() != null){
                    try {

                        File albumFile = null;
                        albumFile = createImageFile();
                        photoUri = data.getData();
                        albumUri = Uri.fromFile(albumFile);
                        Log.d("CropTAG","CROPCROP");

                        cropImage();

                    } catch (IOException e) {

                        e.printStackTrace();

                    }
                } else {
                    Log.d("onResult","data null");
                }
                */
                //비트맵 버전
                /*
                try {
                    if(data.hasExtra("data")){

                        Bundle extras = data.getExtras();
                        Bitmap bitmap = (Bitmap) extras.get("data");

                        //bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);

                        //requestReadExternalStoragePermission();

                        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);

                        File albumFile = null;
                        albumFile = createImageFile();
                        photoUri = Uri.parse(path);
                        albumUri = Uri.fromFile(albumFile);
                        Log.d("CropTAG","CROPCROP");
                        cropImage();
                    } else {
                        Log.d(TAG,"data null");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                 */

                break;
            }
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE : {
                CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
                cropUri = activityResult.getUri();
                Log.d("CROP_RESULT","CROP_RESULT");
                //galleryAddPic();
                list.clear();
                list.add(cropUri);
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 0, tabLayout);
                viewPager.setAdapter(viewPagerAdapter);
                EmptyUriListFlag = false;
                btn_imageClose.setVisibility(View.VISIBLE);
                // 임시 파일 삭제
                /*
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()){
                    f.delete();
                }
                 */

                break;

            }

        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("Env_DIR_PICTURES",Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = imageFile.getAbsolutePath();
        Log.d("mCurrentPhotoPath2", imageFilePath);
        return imageFile;
    }


    @NonNull
    static String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "image/*"; // fallback type. You might set it to */*
        }
        return type;
    }


    private void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE );
                // MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
