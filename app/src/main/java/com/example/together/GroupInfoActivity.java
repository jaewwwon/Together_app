package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
import java.util.Arrays;
import java.util.List;

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.PAGE_GROUP_NAME;

public class GroupInfoActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupInfoActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; //json 데이터 파일
    private GroupInfoAdapter groupInfoAdapter; //모임정보 어댑터
    List<String> groupInfoList; //모임 정보 파싱 데이터 리스트
    List<String> userInfoList; //회원 정보 파싱 데이터 리스트
    List<Integer> groupMemberList; //모임 멤버 정보 파싱 데이터 리스트
    TextView pageGroupTit; //페이지 상단 모임 이름
    TextView infoTab; //정보탭 버튼
    TextView scheduleTab; //일정탭 버튼
    TextView boardTab; //게시판탭 버튼
    TextView photoTab; //사진첩탭 버튼
    TextView chatTab; //채팅탭 버튼
    ImageView groupThumbnail; //모임 대표이미지
    TextView groupCategory; //모임카테고리
    TextView groupLocation; //모임 장소
    TextView groupHost; //모임장
    TextView groupMember; //상단 모임멤버 수
    TextView groupTitle; //모임 이름
    TextView groupIntro; //모임 소개란
    TextView memberCount; //하단 모임멤버 수
    TextView hostName; //모임장 이름
    TextView hostIntro; //모임장 이름
    ImageView hostProfile; //모임장 프로필사진


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        //초기화
        pageGroupTit = findViewById(R.id.pageGroupTit);
        infoTab = findViewById(R.id.infoTab);
        scheduleTab = findViewById(R.id.scheduleTab);
        boardTab = findViewById(R.id.boardTab);
        photoTab = findViewById(R.id.photoTab);
        chatTab = findViewById(R.id.chatTab);
        groupThumbnail = findViewById(R.id.groupThumbnail);
        groupCategory = findViewById(R.id.groupCategory);
        groupLocation = findViewById(R.id.groupLocation);
        groupHost = findViewById(R.id.groupHost);
        groupMember = findViewById(R.id.groupMember);
        groupTitle = findViewById(R.id.groupTitle);
        groupIntro = findViewById(R.id.groupIntro);
        memberCount = findViewById(R.id.memberCount);
        hostName = findViewById(R.id.hostName);
        hostIntro = findViewById(R.id.hostIntro);
        hostProfile = findViewById(R.id.hostProfile);

        // 인텐트값 넘겨받기
//        Intent intent = getIntent();
//        PAGE_GROUP_INDEX = intent.getIntExtra("groupIdx", 0); // 모임 index

        Log.e(TAG, "모임 index: " + PAGE_GROUP_INDEX);

        //정보탭 버튼을 클릭했을 경우
        infoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, GroupInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //일정탭 버튼을 클릭했을 경우
        scheduleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, GroupScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //게시판탭 버튼을 클릭했을 경우
        boardTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, GroupBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //사진첩탭 버튼을 클릭했을 경우
        photoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, GroupPhotoActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //채팅탭 버튼을 클릭했을 경우
        chatTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, GroupChatActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        //회원 정보 JSON 파일 가져오기
        GetUserData userTask = new GetUserData();
        userTask.execute("http://" + IP_ADDRESS + "/db/user.php", "");

        //모임 정보 JSON 파일 가져오기
        GetGroupInfoData groupInfoTask = new GetGroupInfoData();
        groupInfoTask.execute("http://" + IP_ADDRESS + "/db/group_info.php", "");

        MemberInit(); //멤버 목록 초기화
        //모임 멤버 정보 JSON 파일 가져오기
        GetGroupMemberData groupMemberTask = new GetGroupMemberData();
        groupMemberTask.execute("http://" + IP_ADDRESS + "/db/group_member.php", "");

    }

    //멤버 목록 초기화
    private void MemberInit() {
        RecyclerView recyclerView = findViewById(R.id.memberList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false); // 스크롤 중복 방지
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        groupInfoAdapter = new GroupInfoAdapter();
        recyclerView.setAdapter(groupInfoAdapter);
    }


    //모임 정보 JSON 가져오기
    private class GetGroupInfoData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupInfoActivity.this,
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
                groupInfoResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
