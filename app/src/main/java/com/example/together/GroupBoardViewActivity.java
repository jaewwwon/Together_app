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
import android.widget.ImageButton;
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
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.PAGE_GROUP_NAME;
import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.getToday;

public class GroupBoardViewActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupBoardViewActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    static GroupBoardViewAdapter groupBoardViewAdapter; //게시판 어댑터
    private ArrayList<GroupBoardViewData> listData;
    //전달받은 intent 값 저장
    int itemPosition; //아이템 위치값
    int itemSize; //아이템 사이즈
    int boardIdx; //현재 게시글 index 값
    List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트
    List<String> boardInfoList = new ArrayList<String>(); // 게시글 정보 리스트
    TextView pageGroupTit; //페이지 상단 모임 이름
    TextView infoTab; //정보탭 버튼
    TextView scheduleTab; //일정탭 버튼
    TextView boardTab; //게시판탭 버튼
    TextView photoTab; //사진첩탭 버튼
    TextView chatTab; //채팅탭 버튼
    TextView boardTitle; //게시글 제목
    ImageView userProfile; //게시글 작성자 프로필
    TextView userName; //게시글 작성자 이름
    TextView boardDate; //게시글 작성일
    TextView boardView; //게시글 조회수
    TextView boardContent; //게시글 내용
    TextView commentCount; //댓글 총 개수
    ImageButton boardUtilBtn; //게시글 수정/삭제 버튼


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_board_view);

        Log.e(TAG, "모임 index: " + PAGE_GROUP_INDEX);

        //초기화
        pageGroupTit = findViewById(R.id.pageGroupTit);
        infoTab = findViewById(R.id.infoTab);
        scheduleTab = findViewById(R.id.scheduleTab);
        boardTab = findViewById(R.id.boardTab);
        photoTab = findViewById(R.id.photoTab);
        chatTab = findViewById(R.id.chatTab);
        boardTitle = findViewById(R.id.boardTitle);
        userProfile = findViewById(R.id.userProfile);
        userName = findViewById(R.id.userName);
        boardDate = findViewById(R.id.boardDate);
        boardView = findViewById(R.id.boardView);
        boardContent = findViewById(R.id.boardContent);
        commentCount = findViewById(R.id.commentCount);
        boardUtilBtn = findViewById(R.id.boardUtilBtn);

        //페이지 모임 이름 설정
        pageGroupTit.setText(PAGE_GROUP_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

        // 인텐트값 넘겨받기
        Intent intent = getIntent();
        itemPosition = intent.getIntExtra("itemPositionOri", 0); //아이템 위치값
        itemSize = intent.getIntExtra("itemSizeOri", 0); //아이템 사이즈
        boardIdx = intent.getIntExtra("boardIdxOri", 0); //게시글 index
        Log.e(TAG, "게시글 index 정보: " + boardIdx);


        //정보탭 버튼을 클릭했을 경우
        infoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardViewActivity.this, GroupInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //일정탭 버튼을 클릭했을 경우
        scheduleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardViewActivity.this, GroupScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //게시판탭 버튼을 클릭했을 경우
        boardTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardViewActivity.this, GroupBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //사진첩탭 버튼을 클릭했을 경우
        photoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardViewActivity.this, GroupPhotoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //채팅탭 버튼을 클릭했을 경우
        chatTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardViewActivity.this, GroupChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //게시글 수정/삭제 버튼을 클릭했을 경우
        boardUtilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardViewActivity.this, PopupBoardUtilActivity.class);
                intent.putExtra("itemPositionOri", itemPosition); //해당 게시글 아이템 위치값
                intent.putExtra("itemSizeOri", itemSize); //해당 게시글 아이템 사이즈
                intent.putExtra("boardIdxOri", boardIdx); //해당 게시글 index
