package com.example.together;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
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
import static com.example.together.StaticInit.getToday;
import static com.example.together.StaticInit.loginUserId;
import static com.example.together.StaticInit.loginUserName;


public class SettingProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //속성(변수) 선언
    private static String TAG = "SettingProfileActivity";
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.togetherme.tk/";
    private String jsonString; // json 데이터 파일
    private List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트
    private Uri uri;
    private String profilePath; //프로필사진 경로
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    ImageView prevButton; //이전화면 이동 버튼
    ImageView profileButton; //프로필사진 버튼
    EditText inputName; //이름 입력란
    TextView inputEmail; //이메일란
    EditText inputPassword; //비밀번호 입력란
    EditText inputPasswordRe; //비밀번호 재입력란
    EditText userTelFirst; //핸드폰 첫번째 자리
    EditText userTelMiddle; //핸드폰 두번째 자리
    EditText userTelLast; //핸드폰 세번째 자리
    TextView inputBirth; //생년월일란
    TextView searchLocationBtn; //지역란
    EditText inputIntro; //소개란
    Button modifyButton; //회원정보 수정 버튼
    //intent로 전달받은 값
    String districtCityKey; //시군
    String districtCountyKey; //시군구
    String districtNameKey; //읍면동


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);

        //초기화
        prevButton = findViewById(R.id.prevButton);
        profileButton = findViewById(R.id.profileButton);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordRe = findViewById(R.id.inputPasswordRe);
        userTelFirst = findViewById(R.id.userTelFirst);
        userTelMiddle = findViewById(R.id.userTelMiddle);
        userTelLast = findViewById(R.id.userTelLast);
        inputBirth = findViewById(R.id.inputBirth);
        searchLocationBtn = findViewById(R.id.searchLocationBtn);
        inputIntro = findViewById(R.id.inputIntro);
        modifyButton = findViewById(R.id.modifyButton);

        //SharedPreferences에 저장된 로그인 정보를 전역변수에 저장한다.
        SharedPreferences preferences = getSharedPreferences("sFile", 0);
        loginUserId = preferences.getString("USER_LOGIN_ID", null);
        loginUserName = preferences.getString("USER_LOGIN_NAME", null);
        if (loginUserId != null) {
            Log.e(TAG, "로그인 id: " + loginUserId);
            Log.e(TAG, "로그인 name: " + loginUserName);

        }

        //회원정보 불러오기
        GetUserData userTask = new GetUserData();
        userTask.execute("http://" + IP_ADDRESS + "/db/user.php", "");

        //이전화면 이동 버튼을 클릭했을 경우, 현재 액티비티 종료
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //프로필사진 버튼을 클릭했을 경우
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        //생년월일란을 클릭했을 경우
        inputBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SettingProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getDatePicker().setCalendarViewShown(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

//                AlertDialog.Builder builder = new AlertDialog.Builder(SettingProfileActivity.this);
//                View view = LayoutInflater.from(SettingProfileActivity.this).inflate(R.layout.activity_popup_birth, null, false);
//                builder.setView(view);
//
//                final Button submitButton = view.findViewById(R.id.submitButton); // 선택 버튼
//                final ImageButton closePopupButton = view.findViewById(R.id.closePopupButton); // 팝업닫기 버튼
//                final DatePicker datePicker = view.findViewById(R.id.datePicker); // 생년월일 선택
//                final AlertDialog dialog = builder.create();
//
//
//                // 선택 버튼을 눌렀을 경우
//                submitButton.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
//                            @Override
//                            public void onDateChanged(DatePicker picker, int yyyy, int mm, int dd) {
//                                Log.e(TAG, String.valueOf(yyyy));
//                                inputBirth.setText(yyyy + "." + mm + "." + dd);
//                            }
//                        });
//                        dialog.dismiss();
//                    }
//                });
//
//
//                // 팝업닫기 버튼을 눌렀을 경우
//                closePopupButton.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + month + "-" + day;
                inputBirth.setText(date);
            }
        };

        //지역란을 클릭했을 경우
        searchLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //읍면동 검색 화면으로 이동
                Intent intent = new Intent(SettingProfileActivity.this, SearchDistrictActivity.class);
                //읍면동 검색 결과 받기
                startActivityForResult(intent, 3000);
            }
        });


        //가입하기 버튼을 클릭했을 경우
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputName.getText().toString().length() == 0) {
                    Toast.makeText(SettingProfileActivity.this, "이름을 입력해주세요. ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!inputPassword.getText().toString().equals(inputPasswordRe.getText().toString())) {
                    Toast.makeText(SettingProfileActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    inputPasswordRe.setText("");
                    return;
                }

//                Log.e(TAG, "이름: " + inputName.getText().toString());
//                Log.e(TAG, "비밀번호: " + inputPassword.getText().toString());
//                Log.e(TAG, "프로필: " + profilePath);
//                Log.e(TAG, "연락처: " + userTelFirst.getText().toString() + "-" + userTelMiddle.getText().toString() + "-" + userTelLast.getText().toString());
//                Log.e(TAG, "생년월일: " + inputBirth.getText().toString());
//                Log.e(TAG, "지역: " + searchLocationBtn.getText().toString());
//                Log.e(TAG, "자기소개: " + inputIntro.getText().toString());


                //DB 저장
                InsertData task = new InsertData();
                //이름, 비밀번호, 프로필사진, 연락처 첫번째, 연락처 중간, 연락처 마지막, 생년월일, 지역, 자기소개, 회원이메일 순서
                task.execute("http://" + IP_ADDRESS + "/mypage/profile_check.php",
                        inputName.getText().toString(),
                        inputPassword.getText().toString(),
                        profilePath,
                        userTelFirst.getText().toString(),
                        userTelMiddle.getText().toString(),
                        userTelLast.getText().toString(),
                        inputBirth.getText().toString(),
                        searchLocationBtn.getText().toString(),
                        inputIntro.getText().toString(),
                        inputEmail.getText().toString()
                );

                //현재 액티비티 종료
//                finish();
                Toast.makeText(SettingProfileActivity.this, "회원정보가 수정됐습니다.", Toast.LENGTH_SHORT).show();
                inputPassword.setText("");
                inputPasswordRe.setText("");

            }
        });
    }

    //회원 정보 JSON 가져오기
    private class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SettingProfileActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {

                jsonString = result;
                userResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "userEmail=" + loginUserId;

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

    //회원 정보 JSON 파싱 후, 데이터 저장하기
    private void userResult() {

        String TAG_JSON = "user";
        String TAG_USER_NAME = "userName"; //회원 이름
        String TAG_USER_EMAIL = "userEmail"; //회원 이메일
        String TAG_USER_TEL = "userTel"; //회원 전화번호
        String TAG_USER_BIRTH = "userBirth"; //회원 생년월일
        String TAG_USER_LOCATION = "userLocation"; //회원 지역
        String TAG_USER_INTRO = "userIntro"; //회원 소개란
        String TAG_USER_PROFILE = "userProfile"; //회원 프로필사진

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String userName = item.getString(TAG_USER_NAME);
                String userEmail = item.getString(TAG_USER_EMAIL);
                String userTel = item.getString(TAG_USER_TEL);
                String userBirth = item.getString(TAG_USER_BIRTH);
                String userLocation = item.getString(TAG_USER_LOCATION);
                String userIntro = item.getString(TAG_USER_INTRO);
                String userProfile = item.getString(TAG_USER_PROFILE);

                // 프로필사진이 없을경우 기본사진으로 저장한다.
                if (userProfile.equals("null") || userProfile.length() == 0 || userProfile == null) {
                    Glide.with(this)
                            .load("http://www.togetherme.tk/static/images/icon_profile.png")
                            .override(500, 500)
                            .into(profileButton);
                } else {
                    Glide.with(this)
                            .load(userProfile)
                            .override(500, 500)
                            .into(profileButton);
                }

                inputName.setText(userName);
                inputEmail.setText(userEmail);
                if (!userBirth.equals("null") && !userBirth.equals("")) {
                    inputBirth.setText(userBirth);
                }
//                Log.e(TAG, "연락처 : " + userTel);
                if (!userTel.equals("null") && !userTel.equals("")) {
                    userTelFirst.setText(userTel.substring(0, 3));
                    userTelMiddle.setText(userTel.substring(3, 7));
                    userTelLast.setText(userTel.substring(7, 11));
                }

                if (!userLocation.equals("null") && !userLocation.equals("")) {
                    searchLocationBtn.setText(userLocation);
                }

                if (!userIntro.equals("null") && !userIntro.equals("")) {
                    inputIntro.setText(userIntro);
                }


            }

        } catch (JSONException e) {

            Log.e(TAG, "groupMemberResult : ", e);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, SettingProfileActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 3000:

                    //전달 받은 값 저장
                    districtCityKey = data.getStringExtra("districtCityOri"); //시군
                    districtCountyKey = data.getStringExtra("districtCountyOri"); //시군구
                    districtNameKey = data.getStringExtra("districtNameOri"); //읍면동
                    // 읍면동 결과값 입력
                    searchLocationBtn.setText(districtCityKey + " " + districtCountyKey + " " + districtNameKey);
                    break;
            }
        }

        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, SettingProfileActivity.this);
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
//                        Toast.makeText(SettingProfileActivity.this, "Response_onResponse " + response.raw().message(), Toast.LENGTH_LONG).show();
//                        Toast.makeText(SettingProfileActivity.this, "Success_onResponse " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
//                        Log.e(TAG, "저장된 이미지경로 : " + response.body().getSuccess());
                        profilePath = SERVER_PATH + response.body().getSuccess();
                        Log.e(TAG, "profilePath : " + profilePath);
                        Glide.with(SettingProfileActivity.this)
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
            String filePath = getRealPathFromURIPath(uri, SettingProfileActivity.this);
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
//                    Toast.makeText(SettingProfileActivity.this, "Success_3 " + response.message(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(SettingProfileActivity.this, "Success_4 " + response.body().toString(), Toast.LENGTH_LONG).show();
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

            progressDialog = ProgressDialog.show(SettingProfileActivity.this,
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

            String postParameters = "";
            String serverURL = params[0]; //URL 주소값
            String userName = params[1]; //회원 이름
            String userPassword = params[2]; //회원 비밀번호
            String userProfile = params[3]; //프로필사진
            String userTel01 = params[4]; //핸드폰 첫번째 자리
            String userTel02 = params[5]; //핸드폰 두번째 자리
            String userTel03 = params[6]; //핸드폰 세번째 자리
            String userBirth = params[7]; //생년월일
            String userLocation = params[8]; //회원 지역
            String userIntro = params[9]; //회원소개
            String userEmail = params[10]; //회원소개

//            Log.e(TAG, "서버에 보내는 이미지 경로: " + userProfile);

            //프로필을 변경하는 경우
            if(userProfile != null){
                //이름, 비밀번호, 프로필사진, 연락처 첫번째, 연락처 중간, 연락처 마지막, 생년월일, 지역, 자기소개, 회원이메일 순서
                postParameters = "userName=" + userName + "&userPassword=" + userPassword + "&userProfile=" + userProfile + "&userTel01=" + userTel01 + "&userTel02=" + userTel02 + "&userTel03=" + userTel03 + "&userBirth=" + userBirth + "&userLocation=" + userLocation + "&userIntro=" + userIntro + "&userEmail=" + userEmail;
            } else {
            //프로필을 변경하지 않는 경우
                //이름, 비밀번호, 연락처 첫번째, 연락처 중간, 연락처 마지막, 생년월일, 지역, 자기소개, 회원이메일 순서
                postParameters = "userName=" + userName + "&userPassword=" + userPassword + "&userTel01=" + userTel01 + "&userTel02=" + userTel02 + "&userTel03=" + userTel03 + "&userBirth=" + userBirth + "&userLocation=" + userLocation + "&userIntro=" + userIntro + "&userEmail=" + userEmail;
            }


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
