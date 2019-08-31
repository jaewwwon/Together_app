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
import android.widget.Button;
import android.widget.EditText;
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
import static com.example.together.StaticInit.loginUserId;

public class GroupBoardViewActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupBoardViewActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    private GroupBoardViewAdapter groupBoardViewAdapter; //게시판 어댑터
    private ArrayList<GroupBoardViewData> listData;
    //전달받은 intent 값 저장
    int itemPosition; //아이템 위치값
    int itemSize; //아이템 사이즈
    int boardIdx; //현재 게시글 index 값
    List<String> boardInfoList; // 게시글 정보 리스트
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
    TextView commentButton; //댓글 작성 버튼
    View commentArea; //댓글 작성 영역
    ImageView closeButton; //댓글 작성 영역 닫기 버튼
    EditText inputComment; //댓글 작성란
    Button commentSubmit; //댓글 작성 버튼
    TextView noneContent; //댓글이 없을경우 보여주는 문구


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
        commentButton = findViewById(R.id.commentButton);
        commentArea = findViewById(R.id.commentArea);
        closeButton = findViewById(R.id.closeButton);
        inputComment = findViewById(R.id.inputComment);
        commentSubmit = findViewById(R.id.commentSubmit);
        noneContent = findViewById(R.id.noneContent);

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


        //댓글쓰기 버튼을 눌렀을 경우
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 댓글 작성 영역 활성화
                commentArea.setVisibility(View.VISIBLE);
            }
        });

        //댓글작성란 닫기 버튼을 눌렀을 경우
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 댓글 작성 영역 비활성화
                commentArea.setVisibility(View.GONE);
                // 댓글 입력란 초기화
                inputComment.setText("");
            }
        });

        //댓글작성 영역의 댓글 작성 버튼을 눌렀을 경우
        commentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //댓글 작성 정보 보내기
                CommentWriteData commentTask = new CommentWriteData();
                //작성자 이메일, 댓글내용, 게시글 index 순서
                commentTask.execute("http://" + IP_ADDRESS + "/group/comment_write.php", loginUserId, inputComment.getText().toString(), String.valueOf(boardIdx));

                // 댓글 작성 영역 비활성화
                commentArea.setVisibility(View.GONE);
                // 댓글 입력란 초기화
                inputComment.setText("");

                Intent intent = new Intent(GroupBoardViewActivity.this, GroupBoardViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("itemPositionOri", itemPosition); //아이템 위치값
                intent.putExtra("itemSizeOri", itemSize); //아이템 사이즈
                intent.putExtra("boardIdxOri", boardIdx); //게시글 index
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        //조회수 요청하기
        GetBoardViewData viewTask = new GetBoardViewData();
        viewTask.execute("http://" + IP_ADDRESS + "/db/board_view_cookie.php", String.valueOf(boardIdx), String.valueOf(PAGE_GROUP_INDEX));


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

    //댓글 작성 정보 보내기
    private class CommentWriteData extends AsyncTask<String, Void, String> {

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
            String userEmail = params[1];
            String commContent = params[2];
            String boardIdx = params[3];

            //작성자 이메일, 댓글내용, 게시글 index 순서
            String postParameters = "userEmail=" + userEmail + "&commContent=" + commContent + "&boardIdx=" + boardIdx;

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
            String postParameters = "boardIdx=" + boardIdx + "&whereTxt=" + "ORDER BY comm_idx DESC";

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
        String TAG_USER_NAME = "userName"; //댓글 작성자 이름
        String TAG_USER_PROFILE = "userProfile"; //댓글 작성자 이름

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String commUser = item.getString(TAG_COMMENT_USER);
                String commContent = item.getString(TAG_COMMENT_CONTENT);
                String commDate = item.getString(TAG_COMMENT_DATE);
                String commParent = item.getString(TAG_COMMENT_PARENT);
                String userName = item.getString(TAG_USER_NAME);
                String userProfile = item.getString(TAG_USER_PROFILE);

                // 각 List의 값들을 data 객체에 set 해줍니다.
                GroupBoardViewData data = new GroupBoardViewData();

                //TODO 대댓글 표시 고민해보기
                // 대댓글인 경우
                if (!commParent.equals("null")) {
                    Log.e(TAG, "대댓글 이다");
                }

                if (userName.equals("null") || userName.equals("") || userName.equals(" ")) {
                    data.setUserName("탈퇴회원"); //게시글 작성자 이름
                } else {
                    data.setUserName(userName); //댓글 작성자 이름
                    data.setUserEmail(commUser); //게시글 작성자 이메일
                }

                if (userProfile.equals("null") || userProfile.equals("") || userProfile.equals(" ")) {
                    data.setUserProfile("http://www.togetherme.tk/static/images/icon_profile.png");
                } else {
                    data.setUserProfile(userProfile); //댓글 작성자 프로필
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
                commentCount.setText("(" + jsonArray.length() + ")");


                // 각 값이 들어간 data를 adapter에 추가합니다.
                groupBoardViewAdapter.addItem(data);
            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            groupBoardViewAdapter.notifyDataSetChanged();


            //댓글이 없을 경우, 댓글이 없다는 문구를 보여준다.
            if(groupBoardViewAdapter.listData.size() == 0){
                noneContent.setVisibility(View.VISIBLE);
            } else {
                noneContent.setVisibility(View.GONE);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }

    }


    //해당 게시글의 정보를 불러온다.
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
                //게시글 카테고리, 제목, 내용, 작성자, 작성일, 조회수, 작성자 프로필, 작성자 이메일 순으로 저장
//                Log.e(TAG, "게시글 정보: " + boardInfoList);
                boardTitle.setText("[" + boardInfoList.get(0) + "] " + boardInfoList.get(1)); //게시글 카테고리 및 제목
                boardContent.setText(Html.fromHtml(boardInfoList.get(2))); //게시글 내용
                userName.setText(boardInfoList.get(3)); //작성자 이름

                if(boardInfoList.get(6).equals("null")){
                    Glide.with(GroupBoardViewActivity.this)
                            .load("http://www.togetherme.tk/static/images/icon_profile.png")
                            .override(500, 500)
                            .into(userProfile); //작성자 프로필사진
                } else {
                    Glide.with(GroupBoardViewActivity.this)
                            .load(boardInfoList.get(6))
                            .override(500, 500)
                            .into(userProfile); //작성자 프로필사진
                }

                //만약 작성일이 오늘날짜라면 시간만 보이도록 출력하고,
                //작성일이 오늘날짜가 아니라면 년,월,일 형식으로 출력한다.
                if (getToday("yyyy-MM-dd").equals(getDateFormat(boardInfoList.get(4), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"))) {
                    boardDate.setText(getDateFormat(boardInfoList.get(4), "yyyy-MM-dd HH:mm:ss", "HH:mm")); //게시글 작성시간
                } else {
                    boardDate.setText(getDateFormat(boardInfoList.get(4), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd")); //게시글 작성날짜
                }

                boardView.setText(boardInfoList.get(5)); //조회수

                //게시글 작성자와 로그인한 이메일이 다를 경우, 게시글 수정버튼을 숨긴다
                if(!boardInfoList.get(7).equals(loginUserId)) {
                    boardUtilBtn.setVisibility(View.GONE);
                }
            }

        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "boardIdx=" + boardIdx + "&whereTxt=" + "" + "&joinUser=" + "LEFT JOIN user ON group_board.board_user = user.user_email";

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

        //파싱한 게시글 정보를 저장하는 리스트
        boardInfoList = new ArrayList<String>();

        String TAG_JSON = "groupBoard";
        String TAG_BOARD_CATEGORY = "boardCategory"; //게시글 카테고리
        String TAG_BOARD_TITLE = "boardTitle"; //게시글 제목
        String TAG_BOARD_CONTENT = "boardContent"; //게시글 내용
        String TAG_USER_NAME = "userName"; //작성자 이름
        String TAG_BOARD_DATE = "boardDate"; //게시글 작성일
        String TAG_BOARD_VIEW = "boardView"; //게시글 조회수
        String TAG_USER_PROFILE = "userProfile"; //게시글 조회수
        String TAG_BOARD_USER = "boardUser"; //작성자 이메일

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String boardCategory = item.getString(TAG_BOARD_CATEGORY);
                String boardTitle = item.getString(TAG_BOARD_TITLE);
                String boardContent = item.getString(TAG_BOARD_CONTENT);
                String userName = item.getString(TAG_USER_NAME);
                String boardDate = item.getString(TAG_BOARD_DATE);
                String boardView = item.getString(TAG_BOARD_VIEW);
                String userProfile = item.getString(TAG_USER_PROFILE);
                String boardUser = item.getString(TAG_BOARD_USER);

                //게시글 카테고리, 제목, 내용, 작성자, 작성일, 조회수, 작성자 프로필 순으로 저장
                boardInfoList.add(boardCategory);
                boardInfoList.add(boardTitle);
                boardInfoList.add(boardContent);
                boardInfoList.add(userName);
                boardInfoList.add(boardDate);
                boardInfoList.add(boardView);
                boardInfoList.add(userProfile);
                boardInfoList.add(boardUser);
            }

        } catch (JSONException e) {
            Log.e(TAG, "showResult : ", e);
        }
    }


}