//            String postParameters = "groupIdx=" + PAGE_GROUP_INDEX;
            String postParameters = "groupIdx=" + PAGE_GROUP_INDEX + "&whereTxt=" + "";

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
    private void groupInfoResult() {

        String TAG_JSON = "groupInfo";
        String TAG_GROUP_IDX = "groupIdx"; //모임 index
        String TAG_GROUP_NAME = "groupName"; //모임이름
        String TAG_GROUP_CATEGORY = "groupCategory"; //모임 카테고리
        String TAG_GROUP_LOCATION = "groupLocation"; //모임 장소
        String TAG_GROUP_INTRO = "groupIntro"; //모임 소개란
        String TAG_GROUP_HOST = "groupHost"; //모임장
        String TAG_GROUP_IMG = "groupImg"; //모임 대표이미지

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                if (item.getString(TAG_GROUP_IDX).equals(String.valueOf(PAGE_GROUP_INDEX))) {
//                    String groupName = ;
//                    String groupLocation = item.getString(TAG_GROUP_LOCATION);
//                    String groupIntro = item.getString(TAG_GROUP_INTRO);
//                    String groupHost = item.getString(TAG_GROUP_HOST);
//                    String groupImg = item.getString(TAG_GROUP_IMG);

                    // 페이지 상단 모임명 입력
                    PAGE_GROUP_NAME = item.getString(TAG_GROUP_NAME);
                    pageGroupTit.setText(item.getString(TAG_GROUP_NAME));
                    // 모임 대표 이미지 입력
                    if (item.getString(TAG_GROUP_IMG) != null && !item.getString(TAG_GROUP_IMG).isEmpty() && !item.getString(TAG_GROUP_IMG).equals("null")) {
                        Glide.with(this)
                                .load(item.getString(TAG_GROUP_IMG))
                                .override(500, 300)
                                .into(groupThumbnail);
                    } else {
                        Glide.with(this)
                                .load("http://www.togetherme.tk/static/images/bg_default_large.gif")
                                .override(500, 300)
                                .into(groupThumbnail);
                    }

                    // 모임 카테고리 입력
                    groupCategory.setText(item.getString(TAG_GROUP_CATEGORY));
                    // 모임명 입력
                    groupTitle.setText(item.getString(TAG_GROUP_NAME));
                    // 모임 소개 입력
                    //TODO 이미지 태그 안보이는거랑 글씨 간격 해결할 것
                    groupIntro.setText(Html.fromHtml(item.getString(TAG_GROUP_INTRO)));
                    // 모임 장소 입력
                    groupLocation.setText(item.getString(TAG_GROUP_LOCATION));

                    if (userInfoList.contains(item.getString(TAG_GROUP_HOST))) {
                        groupHost.setText(userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 1));
                        hostName.setText(userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 1));

                        if (userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 2) != null && !userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 2).isEmpty() && !userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 2).equals("null")) {
                            hostIntro.setText(userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 2));
                        } else {
                            hostIntro.setText("");
                        }