//                startActivity(intent);
                startActivityForResult(intent, 5000);
                overridePendingTransition(0, 0);
            }
        });


        //조회수 요청하기
        GetBoardViewData viewTask = new GetBoardViewData();
        viewTask.execute("http://" + IP_ADDRESS + "/db/board_view_cookie.php", String.valueOf(boardIdx), String.valueOf(PAGE_GROUP_INDEX));


        // 회원 정보 가져오기
        GetUserData userTask = new GetUserData();
        userTask.execute("http://" + IP_ADDRESS + "/db/user.php", "");

        // 게시글 정보 가져오기
        GetGroupBoardData boardTask = new GetGroupBoardData();
        boardTask.execute("http://" + IP_ADDRESS + "/db/group_board.php", "");


        BoardCommentInit(); //게시판 댓글 목록 초기화
        //게시글 댓글 정보 JSON 가져오기
        GetBoardCommentData boardCommentTask = new GetBoardCommentData();
        boardCommentTask.execute("http://" + IP_ADDRESS + "/db/board_comment.php", "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 5000:
                    finish();
                    break;
            }
        }
    }

    //게시판 댓글 목록 초기화
    private void BoardCommentInit() {
        RecyclerView recyclerView = findViewById(R.id.commentList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false); // 스크롤 중복 방지

        groupBoardViewAdapter = new GroupBoardViewAdapter();
        recyclerView.setAdapter(groupBoardViewAdapter);
    }

    //조회수 요청 보내기
    private class GetBoardViewData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardViewActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 boardCommentResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.e(TAG, "response - " + result);

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
            String boardIdx = params[1];
            String groupIdx = params[2];
            String postParameters = "board=" + boardIdx + "&group=" + groupIdx;

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

    //모임 게시판 정보 JSON 가져오기
    private class GetBoardCommentData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardViewActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 boardCommentResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);
//            Log.e(TAG, "response jsonString - " + jsonString);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {

                jsonString = result;
                boardCommentResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "boardIdx=" + boardIdx + "&whereTxt=" + "";

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

    //모임 댓글 정보 JSO 파싱 후, 데이터 저장하기
    private void boardCommentResult() {

        String TAG_JSON = "boardComment";
        String TAG_COMMENT_USER = "commUser"; //댓글 작성자
        String TAG_COMMENT_CONTENT = "commContent"; //댓글 내용
        String TAG_COMMENT_DATE = "commDate"; //댓글 작성일
        String TAG_COMMENT_PARENT = "commParent"; //부모 댓글 index(대댓글 여부. 대댓글이 아닐경우 NULL 값)

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String commUser = item.getString(TAG_COMMENT_USER);
                String commContent = item.getString(TAG_COMMENT_CONTENT);
                String commDate = item.getString(TAG_COMMENT_DATE);
                String commParent = item.getString(TAG_COMMENT_PARENT);

                // 각 List의 값들을 data 객체에 set 해줍니다.
                GroupBoardViewData data = new GroupBoardViewData();

                //TODO 대댓글 표시 고민해보기
                // 대댓글인 경우
                if (!commParent.equals("null")) {
                    Log.e(TAG, "대댓글 이다");
                }
                if (userInfoList.contains(commUser)) {
                    data.setUserProfile(userInfoList.get(userInfoList.indexOf(commUser) + 2)); //댓글 작성자 프로필
                    data.setUserName(userInfoList.get(userInfoList.indexOf(commUser) + 1)); //댓글 작성자 이름
                } else {
                    data.setUserProfile("http://www.togetherme.tk/static/images/icon_profile.png");
                    data.setUserName("탈퇴회원"); //게시글 작성자 이름
                }

                //만약 작성일이 오늘날짜라면 시간만 보이도록 출력하고,
                //작성일이 오늘날짜가 아니라면 년,월,일 형식으로 출력한다.
                if (StaticInit.getToday("yyyy-MM-dd").equals(getDateFormat(commDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"))) {
                    data.setCommentDate(getDateFormat(commDate, "yyyy-MM-dd HH:mm:ss", "HH:mm")); //댓글 작성시간
                } else {
                    data.setCommentDate(getDateFormat(commDate, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd")); //댓글 작성날짜
                }

                data.setCommentContent(commContent); //댓글 내용

                // 댓글 총 개수 입력
                commentCount.setText("(" + groupBoardViewAdapter.listData.size() + ")");


                // 각 값이 들어간 data를 adapter에 추가합니다.
                groupBoardViewAdapter.addItem(0, data);
            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            groupBoardViewAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }

    }

    //회원 정보 JSON 파싱 후, 데이터 저장하기
    private class GetUserData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardViewActivity.this,
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
        String TAG_USER_EMAIL = "userEmail"; //회원 이메일
        String TAG_USER_NAME = "userName"; //회원 이름
        String TAG_USER_PROFILE = "userProfile"; //회원 프로필

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String userEmail = item.getString(TAG_USER_EMAIL);
                String userName = item.getString(TAG_USER_NAME);
                String userProfile = item.getString(TAG_USER_PROFILE);

                userInfoList.add(userEmail);
                userInfoList.add(userName);
                userInfoList.add(userProfile);
            }

        } catch (JSONException e) {
            Log.e(TAG, "showResult : ", e);
        }
    }

    //회원 정보 JSO 파싱 후, 데이터 저장하기
    private class GetGroupBoardData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardViewActivity.this,
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
                groupBoardResult();

                //게시글 정보 입력
                //boardInfoList에 게시글 카테고리, 제목, 내용, 작성자이메일, 작성일, 조회수 순으로 저장
