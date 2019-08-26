package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;

public class FindPasswordActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "FindPasswordActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; //json 데이터 파일
    private List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트
    EditText findInputEmail; //이메일 입력란
    Button submitButton; //비밀번호 재설정 버튼
    TextView registerButton; //회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        //초기화
        findInputEmail = findViewById(R.id.findInputEmail);
        submitButton = findViewById(R.id.submitButton);
        registerButton = findViewById(R.id.registerButton);

        //회원가입 문구 넣기
        String registerBtnString = "아직회원이 아니세요? <b>회원가입하기</b>";
        registerButton.setText(Html.fromHtml(registerBtnString));

        //회원가입 버튼을 클릭했을 경우
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindPasswordActivity.this, RegisterAgreeActivity.class);
                startActivity(intent);
            }
        });

        //비밀번호 재설정 버튼을 클릭했을 경우
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!findInputEmail.getText().toString().matches("^[A-z|0-9]([A-z|0-9]*)(@)([A-z]*)(\\.)([A-z]*)$")) {
                    Toast.makeText(FindPasswordActivity.this, "이메일을 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 회원가입 정보 가져오기 (가입정보 유무 확인)
                GetUserData userTask = new GetUserData();
                userTask.execute("http://" + IP_ADDRESS + "/db/user.php", "");

            }
        });


    }

    //사용자 정보 요청하기
    private class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(FindPasswordActivity.this, "Please Wait", null, true, true);
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

                //가입한 이메일인지 확인한다.
                if (!userInfoList.contains(findInputEmail.getText().toString())) {
                    Toast.makeText(FindPasswordActivity.this, "가입정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //가입된 이메일이라면 비밀번호 찾기 정보(입력받은 이메일 값)를 보낸다.
                InfoData infoTask = new InfoData();
                infoTask.execute("http://" + IP_ADDRESS + "/find_password_check.php", findInputEmail.getText().toString());
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

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String userEmail = item.getString(TAG_USER_EMAIL);

                userInfoList.add(userEmail);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }
    }

    //비밀번호 찾기 양식 보내기
    private class InfoData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(FindPasswordActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.e(TAG, "response - " + result);
            Toast.makeText(FindPasswordActivity.this, "해당 메일로 임시 비밀번호가 발송되었습니다.", Toast.LENGTH_SHORT).show();

            if (result == null) {
                Log.e(TAG, errorString);
            } else {

                jsonString = result;
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String inputUserEmail = params[1];
            String postParameters = "inputUserEmail=" + inputUserEmail;

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