//                        if (userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 3) != null) {
                        if (userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 3) != null && !userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 3).isEmpty() && !userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 3).equals("null")) {
                            Glide.with(this)
                                    .load(userInfoList.get(userInfoList.indexOf(item.getString(TAG_GROUP_HOST)) + 3))
                                    .override(500, 500)
                                    .into(hostProfile);
                        } else {
                            Glide.with(this)
                                    .load("http://www.togetherme.tk/static/images/icon_profile.png")
                                    .override(200, 200)
                                    .into(hostProfile);
                        }

                    }
                }
            }


        } catch (JSONException e) {
            Log.e(TAG, "groupInfoResult : ", e);
        }

    }

    private class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupInfoActivity.this,
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

    private void userInfoResult() {

        userInfoList = new ArrayList<String>();

        String TAG_JSON = "user";
        String TAG_USER_EMAIL = "userEmail"; //회원 이메일
        String TAG_USER_NAME = "userName"; //회원 이름
        String TAG_USER_INTRO = "userIntro"; //회원 소개란
        String TAG_USER_PROFILE = "userProfile"; //회원 프로필사진

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String userEmail = item.getString(TAG_USER_EMAIL);
                String userName = item.getString(TAG_USER_NAME);
                String userIntro = item.getString(TAG_USER_INTRO);
                String userProfile = item.getString(TAG_USER_PROFILE);

                userInfoList.add(userEmail);
                userInfoList.add(userName);
                userInfoList.add(userIntro);
                userInfoList.add(userProfile);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }
    }

    private class GetGroupMemberData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupInfoActivity.this,
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
                groupMemberResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "groupIdx=" + PAGE_GROUP_INDEX;

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

    private void groupMemberResult() {

        String TAG_JSON = "groupMember";
        String TAG_GROUP_IDX = "groupIdx"; //모임 index
        String TAG_MEMBER_USER = "memberUser"; //회원 이메일

        // 사용자 정보 리스트안(userInfoList)에 있는 이메일 정보의 index 값이다.
        // 추가로 사용자 정보 리스트의 저장값 순서는 (이메일, 이름, 소개란, 프로필사진) 이다.
        int userEmailIdx;
//        Log.e(TAG, "사용자 정보 리스트: " + userInfoList);

        int memberNum = 0; //모임 멤버 수

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String groupIdx = item.getString(TAG_GROUP_IDX);
                String memberEmail = item.getString(TAG_MEMBER_USER);
                userEmailIdx = userInfoList.indexOf(memberEmail);

//                Log.e(TAG, groupIdx.equals(String.valueOf(PAGE_GROUP_INDEX)) + "");

                if (groupIdx.equals(String.valueOf(PAGE_GROUP_INDEX))) {

                    memberNum++;

                    // 각 List의 값들을 data 객체에 set 해줍니다.
                    GroupInfoData data = new GroupInfoData();

                    if (userInfoList.contains(memberEmail)) {
                        data.setUserName(userInfoList.get(userEmailIdx + 1));
                        if (userInfoList.get(userEmailIdx + 2) != null && !userInfoList.get(userEmailIdx + 2).isEmpty() && !userInfoList.get(userEmailIdx + 2).equals("null")) {
                            data.setUserIntro(userInfoList.get(userEmailIdx + 2));
                        } else {
                            data.setUserIntro("");
                        }
//                        if (userInfoList.get(userEmailIdx + 3) == null || userInfoList.get(userEmailIdx + 3).equals("") || userInfoList.get(userEmailIdx + 3).equals(" ")) {
                        if (userInfoList.get(userEmailIdx + 3) != null && !userInfoList.get(userEmailIdx + 3).isEmpty() && !userInfoList.get(userEmailIdx + 3).equals("null")) {
//                            Log.e(TAG + "_프로필 O", userInfoList.get(userEmailIdx + 3));
                            data.setUserProfile(userInfoList.get(userEmailIdx + 3));
                        } else {
//                            Log.e(TAG + "_프로필 X", userInfoList.get(userEmailIdx + 3));
                            data.setUserProfile("http://www.togetherme.tk/static/images/icon_profile.png");
                        }
                    }

                    groupMember.setText(String.valueOf(memberNum + 1)); //상단 멤버 수 정보
                    memberCount.setText(String.valueOf(memberNum + 1)); //하단 멤버 수 정보

                    // 각 값이 들어간 data를 adapter에 추가합니다.
                    groupInfoAdapter.addItem(0, data);
                }

            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            groupInfoAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
