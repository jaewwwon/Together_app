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
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.together.StaticInit.PAGE_GROUP_HOST;
import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.PAGE_GROUP_NAME;
import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.loginUserId;

public class GroupBoardActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupBoardActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    static GroupBoardAdapter groupBoardAdapter; //게시판 어댑터
    private ArrayList<GroupBoardData> listData;
    List<String> groupMemberList; //모임 멤버 정보 파싱 데이터 리스트
    List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트
    TextView pageGroupTit; //페이지 상단 모임 이름
    TextView infoTab; //정보탭 버튼
    TextView scheduleTab; //일정탭 버튼
    TextView boardTab; //게시판탭 버튼
    TextView photoTab; //사진첩탭 버튼
    TextView chatTab; //채팅탭 버튼
    ImageView scheduleAddBtn; //일정 추가 버튼
    TextView boardCateSelect; //말머리 선택 버튼
    ImageView boardCateSelectImg; //말머리 선택 버튼 이미지
    TextView boardNoticeBtn; //공지사항 버튼
    ImageView boardAddBtn; //게시글 추가 버튼
    TextView noneContent; //컨텐츠가 없을 경우 표시되는 문구

    private int PAGE_LOAD_NUM = 0; //페이지 로드 수


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_board);

//        Log.e(TAG, "모임 index: " + PAGE_GROUP_INDEX);

        //초기화
        pageGroupTit = findViewById(R.id.pageGroupTit);
        infoTab = findViewById(R.id.infoTab);
        scheduleTab = findViewById(R.id.scheduleTab);
        boardTab = findViewById(R.id.boardTab);
        photoTab = findViewById(R.id.photoTab);
        chatTab = findViewById(R.id.chatTab);
        scheduleAddBtn = findViewById(R.id.scheduleAddBtn);
        boardCateSelect = findViewById(R.id.boardCateSelect);
        boardCateSelectImg = findViewById(R.id.boardCateSelectImg);
        boardNoticeBtn = findViewById(R.id.boardNoticeBtn);
        boardAddBtn = findViewById(R.id.boardAddBtn);
        noneContent = findViewById(R.id.noneContent);


        //페이지 모임 이름 설정
        pageGroupTit.setText(PAGE_GROUP_NAME);

        //정보탭 버튼을 클릭했을 경우
        infoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardActivity.this, GroupInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //일정탭 버튼을 클릭했을 경우
        scheduleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardActivity.this, GroupScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //게시판탭 버튼을 클릭했을 경우
        boardTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardActivity.this, GroupBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //사진첩탭 버튼을 클릭했을 경우
        photoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardActivity.this, GroupPhotoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //채팅탭 버튼을 클릭했을 경우
        chatTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardActivity.this, GroupChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //게시글 작성 버튼을 클릭했을 경우
        boardAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupBoardActivity.this, GroupBoardWriteActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        PAGE_LOAD_NUM = 0;

        //모임 멤버 정보 JSON 가져오기
        GetgroupMemberData groupMemberTask = new GetgroupMemberData();
        groupMemberTask.execute("http://" + IP_ADDRESS + "/db/group_member.php", "");

        BoardInit(); //게시글 목록 초기화
        //모임 게시글 정보 JSON 가져오기
        GetGroupBoardData groupBoardTask = new GetGroupBoardData();
        groupBoardTask.execute("http://" + IP_ADDRESS + "/db/group_board.php", "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    //게시판 목록 초기화
    private void BoardInit() {
        RecyclerView recyclerView = findViewById(R.id.boardList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false); // 스크롤 중복 방지

        groupBoardAdapter = new GroupBoardAdapter();
        recyclerView.setAdapter(groupBoardAdapter);

        // Pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount();

                //리스트 마지막(바닥) 도착!!!!! 다음 페이지 데이터 로드!!
                if (lastVisibleItemPosition == itemTotalCount - 1) {

                    //모임 게시글 정보 JSON 가져오기
                    GetGroupBoardData groupBoardTask = new GetGroupBoardData();
                    groupBoardTask.execute("http://" + IP_ADDRESS + "/db/group_board.php", String.valueOf(PAGE_GROUP_INDEX));
                }

            }
        });
    }

    //모임 게시판 정보 JSON 가져오기
    private class GetGroupBoardData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.e(TAG, "response - " + result);
