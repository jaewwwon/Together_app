package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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

public class LoginActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "LoginActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    EditText inputEmail; // 이메일 입력란
    EditText inputPassword; // 비밀번호 입력란
    TextView findPwdButton; // 비밀번호 찾기 버튼
    TextView registerButton; // 회원가입 버튼
    Button loginButton; // 로그인 버튼
    String userEmailKey; //회원가입 시 전달받은 이메일 값
    List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트
    String autoLoginId; //자동로그인 아이디값
    String autoLoginPassword; //자동로그인 비밀번호값

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //초기화
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        findPwdButton = findViewById(R.id.findPwdButton);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);


        // 인텐트값 넘겨받기
        Intent intent = getIntent();
        String userEmailKey = intent.getStringExtra("userEmailOri"); // 가입한 Email 값

        //가입한 이메일주소를 이메일란에 저장한다.
        inputEmail.setText(userEmailKey);


        //자동로그인 여부
        //SharedPreferences에 저장된 로그인 정보를 전역변수에 저장한다.
        SharedPreferences preferences = getSharedPreferences("sFile", 0);
        autoLoginId = preferences.getString("USER_LOGIN_ID", null);
        autoLoginPassword = preferences.getString("USER_LOGIN_NAME", null);

        inputEmail.setText(autoLoginId);
        inputPassword.setText(autoLoginPassword);

        if(autoLoginId !=null && autoLoginPassword != null) {
            if(autoLoginId.equals(inputEmail.getText().toString()) && autoLoginPassword.equals(inputPassword.getText().toString())) {
                Intent autoIntent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(autoIntent);
                finish();
            }
        }


        //회원가입 문구 넣기
        String registerBtnString = "아직회원이 아니세요? <b>회원가입하기</b>";
        registerButton.setText(Html.fromHtml(registerBtnString));


        //비밀번호 찾기 버튼을 클릭했을 경우
        findPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });

        //회원가입 버튼을 클릭했을 경우
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterAgreeActivity.class);
                startActivity(intent);
            }
        });

        //로그인 버튼을 클릭했을 경우
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 이메일을 입력하지 않은 경우
                if (inputEmail.length() == 0) {
                    Toast.makeText(LoginActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이메일 형식 확인
                if (!inputEmail.getText().toString().matches("^[A-z|0-9]([A-z|0-9]*)(@)([A-z]*)(\\.)([A-z]*)$")) {
                    Toast.makeText(LoginActivity.this, "이메일을 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 회원 정보 가져오기
                GetUserData userTask = new GetUserData();
                userTask.execute("http://" + IP_ADDRESS + "/db/user.php", inputEmail.getText().toString());


//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        });

    }

    private class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait", null, true, true);
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

                // 회원 정보에 입력한 아이디(이메일) 값이 있고, 그 아이디의 비밀번호 정보와 사용자 입력한 비밀번호가 일치할 때, 로그인이 된다.
                if (userInfoList.contains(inputEmail.getText().toString())) {

                    Log.e(TAG, inputEmail.getText().toString());

                    // 사용자 정보 가져오기
                    GetUserCheckData userCheckTask = new GetUserCheckData();
                    String userCheckResult = null;
                    try {
                        userCheckResult = userCheckTask.execute("http://" + IP_ADDRESS + "/db/user_check.php", inputEmail.getText().toString(), inputPassword.getText().toString()).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (userCheckResult.length() > 0) {
                        Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 로그인한 회원정보 아이디를 SharedPreferences에 저장한다.
                    SharedPreferences preferences = getSharedPreferences("sFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("USER_LOGIN_ID", inputEmail.getText().toString());
                    editor.putString("USER_LOGIN_NAME", userInfoList.get(userInfoList.indexOf(inputEmail.getText().toString()) - 1));

                    //자동 로그인 정보 저장
                    editor.putString("AOTO_LOGIN_ID", inputEmail.getText().toString()); //아이디값 저장
                    editor.putString("AOTO_LOGIN_PASSWORD", inputPassword.getText().toString()); //비밀번호값 저장
                    editor.apply();

                } else {
                    Toast.makeText(LoginActivity.this, "가입된 이메일이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String userEmail = params[1];
            Log.e(TAG, "userEmail : " + userEmail);
            String postParameters = "userEmail=" + userEmail;

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

    private void userInfoResult() {
        String TAG_JSON = "user";
        String TAG_USER_NAME = "userName"; //회원 이름
        String TAG_USER_EMAIL = "userEmail"; //회원 이메일
        String TAG_USER_PASSWORD = "userPassword"; //회원 비밀번호

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String userName = item.getString(TAG_USER_NAME);
                String userEmail = item.getString(TAG_USER_EMAIL);
                String userPassword = item.getString(TAG_USER_PASSWORD);

                userInfoList.add(userName);
                userInfoList.add(userEmail);
                userInfoList.add(userPassword);

            }


        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }
    }


    private class GetUserCheckData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait", null, true, true);
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
//                Log.e(TAG+"_check :", jsonString);
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String userEmail = params[1];
            String userPassword = params[2];

            String postParameters = "userEmail=" + userEmail + "&userPassword=" + userPassword;

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
                Log.e(TAG, "response code - " + responseStatusCode);

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
