package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.together.StaticInit.loginUserId;
import static com.example.together.StaticInit.loginUserName;

public class SettingLeaveActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "SettingLeaveActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    private List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트
    ImageView prevButton; //뒤로가기 버튼
    TextView userEmail; //이메일란
    EditText userPassword; //비밀번호 입력란
    CheckBox checkAgree; //회원탈퇴 동의 체크박스
    private boolean isCheck = false; //체크박스 확인 유무
    Button leaveBtn; //탈퇴하기 버튼



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_leave);

        //초기화
        prevButton = findViewById(R.id.prevButton);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        checkAgree = findViewById(R.id.checkAgree);
        leaveBtn = findViewById(R.id.leaveBtn);

        //뒤로가기 버튼을 클릭했을 경우
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //SharedPreferences에 저장된 로그인 정보를 전역변수에 저장한다.
        SharedPreferences preferences = getSharedPreferences("sFile", 0);
        loginUserId = preferences.getString("USER_LOGIN_ID", null);

        //이메일란에 로그인한 이메일 정보를 저장한다.
        userEmail.setText(loginUserId);

        // 회원탈퇴 동의 체크박스 함수
        checkAgree.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) { //체크가 된경우
                    isCheck = true;
                } else { //체크를 해제한 경우
                    isCheck = false;
                }
            }
        });


        //탈퇴하기 버튼을 클릭했을 경우
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userPassword.getText().toString().length() == 0){
                    Toast.makeText(SettingLeaveActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isCheck == false){
                    Toast.makeText(SettingLeaveActivity.this, "탈퇴 유의사항에 동의해야합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }


                //회원가입 탈퇴 정보 보내기
                GetUserData userTask = new GetUserData();
                //이메일, 비밀번호 순서
                userTask.execute("http://" + IP_ADDRESS + "/mypage/leave_check.php", loginUserId, userPassword.getText().toString());
            }
        });

    }

    private class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SettingLeaveActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 showResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {
                jsonString = result;

                if(result.equals("회원탈퇴가 완료되었습니다.")){
                    Toast.makeText(SettingLeaveActivity.this, result, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SettingLeaveActivity.this, LoginActivity.class);
                    // 기존에 담겨있던 모든 task를 제거하고, 로그인 activity만 보여줌
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(SettingLeaveActivity.this, result, Toast.LENGTH_SHORT).show();
                }

            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String userLoginEmail = params[1];
            String userPassword = params[2];

            //이메일, 비밀번호 순서
            String postParameters = "userLoginEmail=" + userLoginEmail + "&userPassword=" + userPassword;

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

}