//            Log.e(TAG, "response jsonString - " + jsonString);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {

                jsonString = result;
                groupBoardResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            PAGE_LOAD_NUM += 10;

            Log.e(TAG, "요청하는 아이템 수 : " + PAGE_LOAD_NUM);

            String serverURL = params[0];
            String postParameters = "groupIdx=" + PAGE_GROUP_INDEX + "&whereTxt=" + "ORDER BY board_idx DESC" + "&joinUser=" + "LEFT JOIN user ON group_board.board_user = user.user_email" + "&limitTxt=" + PAGE_LOAD_NUM;


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
    private void groupBoardResult() {

        String TAG_JSON = "groupBoard";
        String TAG_BOARD_CATEGORY = "boardCategory"; //게시글 카테고리
        String TAG_BOARD_TITLE = "boardTitle"; //게시글 제목
        String TAG_BOARD_DATE = "boardDate"; //게시글 작성일
        String TAG_BOARD_VIEW = "boardView"; //게시글 조회수
        String TAG_BOARD_IDX = "boardIdx"; //게시글 index
        String TAG_USER_NAME = "userName"; //작성자 이름

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String boardCategory = item.getString(TAG_BOARD_CATEGORY);
                String boardTitle = item.getString(TAG_BOARD_TITLE);
                String boardDate = item.getString(TAG_BOARD_DATE);
                String boardView = item.getString(TAG_BOARD_VIEW);
                String boardIdx = item.getString(TAG_BOARD_IDX);
                String userName = item.getString(TAG_USER_NAME);

                // 각 List의 값들을 data 객체에 set 해줍니다.
                GroupBoardData data = new GroupBoardData();
                data.setBoardTitle("[" + boardCategory + "] " + boardTitle); //게시글 카테고리 + 제목

                // TODO 회원탈퇴시에 해당 게시글을 어떻게 처리할지 고민해봐야 함
                if (userName.equals("null") || userName.equals(" ") || userName.equals("")) {
                    data.setBoardUser("탈퇴회원"); //게시글 작성자 이름
                } else {
                    data.setBoardUser(userName); //게시글 작성자 이름
                }

                //만약 작성일이 오늘날짜라면 시간만 보이도록 출력하고,
                //작성일이 오늘날짜가 아니라면 년,월,일 형식으로 출력한다.
                if (StaticInit.getToday("yyyy-MM-dd").equals(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"))) {
                    data.setBoardDate(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "HH:mm")); //게시글 작성시간
                } else {
                    data.setBoardDate(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd")); //게시글 작성날짜
                }

                data.setBoardView(Integer.parseInt(boardView)); //게시글 조회수

                data.setBoardIdx(Integer.parseInt(boardIdx)); //게시글 index


                //모임 게시글 정보 JSON 가져오기
                String commentCount = ""; //게시글 댓글 수
                CommentCountData commentTask = new CommentCountData();
                try {
                    commentCount = commentTask.execute("http://" + IP_ADDRESS + "/db/board_comment_count.php", boardIdx).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                data.setBoardComment(Integer.parseInt(commentCount));

                // 각 값이 들어간 data를 adapter에 추가합니다.
                groupBoardAdapter.addItem(data);

            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            groupBoardAdapter.notifyDataSetChanged();

//            Log.e(TAG, "목록 아이템 수: " + groupBoardAdapter.listData.size());

            if (groupBoardAdapter.listData.size() == 0) {
                noneContent.setVisibility(View.VISIBLE);
            } else {
                noneContent.setVisibility(View.GONE);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }

    }

    //게시판 댓글 정보 JSON 가져오기
    private class CommentCountData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
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
                groupBoardResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String boardIdx = params[1];
            String postParameters = "boardIdx=" + boardIdx;

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

    //모임 게시판 정보 JSON 가져오기
    private class GetgroupMemberData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.e(TAG, "response - " + result);
//            Log.e(TAG, "response jsonString - " + jsonString);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {

                jsonString = result;
                groupMemberResult();


                //로그인한 이메일과 모임장 또는 모임가입 회원의 이메일이 다를 경우에는 게시글 추가 버튼을 나타낸다.
                Log.e(TAG, "로그인 ID: " + loginUserId);
                Log.e(TAG, "모임장 ID: " + PAGE_GROUP_HOST);
                if (PAGE_GROUP_HOST.equals(loginUserId) || groupMemberList.contains(loginUserId)) {
                    boardAddBtn.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(GroupBoardActivity.this, "모임멤버만 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
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

    //모임 게시판 정보 JSO 파싱 후, 데이터 저장하기
    private void groupMemberResult() {

        groupMemberList = new ArrayList<>();

        String TAG_JSON = "groupMember"; //멤버 이메일
        String TAG_MEMBER_USER = "memberUser"; //멤버 이메일

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String memberEmail = item.getString(TAG_MEMBER_USER);
                groupMemberList.add(memberEmail);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }

    }


}
