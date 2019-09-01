package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

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


public class PopupAttendMemberActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "PopupAttendMemberActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private GroupInfoAdapter groupInfoAdapter; //모임정보 어댑터
    private String jsonString; // json 데이터 파일
    ImageButton closePopupButton; //액티비티 닫기 버튼
    TextView noneContent; //참석하는 멤버가 없을 경우 보여주는 문구

    //전달받은 인텐트 값
    String scheduleIdxKey; //일정 index


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 상단 타이틀 제거
        setContentView(R.layout.activity_popup_attend_member);

        //초기화
        closePopupButton = findViewById(R.id.closePopupButton);
        noneContent = findViewById(R.id.noneContent);

        // 인텐트값 넘겨받기
        Intent intent = getIntent();
        scheduleIdxKey = intent.getStringExtra("scheduleIdxOri"); // 일정 index

        Log.e(TAG, "일정 index : " + scheduleIdxKey);

        //액티비티 닫기 버튼을 클릭했을 경우
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MemberInit(); //멤버 목록 초기화
        //모임 멤버 정보 JSON 파일 가져오기
        GetAttendMemberData memberTask = new GetAttendMemberData();
        //일정 index 순서
        memberTask.execute("http://" + IP_ADDRESS + "/db/schedule_attend.php", scheduleIdxKey);

        Log.e(TAG, "일정 index : " + scheduleIdxKey);

    }


    //멤버 목록 초기화
    private void MemberInit() {
        RecyclerView recyclerView = findViewById(R.id.attendMemberList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false); // 스크롤 중복 방지
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        groupInfoAdapter = new GroupInfoAdapter();
        recyclerView.setAdapter(groupInfoAdapter);
    }

    //모임에 가입한 회원정보를 요청한다.
    private class GetAttendMemberData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupAttendMemberActivity.this,
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
                memberResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String scIdx = params[1];

            //일정 index 순서
            String postParameters = "scIdx=" + scIdx + "&joinUser=" + "LEFT JOIN user ON schedule_attend.attend_member = user.user_email";

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

    //모임에 가입한 회원정보 JSON 파일을 리스트에 저장한다.
    private void memberResult() {

        String TAG_JSON = "scheduleAttend";
        String TAG_USER_NAME = "userName"; //멤버 이름
        String TAG_USER_INTRO = "userIntro"; //멤버 소개
        String TAG_USER_PROFILE = "userProfile"; //멤버 프로필

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String userName = item.getString(TAG_USER_NAME);
                String userIntro = item.getString(TAG_USER_INTRO);
                String userProfile = item.getString(TAG_USER_PROFILE);

                // 각 List의 값들을 data 객체에 set 해줍니다.
                GroupInfoData data = new GroupInfoData();

                data.setUserName(userName);

                if (userIntro.equals("null")) {
                    data.setUserIntro("");
                } else {
                    data.setUserIntro(userIntro);
                }

                if (userProfile.equals("null") || userProfile.equals("") || userProfile.equals(" ")) {
                    data.setUserProfile("http://www.togetherme.tk/static/images/icon_profile.png");
                } else {
                    data.setUserProfile(userProfile);
                }

                // 각 값이 들어간 data를 adapter에 추가합니다.
                groupInfoAdapter.addItem(0, data);
            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            groupInfoAdapter.notifyDataSetChanged();

            if(jsonArray.length() == 0){
                noneContent.setVisibility(View.VISIBLE);
            } else {
                noneContent.setVisibility(View.GONE);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }
    }
}
