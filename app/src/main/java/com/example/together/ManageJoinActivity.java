package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
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
import java.util.concurrent.ExecutionException;

import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.loginUserId;

public class ManageJoinActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "ManageJoinActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    static ManageJoinAdapter manageJoinAdapter; //가입한 모임 어댑터
    private ArrayList<ManageJoinData> listData;
    TextView homeNav; //하단메뉴 - 홈
    TextView manageNav; //하단메뉴 - 모임관리
    TextView settingNav; //하단메뉴 - 설정
    TextView groupJoinBtn; //내가 가입한 모임 탭 버튼
    TextView groupCreateBtn; //내가 만든 모임 탭 버튼
    TextView noneContent; //등록된 일정이 없을경우 표시되는 문구

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_join);

        //초기화
        homeNav = findViewById(R.id.homeNav);
        manageNav = findViewById(R.id.manageNav);
        settingNav = findViewById(R.id.settingNav);
        groupJoinBtn = findViewById(R.id.groupJoinBtn);
        groupCreateBtn = findViewById(R.id.groupCreateBtn);
        noneContent = findViewById(R.id.noneContent);

        //하단메뉴 - 홈 버튼을 클릭했을 경우
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageJoinActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //하단메뉴 - 모임관리 버튼을 클릭했을 경우
        manageNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageJoinActivity.this, ManageJoinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //하단메뉴 - 설정 버튼을 클릭했을 경우
        settingNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageJoinActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //내가 가입한 모임 탭 버튼을 클릭했을 경우
        groupJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageJoinActivity.this, ManageJoinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //내가 만든 모임 탭 버튼을 클릭했을 경우
        groupCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageJoinActivity.this, ManageCreateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //가입한 모임 목록 초기화
        manageJoinInit();
        //가입한 모임 정보 JSON 가져오기
        GetJoinGroupData groupTask = new GetJoinGroupData();
        groupTask.execute("http://" + IP_ADDRESS + "/db/group_member.php", "");

    }

    //가입한 모임 목록 초기화
    private void manageJoinInit() {
        RecyclerView recyclerView = findViewById(R.id.joinGroupList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false); // 스크롤 중복 방지

        manageJoinAdapter = new ManageJoinAdapter();
        recyclerView.setAdapter(manageJoinAdapter);
    }

    //모임 게시판 정보 JSON 가져오기
    private class GetJoinGroupData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ManageJoinActivity.this,
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
                if (result != null) {
                    groupResult();
                }

            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {
//            Log.e(TAG, "로그인 ID: " + loginUserId);
            String serverURL = params[0];
            String postParameters = "userLoginEmail=" + loginUserId + "&joinGroupInfo=" + "LEFT JOIN group_info ON group_member.group_idx = group_info.group_idx";

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

    //모임 게시판 정보 JSO 파싱 후, 데이터 저장하기
    private void groupResult() {

        String TAG_JSON = "groupMember";
        String TAG_GROUP_IDX = "groupIdx"; //모임 index
        String TAG_MEMBER_DATE = "memberDate"; //모임 가입일
        String TAG_GROUP_CATEGORY = "groupCategory"; //모임 카테고리
        String TAG_GROUP_NAME = "groupName"; //모임 이름
        String TAG_GROUP_INTRO = "groupIntro"; //모임 소개
        String TAG_GROUP_CITY = "groupCity"; //모임 지역 시/군
        String TAG_GROUP_COUNTY = "groupCounty"; //모임 지역 시/군/구
        String TAG_GROUP_DISTRICT = "groupDistrict"; //모임 지역 읍/면/동

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String groupIdx = item.getString(TAG_GROUP_IDX);
                String memberDate = item.getString(TAG_MEMBER_DATE);
                String groupCategory = item.getString(TAG_GROUP_CATEGORY);
                String groupName = item.getString(TAG_GROUP_NAME);
                String groupIntro = item.getString(TAG_GROUP_INTRO);
                String groupCity = item.getString(TAG_GROUP_CITY);
                String groupCounty = item.getString(TAG_GROUP_COUNTY);
                String groupDistrict = item.getString(TAG_GROUP_DISTRICT);

                // 각 List의 값들을 data 객체에 set 해줍니다.
                ManageJoinData data = new ManageJoinData();

                data.setGroupIdx(Integer.parseInt(groupIdx));
                data.setGroupDate(getDateFormat(memberDate, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd"));
                data.setGroupCategory(groupCategory);
                data.setGroupTitle(groupName);
                data.setGroupIntro(String.valueOf(Html.fromHtml((groupIntro).replaceAll("<img.+?>", ""))));
                data.setGroupLocation(groupCity + " " + groupCounty);

                //모임 멤버 수 정보 JSON 가져오기
                GetMemberCountData groupMemberTask = new GetMemberCountData();
                try {
                    int memberCount = Integer.parseInt(groupMemberTask.execute("http://" + IP_ADDRESS + "/db/group_member_count.php", groupIdx).get());
                    // 모임 가입 테이블에는 모임장이 포함되지 않으므로 +1을 해줘야 한다.
                    data.setGroupMember(memberCount + 1);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                // 각 값이 들어간 data를 adapter에 추가합니다.
                manageJoinAdapter.addItem(data);
            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            manageJoinAdapter.notifyDataSetChanged();

            if(manageJoinAdapter.listData.size() == 0){
                noneContent.setVisibility(View.VISIBLE);
            } else {
                noneContent.setVisibility(View.GONE);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }

    }

    //모임 게시판 정보 JSON 가져오기
    private class GetMemberCountData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ManageJoinActivity.this,
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
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String groupIdx = params[1];
            String postParameters = "groupIdx=" + groupIdx;

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

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