//                Log.e(TAG, "게시글 정보: " + boardInfoList);
//                Log.e(TAG, "사용자 정보: " + userInfoList);
                boardTitle.setText("[" + boardInfoList.get(0) + "] " + boardInfoList.get(1)); //게시글 제목
                boardContent.setText(Html.fromHtml(boardInfoList.get(2))); //게시글 내용

                if (userInfoList.contains(boardInfoList.get(3))) { //게시글 작성자
                    Glide.with(GroupBoardViewActivity.this)
                            .load(userInfoList.get(userInfoList.indexOf(boardInfoList.get(3)) + 2))
                            .override(500, 500)
                            .into(userProfile);
                    userName.setText(userInfoList.get(userInfoList.indexOf(boardInfoList.get(3)) + 1)); //댓글 작성자 이름
                } else {
                    Glide.with(GroupBoardViewActivity.this)
                            .load("http://www.togetherme.tk/static/images/icon_profile.png")
                            .override(500, 500)
                            .into(userProfile);
                    userName.setText("탈퇴회원"); //게시글 작성자 이름
                }

                //만약 작성일이 오늘날짜라면 시간만 보이도록 출력하고,
                //작성일이 오늘날짜가 아니라면 년,월,일 형식으로 출력한다.
                if (getToday("yyyy-MM-dd").equals(getDateFormat(boardInfoList.get(4), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"))) {
                    boardDate.setText(getDateFormat(boardInfoList.get(4), "yyyy-MM-dd HH:mm:ss", "HH:mm")); //게시글 작성시간
                } else {
                    boardDate.setText(getDateFormat(boardInfoList.get(4), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd")); //게시글 작성날짜
                }

                boardView.setText(boardInfoList.get(5)); //조회수
            }

        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "boardIdx=" + boardIdx + "&whereTxt=" + "";

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

    private void groupBoardResult() {
        String TAG_JSON = "groupBoard";
        String TAG_BOARD_CATEGORY = "boardCategory"; //게시글 카테고리
        String TAG_BOARD_TITLE = "boardTitle"; //게시글 제목
        String TAG_BOARD_CONTENT = "boardContent"; //게시글 내용
        String TAG_BOARD_USER = "boardUser"; //게시글 작성자
        String TAG_BOARD_DATE = "boardDate"; //게시글 작성일
        String TAG_BOARD_VIEW = "boardView"; //게시글 조회수

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String boardCategory = item.getString(TAG_BOARD_CATEGORY);
                String boardTitle = item.getString(TAG_BOARD_TITLE);
                String boardContent = item.getString(TAG_BOARD_CONTENT);
                String boardUser = item.getString(TAG_BOARD_USER);
                String boardDate = item.getString(TAG_BOARD_DATE);
                String boardView = item.getString(TAG_BOARD_VIEW);

                //게시글 카테고리, 제목, 내용, 작성자이메일, 작성일, 조회수 순으로 저장
                boardInfoList.add(boardCategory);
                boardInfoList.add(boardTitle);
                boardInfoList.add(boardContent);
                boardInfoList.add(boardUser);
                boardInfoList.add(boardDate);
                boardInfoList.add(boardView);
            }

        } catch (JSONException e) {
            Log.e(TAG, "showResult : ", e);
        }
    }




}
