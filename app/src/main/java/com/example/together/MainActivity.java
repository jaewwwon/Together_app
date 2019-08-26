package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.getDateWeek;
import static com.example.together.StaticInit.getToday;
import static com.example.together.StaticInit.loginUserId;
import static com.example.together.StaticInit.loginUserName;


public class MainActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "MainActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    TextView groupCreateBtn; // 모임 만들기 버튼
    TextView scheduleAllBtn; //일정 전체보기 버튼
    TextView groupAllBtn; //모임 전체보기 버튼
    List<String> groupInfoList; // 모임 정보 파싱 데이터 리스트
    List<Integer> groupMemberList; // 모임 멤버 정보 파싱 데이터 리스트
    List<String> attendMemberList; // 일정 참석 멤버 정보 파싱 데이터 리스트
    private ScheduleAdapter scheduleAdapter; //일정 어댑터
    private GroupAdapter groupAdapter; //모임 어댑터
    TextView homeNav; //하단메뉴 - 홈
    TextView manageNav; //하단메뉴 - 모임관리
    TextView settingNav; //하단메뉴 - 설정


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences에 저장된 로그인 정보를 전역변수에 저장한다.
        SharedPreferences preferences = getSharedPreferences("sFile", 0);
        loginUserId = preferences.getString("USER_LOGIN_ID", null);
        loginUserName = preferences.getString("USER_LOGIN_NAME", null);
        if (loginUserId != null) {
            Log.e(TAG, "로그인 id: " + loginUserId);
            Log.e(TAG, "로그인 name: " + loginUserName);

        }

        //초기화
        groupCreateBtn = findViewById(R.id.groupCreateBtn);
        scheduleAllBtn = findViewById(R.id.scheduleAllBtn);
        groupAllBtn = findViewById(R.id.groupAllBtn);
        homeNav = findViewById(R.id.homeNav);
        manageNav = findViewById(R.id.manageNav);
        settingNav = findViewById(R.id.settingNav);

        //하단메뉴 - 홈 버튼을 클릭했을 경우
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //하단메뉴 - 모임관리 버튼을 클릭했을 경우
        manageNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManageJoinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //하단메뉴 - 설정 버튼을 클릭했을 경우
        settingNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //모임 만들기 버튼을 클릭했을 경우
        groupCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateGroupCategoryActivity.class);
                startActivity(intent);
            }
        });

        //일정 전체보기 버튼을 클릭했을 경우
        scheduleAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchScheduleActivity.class);
                startActivity(intent);
            }
        });

        //모임 전체보기 버튼을 클릭했을 경우
        groupAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchGroupActivity.class);
                startActivity(intent);
            }
        });


        //모임 멤버 정보 JSON 파일 가져오기
        GetGroupMemberData groupMemberTask = new GetGroupMemberData();
        groupMemberTask.execute("http://" + IP_ADDRESS + "/db/group_member.php", "");

        //일정 참석 멤버 정보 JSON 파일 가져오기
        GetScheduleAttendData attendMemberTask = new GetScheduleAttendData();
        attendMemberTask.execute("http://" + IP_ADDRESS + "/db/schedule_attend.php", "");

        GroupInit(); //모임 목록 초기화
        //모임 정보 JSON 파일 가져오기
        GetGroupData groupTask = new GetGroupData();
        groupTask.execute("http://" + IP_ADDRESS + "/db/group_info.php", "");

        ScheduleInit(); //일정 목록 초기화
        //일정 정보 JSON 파일 가져오기
        GetScheduleData scheduleTask = new GetScheduleData();
        scheduleTask.execute("http://" + IP_ADDRESS + "/db/group_schedule.php", "");

    }

    //일정 목록 초기화
    private void ScheduleInit() {
        RecyclerView recyclerView = findViewById(R.id.scheduleList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);

        scheduleAdapter = new ScheduleAdapter();
        recyclerView.setAdapter(scheduleAdapter);
    }

    //일정 정보 JSON 가져오기
    private class GetScheduleData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                scheduleResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
