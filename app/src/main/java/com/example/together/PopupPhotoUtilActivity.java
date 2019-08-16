package com.example.together;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.together.StaticInit.IP_ADDRESS;
import static com.example.together.StaticInit.PAGE_GROUP_INDEX;

public class PopupPhotoUtilActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //속성(변수) 선언
    private static String TAG = "PopupPhotoUtilActivity";
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.togetherme.tk/";
    private Uri uri;
    Button photoModifyButton; //일정 수정하기 버튼
    Button photoDeleteButton; //일정 삭제하기 버튼
    ImageButton closePopupButton; //팝업 닫기 버튼
    private int itemPositionKey; //intent로 전달받은 아이템 위치값
    private int itemSizeKey; //intent로 전달받은 아이템 크기값
    private int photoIdxKey; //intent로 전달받은 사진 index 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 상단 타이틀 제거
        setContentView(R.layout.activity_popup_photo_util);

        //초기화
        photoModifyButton = findViewById(R.id.photoModifyButton);
        photoDeleteButton = findViewById(R.id.photoDeleteButton);
        closePopupButton = findViewById(R.id.closePopupButton);

        //인텐트값 넘겨받기
        Intent intent = getIntent();
        itemPositionKey = intent.getIntExtra("itemPositionOri", 0); //아이템 위치값
        itemSizeKey = intent.getIntExtra("itemSizeOri", 0); //아이템 크기
        photoIdxKey = intent.getIntExtra("photoIdxOri", 0); //사진 index

        //사진 수정하기 버튼을 눌렀을 경우
        photoModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
//                finish();

            }
        });

        //사진 삭제하기 버튼을 눌렀을 경우
        photoDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DB 저장
                DeleteData task = new DeleteData();
                //일정 index 보내기
                task.execute("http://" + IP_ADDRESS + "/group/gallery_delete.php", String.valueOf(photoIdxKey));

                //해당 아이템을 삭제하고, 리사이클러뷰 아이템을 재 정렬 해준다.
                GroupPhotoActivity.groupPhotoAdapter.removeItem(itemPositionKey);
                GroupPhotoActivity.groupPhotoAdapter.notifyItemRemoved(itemPositionKey);
                GroupPhotoActivity.groupPhotoAdapter.notifyItemRangeChanged(itemPositionKey, itemSizeKey - 1);

                finish();
            }
        });


        //취소 버튼을 눌렀을 경우
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    //해당 사진을 삭제하는 함수
    class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupPhotoUtilActivity.this,
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

            String serverURL = params[0]; //URL 주소값
            int galleryIdx = Integer.parseInt(params[1]);//일정 index

            //일정 index 보내기
            String postParameters = "galleryIdx=" + galleryIdx;

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
                Log.e(TAG, "POST response code - " + responseStatusCode);

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

                Log.e(TAG, "DeleteData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, PopupPhotoUtilActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, PopupPhotoUtilActivity.this);
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
//                        Toast.makeText(PopupPhotoUtilActivity.this, "Response_onResponse " + response.raw().message(), Toast.LENGTH_LONG).show();
//                        Toast.makeText(PopupPhotoUtilActivity.this, "Success_onResponse " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
//                        Log.e(TAG, "저장된 이미지경로 : " + response.body().getSuccess());

                        if (response.body().getSuccess().equals("error uploading file")) {
                            Toast.makeText(PopupPhotoUtilActivity.this, "이미지 업로드를 실패했습니다.", Toast.LENGTH_SHORT).show();
                            finish(); //사진 수정/삭제 팝업창 닫기
                        } else {
                            finish(); //사진 수정/삭제 팝업창 닫기

                            String photoPath = SERVER_PATH + response.body().getSuccess();

                            //리사이클러뷰 저장
                            // 각 List의 값들을 data 객체에 set 해줍니다.
                            GroupPhotoData data = new GroupPhotoData();
                            data.setGroupPhoto(photoPath);
                            GroupPhotoActivity.groupPhotoAdapter.listData.set(itemPositionKey, data);
                            GroupPhotoActivity.groupPhotoAdapter.notifyItemChanged(itemPositionKey);

                            //DB 저장
                            ModifyData task = new ModifyData();
                            //이미지 경로, 이미지 index 순서
                            task.execute("http://" + IP_ADDRESS + "/group/photo_modify.php", photoPath, String.valueOf(photoIdxKey));
                        }
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
            String filePath = getRealPathFromURIPath(uri, PopupPhotoUtilActivity.this);
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
//                    Toast.makeText(PopupPhotoUtilActivity.this, "Success_3 " + response.message(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(PopupPhotoUtilActivity.this, "Success_4 " + response.body().toString(), Toast.LENGTH_LONG).show();
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

    //DB에 모임 이미지 저장하는 함수
    class ModifyData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupPhotoUtilActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
//            Log.e(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0]; //URL 주소값
            String photoUrl = params[1]; //이미지 경로
            String photoIdx = params[2]; //이미지 인덱스

            //이미지 경로, 모임 인덱스 순서
            String postParameters = "photoUrl=" + photoUrl + "&galleryIdx=" + photoIdx;

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

}
