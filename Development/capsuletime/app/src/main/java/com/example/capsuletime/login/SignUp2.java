package com.example.capsuletime.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUp2 extends AppCompatActivity {
    private static final String TAG = "SignUp2";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    private Uri cropUri;
    private Uri photoUri;
    private String imageFilePath;
    private ImageView iv_UserPhoto;
    private int id_view;
    private RetrofitInterface retrofitInterface;


    private String id;
    private String pw;
    private String email;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        pw = intent.getStringExtra("pw");
        email = intent.getStringExtra("email");

        final EditText et_nick = (EditText) this.findViewById(R.id.et_nick_name);
        final EditText et_first = (EditText) this.findViewById(R.id.et_first_name);
        final EditText et_last = (EditText) this.findViewById(R.id.et_last_name);

        iv_UserPhoto = (ImageView) this.findViewById(R.id.user_image);
        CardView cv_upload_picture =  (CardView) this.findViewById(R.id.btn_UploadPicture);
        CardView cv_close_picture = (CardView) this.findViewById(R.id.btn_close);
        Button btn_finish = (Button) this.findViewById(R.id.btn_signupfinish);

        RetrofitClient retrofitClient = new RetrofitClient(getApplicationContext());
        retrofitInterface = retrofitClient.retrofitInterface;

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "권한 설정 완료");
            } else {
                Log.d("TAG", "권한 설정 요청");
                ActivityCompat.requestPermissions(SignUp2.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        */

        cv_upload_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TedPermission.with(getApplicationContext())
                        .setPermissionListener(permissionListener)
                        .setRationaleMessage("카메라 권한이 필요합니다.")
                        .setDeniedMessage("거부하셨습니다.")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();

                CustomDialog custom_dlg = new CustomDialog(SignUp2.this);
                custom_dlg.callFunction();
            }
        });

        cv_close_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cropUri != null && !cropUri.equals(Uri.EMPTY)) {
                    File fdelete = new File(Objects.requireNonNull(cropUri.getPath()));
                    if (fdelete.exists()) {
                        fileDelete(cropUri);
                        iv_UserPhoto.setImageResource(R.drawable.user);
                    } else {
                        Toast.makeText(v.getContext(), "등록한 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), "등록한 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_nick.getText().length() == 0 || et_first.getText().length() == 0 || et_last.getText().length() == 0) {
                    Toast.makeText(v.getContext(), "빈칸을 다 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cropUri != null && !cropUri.equals(Uri.EMPTY)) {

                    RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), id);
                    RequestBody password = RequestBody.create(MediaType.parse("text/plain"), pw);
                    RequestBody nick_name = RequestBody.create(MediaType.parse("text/plain"), et_nick.getText().toString());
                    RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), et_first.getText().toString());
                    RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), et_last.getText().toString());
                    RequestBody email_body = RequestBody.create(MediaType.parse("text/plain"), email);

                    String absoulutePath = getPath(v.getContext(), cropUri);
                    File file = new File(absoulutePath);
                    String type =getMimeType(file);
                    RequestBody requestImage = RequestBody.create(MediaType.parse(type), file);
                    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestImage);

                    Log.d(TAG,"파일 이름 = " + file.getName().toString()+" " + requestImage.toString());

                    retrofitInterface.requestPostUserWithImage(user_id, password, nick_name, first_name, last_name, email_body, multipartBody)
                            .enqueue(new Callback<Success>() {
                                @Override
                                public void onResponse(Call<Success> call, Response<Success> response) {
                                    Success success = response.body();
                                    if (success.getSuccess().equals("true")){
                                        Toast toast = Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent intent = new Intent(SignUp2.this, login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(intent);
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "login_signup_btn 시도 실패", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Success> call, Throwable t) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "서버통신 실패", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                } else {
                    retrofitInterface.requestPostUser(id, pw, et_nick.getText().toString(), et_first.getText().toString(), et_last.getText().toString(), email)
                            .enqueue(new Callback<Success>() {
                                @Override
                                public void onResponse(Call<Success> call, Response<Success> response) {
                                    Success success = response.body();
                                    if (success.getSuccess().equals("true")){
                                        Toast toast = Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent intent = new Intent(SignUp2.this, login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "login_signup_btn 시도 실패", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }


                                @Override
                                public void onFailure(Call<Success> call, Throwable t) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "서버통신 실패", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                }
            }
        });

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

    private void fileDelete(Uri uri) {
        File fDelete = new File(uri.getPath());
        if (fDelete.exists()) {
            if (fDelete.delete()){
                Log.d(TAG,uri.getPath() + " - file is deleted");
            }
        } else {
            Log.d(TAG,uri.getPath() + "- file exist not");
        }
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


    // 사진찍기 or 카메라 촬영 다이얼로그
    private void photoDialog() {
        final CharSequence[] items = {"갤러리", "카메라", "취소"};
        AlertDialog.Builder selectDialog = new AlertDialog.Builder(SignUp2.this);

        selectDialog.setTitle("사진선택");
        selectDialog.setCancelable(true);

        selectDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    case 0: {
                        doTakeAlbumAction();
                        break;
                    }
                    case 1: {
                        doTakePhotoAction();
                        break;
                    }
                    case 2: {
                        break;
                    }
                }
            }
        });

        selectDialog.show();
    }


    // 카메라에서 사진 촬영
    public void doTakePhotoAction() { // 카메라 촬영후 이미지 가져오기


        int permissionCheck = ContextCompat.checkSelfPermission(SignUp2.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { // Camera Denied
            ActivityCompat.requestPermissions(SignUp2.this, new String[]{Manifest.permission.CAMERA},0);

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
                .setAspectRatio(100,100)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
        Log.d("getPath", photoUri.toString());
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

    // 앨범에서 이미지 가져오기
    public void doTakeAlbumAction() { //앨범에서 이미지 가져오기

        if (ActivityCompat.checkSelfPermission(SignUp2.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SignUp2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return ;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                //
                if(data.getData() != null){
                    photoUri = data.getData();
                    launchImageCrop(photoUri);
                }
                break;
            }
            case PICK_FROM_CAMERA: {
                launchImageCrop(photoUri);
                break;
            }
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE : {
                CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
                cropUri = activityResult.getUri();
                final ImageView imageView = findViewById(R.id.user_image);
                Glide
                        .with(this)
                        .load(cropUri)
                        .into(imageView);
            }
            break;

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