//            String postParameters = params[1];
            String joinTxt = "LEFT JOIN group_info ON group_schedule.group_idx = group_info.group_idx";
            String postParameters = "whereTxt=" + "WHERE DATE(sc_date) >= '" + getToday("yyyy-MM-dd HH:mm:ss") + "' ORDER BY sc_date ASC LIMIT 0, 5" + "&joinTxt=" + joinTxt;

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

    //일정 정보 JSON 파싱 후, 데이터 저장하기
    private void scheduleResult() {

//        Log.e(TAG, "_모임정보 파싱: "+String.valueOf(groupInfoList));
//        Log.e(TAG, "_일정참석 수 파싱: "+String.valueOf(attendMemberList));


        String TAG_JSON = "groupSchedule";
        String TAG_SC_IDX = "scIdx"; //일정 index
        String TAG_SC_TITLE = "scTitle"; //일정 제목
        String TAG_SC_DATE = "scDate"; //일정 날짜
        String TAG_SC_LOCATION = "scLocation"; //일정 장소
        String TAG_GROUP_IDX = "groupIdx"; //모임 index
        String TAG_GROUP_CATEGORY = "groupCategory"; //모임 카테고리

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String scIdx = item.getString(TAG_SC_IDX);
                String scTitle = item.getString(TAG_SC_TITLE);
                String scDate = item.getString(TAG_SC_DATE);
                String scLocation = item.getString(TAG_SC_LOCATION);
                String groupIdx = item.getString(TAG_GROUP_IDX);
                String groupCategory = item.getString(TAG_GROUP_CATEGORY);

                ScheduleData data = new ScheduleData();

                data.setGroupCategory(groupCategory);
                // 날짜, 요일, 시간순
                data.setScheduleDate(getDateFormat(scDate, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd") + "(" + getDateWeek(scDate, "yyyy-MM-dd HH:mm:ss") + ") " + getDateFormat(scDate, "yyyy-MM-dd HH:mm:ss", "HH:mm"));
                data.setScheduleTitle(scTitle);
                data.setScheduleLocation(scLocation);

                if (attendMemberList.contains(scIdx)) {
                    int attendCont = 0;
                    for (int j = 0; j < attendMemberList.size(); j++) {
                        if (attendMemberList.get(j).equals(scIdx)) {
                            attendCont++;
//                            Log.e(TAG, "증가!! 일정 idx: " + scIdx);
                        }
                    }
                    data.setScheduleMember(attendCont);
                }
                data.setGroupIdx(Integer.parseInt(groupIdx));

                // listData.add(data);
                scheduleAdapter.addItem(data);
            }

            scheduleAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            Log.e(TAG, "scheduleResult : ", e);
        }

    }


    //일정 참석 멤버 정보 JSON 가져오기
    private class GetScheduleAttendData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                scheduleAttendResult();
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

    //일정 참석 멤버 정보 JSON 파싱 후, 데이터 저장하기
    private void scheduleAttendResult() {

        attendMemberList = new ArrayList<String>();

        String TAG_JSON = "scheduleAttend";
        String TAG_SC_IDX = "scIdx"; //일정 index

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String scIdx = item.getString(TAG_SC_IDX);

                attendMemberList.add(scIdx);
            }

        } catch (JSONException e) {

            Log.e(TAG, "scheduleAttendResult : ", e);
        }

    }

    //모임 멤버 정보 JSON 가져오기
    private class GetGroupMemberData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                groupMemberResult();
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

    //모임 멤버 정보 JSON 파싱 후, 데이터 저장하기
    private void groupMemberResult() {

        groupMemberList = new ArrayList<Integer>();

        String TAG_JSON = "groupMember";
        String TAG_GROUP_IDX = "groupIdx"; //모임 index

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String groupIdx = item.getString(TAG_GROUP_IDX);

                groupMemberList.add(Integer.valueOf(groupIdx));
            }

//            Log.e(TAG, "_모임 멤버 수 파싱: "+String.valueOf(groupMemberList));

        } catch (JSONException e) {

            Log.e(TAG, "groupMemberResult : ", e);
        }

    }

    //모임 목록 초기화
    private void GroupInit() {
        RecyclerView recyclerView = findViewById(R.id.groupList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);

        groupAdapter = new GroupAdapter();
        recyclerView.setAdapter(groupAdapter);
    }

    //모임 정보 JSON 가져오기
    private class GetGroupData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
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
                groupResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
//            String postParameters = params[1];
            String postParameters = "whereTxt=" + "ORDER BY group_idx DESC LIMIT 5";

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

    //모임 정보 JSON 파싱 후, 데이터 저장하기
    private void groupResult() {

        groupInfoList = new ArrayList<String>();

//        Log.e(TAG, "_모임 멤버 수 파싱: "+String.valueOf(groupMemberList));


        // 모임 목록 데이터 저장
        String TAG_JSON = "groupInfo";
        String TAG_GROUP_LIST_IDX = "groupIdx"; //모임 index
        String TAG_GROUP_CATEGORY = "groupCategory"; //모임 카테고리
        String TAG_GROUP_NAME = "groupName"; //모임 이름
        String TAG_GROUP_INTRO = "groupIntro"; //모임 소개
        String TAG_GROUP_IMG = "groupImg"; //모임 대표이미지
        String TAG_GROUP_CITY = "groupCity"; //모임 시도
        String TAG_GROUP_COUNTY = "groupCounty"; //모임 시군구
        String TAG_GROUP_DISTRICT = "groupDistrict"; //모임 읍면동

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                int groupIdx = Integer.parseInt(item.getString(TAG_GROUP_LIST_IDX));
                String groupCategory = item.getString(TAG_GROUP_CATEGORY);
                String groupName = item.getString(TAG_GROUP_NAME);
                String groupIntro = item.getString(TAG_GROUP_INTRO);
                String groupImg = item.getString(TAG_GROUP_IMG);
                String groupCity = item.getString(TAG_GROUP_CITY);
                String groupCounty = item.getString(TAG_GROUP_COUNTY);
                String groupDistrict = item.getString(TAG_GROUP_DISTRICT);

                GroupData data = new GroupData();
                data.setGroupIdx(groupIdx);
                data.setGroupCategory(groupCategory);
                data.setGroupTitle(groupName);
                data.setGroupIntro(String.valueOf(Html.fromHtml((groupIntro).replaceAll("<img.+?>", ""))));

                if (groupImg.length() == 0 || groupImg.equals("null")) {
                    data.setGroupThumbnail("http://www.togetherme.tk/static/images/bg_default_small.gif");
                } else {
                    data.setGroupThumbnail(groupImg);
                }

                data.setGroupLocation(groupCity + groupCounty + groupDistrict);

                if (groupMemberList.contains(groupIdx)) {
                    int memberCont = 0;
                    for (int j = 0; j < groupMemberList.size(); j++) {
                        if (groupMemberList.get(j).equals(groupIdx)) {
                            memberCont++;
                        }
                    }
                    data.setGroupMember(memberCont + 1);
                } else {
                    data.setGroupMember(1);
                }


                // listData.add(data);
                groupAdapter.addItem(data);


                groupInfoList.add(String.valueOf(groupIdx));
                groupInfoList.add(groupCategory);

            }

            groupAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            Log.d(TAG, "groupResult : ", e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


}
