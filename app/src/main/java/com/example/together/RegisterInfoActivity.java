package com.example.together;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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


public class RegisterInfoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //TODO 회원가입 버튼을 눌렀을 경우 이미지 업로드 기능이 구현되도록 수정할 것
    //TODO 현재는 프로필 사진이 변경 될 때마다 이미지가 업로드 된다.

    //속성(변수) 선언
    private static String TAG = "RegisterInfoActivity";
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.togetherme.tk/";
    private String jsonString; // json 데이터 파일
    private List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트
    private Uri uri;
    private String profilePath; //프로필사진 경로
    private boolean isCheck = false;
    ImageView profileButton; //프로필사진 버튼
    EditText inputName; //이름 입력란
    EditText inputEmail; //이메일 입력란
    Button emailCheckBtn; //이메일 중복검사 버튼
    EditText inputPassword; //비밀번호 입력란
    EditText inputPasswordRe; //비밀번호 재입력란
    Button submitButton; //가입하기 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        //초기화
        profileButton = findViewById(R.id.profileButton);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        emailCheckBtn = findViewById(R.id.emailCheckBtn);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordRe = findViewById(R.id.inputPasswordRe);
        submitButton = findViewById(R.id.submitButton);



        //프로필사진 버튼을 클릭했을 경우
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        //이메일 중복확인 버튼을 클릭했을 경우
        emailCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 정보 가져오기
                GetUserData userTask = new GetUserData();
                userTask.execute("http://" + IP_ADDRESS + "/db/user.php", "");

                //핸들러를 사용하여 사용자 정보를 가져오는 시간동안 다음에 수행되어야 할 작업들을 delay시킨다.
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (!inputEmail.getText().toString().matches("^[A-z|0-9]([A-z|0-9]*)(@)([A-z]*)(\\.)([A-z]*)$")) {
//                            Toast.makeText(RegisterInfoActivity.this, "이메일을 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
//                            return;
//                        } else if (userInfoList.contains(inputEmail.getText().toString())) {
//                            Toast.makeText(RegisterInfoActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        isCheck = true;
//                        Toast.makeText(RegisterInfoActivity.this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
//
//                    }
//                }, 1000);




            }
        });

        //이메일 입력란의 텍스트에 변화가 있을 경우
        inputEmail.addTextChangedListener(new TextWatcher() {
            // 입력하기 전에
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            // 입력되는 텍스트에 변화가 있을 때
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCheck = false;
            }

            // 입력이 끝났을 때
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //가입하기 버튼을 클릭했을 경우
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputName.getText().toString().length() == 0) {
                    Toast.makeText(RegisterInfoActivity.this, "이름을 입력해주세요. ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isCheck == false) {
                    Toast.makeText(RegisterInfoActivity.this, "이메일을 중복확인 하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(inputPassword.getText().toString().length() == 0 || inputPasswordRe.getText().toString().length() == 0){
                    Toast.makeText(RegisterInfoActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!inputPassword.getText().toString().equals(inputPasswordRe.getText().toString())) {
                    Toast.makeText(RegisterInfoActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    inputPasswordRe.setText("");
                    return;
                }

                //DB 저장
                InsertData task = new InsertData();
                //이름, 이메일, 비밀번호, 프로필사진 순서
                task.execute("http://" + IP_ADDRESS + "/register_check.php",
                        inputName.getText().toString(),
                        inputEmail.getText().toString(),
                        inputPassword.getText().toString(),
                        profilePath
                );

                //로그인 화면으로 이동
                Intent intent = new Intent(RegisterInfoActivity.this, LoginActivity.class);
                //가입한 이메일 값을 전달한다.
                intent.putExtra("userEmailOri", inputEmail.getText().toString());

                //Flag 정리
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(RegisterInfoActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, RegisterInfoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, RegisterInfoActivity.this);
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
//                        Toast.makeText(RegisterInfoActivity.this, "Response_onResponse " + response.raw().message(), Toast.LENGTH_LONG).show();
//                        Toast.makeText(RegisterInfoActivity.this, "Success_onResponse " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
//                        Log.e(TAG, "저장된 이미지경로 : " + response.body().getSuccess());
                        profilePath = SERVER_PATH + response.body().getSuccess();
                        Log.e(TAG, "profilePath : " + profilePath);
                        Glide.with(RegisterInfoActivity.this)
                                .load(profilePath)
                                .override(500, 300)
                                .into(profileButton);
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
            String filePath = getRealPathFromURIPath(uri, RegisterInfoActivity.this);
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
//                    Toast.makeText(RegisterInfoActivity.this, "Success_3 " + response.message(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(RegisterInfoActivity.this, "Success_4 " + response.body().toString(), Toast.LENGTH_LONG).show();
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

    //DB에 회원가입 정보 저장하는 함수
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RegisterInfoActivity.this,
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
            String userName = params[1]; //회원 이름
            String userEmail = params[2]; //회원 이메일
            String userPassword = params[3]; //회원 비밀번호
            String userProfilePath = params[4]; //회원 프로필경로로

            //이름, 이메일, 비밀번호, 프로필사진 순서
            String postParameters = "userName=" + userName + "&userEmail=" + userEmail + "&userPassword=" + userPassword + "&userProfilePath=" + userProfilePath;

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

    //사용자 정보 요청하기
    private class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(RegisterInfoActivity.this, "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 showResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {
                jsonString = result;
                userInfoResult();
                // 이메일 중복검사 확인 함수
                emailCheckFunction();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.e(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    //사용자 정보 파싱 후 저장하기
    private void userInfoResult() {
        String TAG_JSON = "user";
        String TAG_USER_EMAIL = "userEmail"; //회원 이메일
        String TAG_USER_PASSWORD = "userPassword"; //회원 비밀번호

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String userEmail = item.getString(TAG_USER_EMAIL);
                String userPassword = item.getString(TAG_USER_PASSWORD);

                userInfoList.add(userEmail);
                userInfoList.add(userPassword);

            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }
    }

    private void emailCheckFunction(){
        if (!inputEmail.getText().toString().matches("^[A-z|0-9]([A-z|0-9]*)(@)([A-z]*)(\\.)([A-z]*)$")) {
            Toast.makeText(RegisterInfoActivity.this, "이메일을 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (userInfoList.contains(inputEmail.getText().toString())) {
            Toast.makeText(RegisterInfoActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        isCheck = true;
        Toast.makeText(RegisterInfoActivity.this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
    }


}
