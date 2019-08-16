package com.example.together;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.loginUserId;

public class CreateGroupInfoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //TODO 완료 버튼을 눌렀을 경우 이미지 업로드 기능이 구현되도록 수정할 것
    //TODO 현재는 대표이미지 사진이 변경 될 때마다 이미지가 업로드 된다.

    //속성(변수) 선언
    private static String TAG = "CreateGroupInfoActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.togetherme.tk/";
    private String jsonString; // json 데이터 파일
    private Uri uri;
    private String groupThumbnailUrl; //프로필사진 경로
    String groupCategoryKey; //intent로 넘겨받은 모임 카테고리 값
    ImageView groupThumbnail; //모임 대표이미지
    EditText groupName; //모임 이름
    EditText groupLocation; //모임 장소
    EditText groupIntro; //모임 소개
    Button prevButton; //이전으로 버튼
    Button submitButton; //완료 버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_info);

        SharedPreferences preferences = getSharedPreferences("sFile", 0);
        loginUserId = preferences.getString("USER_LOGIN_ID", null);

        //초기화
        groupThumbnail = findViewById(R.id.groupThumbnail);
        groupName = findViewById(R.id.groupName);
        groupLocation = findViewById(R.id.groupLocation);
        groupIntro = findViewById(R.id.groupIntro);
        prevButton = findViewById(R.id.prevButton);
        submitButton = findViewById(R.id.submitButton);

        // 인텐트값 넘겨받기
        Intent intent = getIntent();
        groupCategoryKey = intent.getStringExtra("groupCategoryOri"); //모임 카테고리
//        Log.e(TAG, groupCategoryKey);
        
        //모임 대표이미지를 클릭했을 경우
        groupThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        //이전으로 버튼을 클릭했을 경우
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //완료 버튼을 클릭했을 경우
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupName.getText().toString().length() == 0){
                    Toast.makeText(CreateGroupInfoActivity.this, "모임이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(groupLocation.getText().toString().length() == 0){
                    Toast.makeText(CreateGroupInfoActivity.this, "모임장소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(groupIntro.getText().toString().length() == 0){
                    Toast.makeText(CreateGroupInfoActivity.this, "모임소개를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //DB 저장
                InsertData task = new InsertData();
                try {
                    // 모임이름, 카테고리, 장소, 소개, 모임장, 모임 대표이미지 순서
                    PAGE_GROUP_INDEX = Integer.parseInt(task.execute("http://" + IP_ADDRESS + "/create_group_check.php",
                            groupName.getText().toString(),
                            groupCategoryKey,
                            groupLocation.getText().toString(),
                            groupIntro.getText().toString(),
                            loginUserId,
                            groupThumbnailUrl
                    ).get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //해당 모임 페이지로 이동
                Intent intent = new Intent(CreateGroupInfoActivity.this, GroupInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CreateGroupInfoActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.e(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0]; //URL 주소값
            String groupName = (String) params[1]; //모임이름
            String groupCategory = (String) params[2];//카테고리
            String groupLocation = (String) params[3];//장소
            String groupIntro = (String) params[4];//소개
            String groupHost = (String) params[5];//모임장
            String imgUrl = (String) params[6];//모임 대표이미지

            Log.e(TAG, "_모임 대표이미지 최종 경로: " + imgUrl);

            // 모임이름, 카테고리, 장소, 소개, 모임장, 모임 대표이미지 순서
            String postParameters = "group_name=" + groupName + "&group_category=" + groupCategory + "&group_location=" + groupLocation + "&group_intro=" + groupIntro + "&group_host=" + groupHost + "&url=" + imgUrl;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.e(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, CreateGroupInfoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, CreateGroupInfoActivity.this);
                File file = new File(filePath);
                Log.e(TAG, "Filename " + file.getName());
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_PATH)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
                Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
                fileUpload.enqueue(new Callback<UploadObject>() {
                    @Override
                    public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
//                        Toast.makeText(CreateGroupInfoActivity.this, "Response_onResponse " + response.raw().message(), Toast.LENGTH_LONG).show();
//                        Toast.makeText(CreateGroupInfoActivity.this, "Success_onResponse " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
//                        Log.e(TAG, "저장된 이미지경로 : " + response.body().getSuccess());
                        groupThumbnailUrl = SERVER_PATH + response.body().getSuccess();
                        Log.e(TAG, "groupThumbnailUrl : " + groupThumbnailUrl);

                        //모임 대표이미지 ImageView 의 값을 선택한 이미지로 변경해준다.
                        Glide.with(CreateGroupInfoActivity.this)
                                .load(groupThumbnailUrl)
                                .override(500, 300)
                                .into(groupThumbnail);
                    }

                    @Override
                    public void onFailure(Call<UploadObject> call, Throwable t) {
                        Log.e(TAG, "Error " + t.getMessage());
                    }
                });
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (uri != null) {
            String filePath = getRealPathFromURIPath(uri, CreateGroupInfoActivity.this);
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
            Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
//                    Toast.makeText(CreateGroupInfoActivity.this, "Success_3 " + response.message(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(CreateGroupInfoActivity.this, "Success_4 " + response.body().toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<UploadObject> call, Throwable t) {
                    Log.e(TAG, "Error " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "Permission has been denied");
    }
}

