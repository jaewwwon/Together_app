package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.together.StaticInit.PAGE_GROUP_HOST;
import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.PAGE_GROUP_NAME;
import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.getToday;
import static com.example.together.StaticInit.loginUserId;

public class GroupBoardModifyActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupBoardModifyActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; //json 데이터 파일
    List<String> boardInfoList; //게시글 정보 파싱 데이터 리스트
    Spinner boardCategory; //게시글 말머리 선택
    ArrayAdapter categoryAdapter; //스피너 어댑터
    EditText boardTitle; //게시글 제목 입력란
    EditText boardContent; //게시글 내용 입력란
    Button cancelButton; //취소 버튼
    Button submitButton; //작성(수정) 버튼
    TextView pageTitle; //페이지 제목
    int itemPositionKey; //intent로 전달받은 아이템 위치값
    int itemSizeKey; //intent로 전달받은 아이템 크기값
    int boardIdxKey; //intent로 전달받은 게시글 index 값

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_board_write);

        //초기화
        boardCategory = findViewById(R.id.boardCategory);
        boardTitle = findViewById(R.id.boardTitle);
        boardContent = findViewById(R.id.boardContent);
        cancelButton = findViewById(R.id.cancelButton);
        submitButton = findViewById(R.id.submitButton);
        pageTitle = findViewById(R.id.pageTitle);

        // 인텐트값 넘겨받기
        Intent intent = getIntent();
        itemPositionKey = intent.getIntExtra("itemPositionOri", 0); //해당 게시글 아이템 위치값
        itemSizeKey = intent.getIntExtra("itemSizeOri", 0); //해당 게시글 아이템 사이즈
        boardIdxKey = intent.getIntExtra("boardIdxOri", 0); //게시글 index
//        Log.e(TAG, "게시글 index 정보: " + boardIdxKey);

        //페이지 제목 설정
        pageTitle.setText("게시글 수정");

        //취소버튼을 누렀을 경우 해당 액티비티를 종료시킨다.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //기존 값 불러오기
        getBoardData boardTask = new getBoardData();
        //게시글 index 순서
        boardTask.execute("http://" + IP_ADDRESS + "/db/group_board.php", String.valueOf(boardIdxKey));

        Log.e(TAG, "게시글 index: " + boardIdxKey);


        //해당 버튼의 텍스트를 작성에서 수정으로 변경해준다.
        submitButton.setText("수정");
        //수정 버튼을 눌렀을 경우
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.e(TAG, "스피너 선택값: " + boardCategory.getSelectedItem().toString());

                //각 폼의 입력값이 있는지 확인한다.
                if (boardTitle.getText().toString().length() == 0) {
                    Toast.makeText(GroupBoardModifyActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (boardContent.getText().toString().length() == 0) {
                    Toast.makeText(GroupBoardModifyActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //일정 추가하기 팝업 양식의 입력값을 변수에 저장한다.
                String boardCategoryStr = boardCategory.getSelectedItem().toString(); //게시글 카테고리
                String boardTitleStr = boardTitle.getText().toString(); //게시글 제목
                String boardContentStr = boardContent.getText().toString();//게시글 내용

                //DB 저장
                InsertData task = new InsertData();
                //게시글 카테고리, 제목, 내용, 게시글 index 순서
                task.execute("http://" + IP_ADDRESS + "/group/board_modify_check.php", boardCategoryStr, boardTitleStr, boardContentStr, String.valueOf(boardIdxKey));

                //리사이클러뷰 저장
//                GroupBoardData data = new GroupBoardData();
//                data.setBoardCategory("");
//                data.setBoardTitle("[" + boardCategoryStr + "] " + boardTitleStr);
//                data.setBoardContent(boardContentStr);
//                data.setBoardUser(StaticInit.loginUserName);
//                data.setBoardDate(getToday("yyyy-MM-dd HH:mm:ss"));
//
//                String boardDate = getToday("yyyy-MM-dd HH:mm:ss");
//                //만약 작성일이 오늘날짜라면 시간만 보이도록 출력하고,
//                //작성일이 오늘날짜가 아니라면 년,월,일 형식으로 출력한다.
//                if (StaticInit.getToday("yyyy-MM-dd").equals(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"))) {
//                    data.setBoardDate(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "HH:mm")); //게시글 작성시간
//                } else {
//                    data.setBoardDate(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd")); //게시글 작성날짜
//                }
//                data.setBoardView(0);
//
//                GroupBoardActivity.groupBoardAdapter.addItem(0, data);
//                GroupBoardActivity.groupBoardAdapter.notifyDataSetChanged();


                //게시글 추가를 완료했을 경우 해당 화면을 닫는다.
                finish();
            }
        });

        //스피너 기본 세팅
        setViews();

    }

    public void setViews() {
        if(PAGE_GROUP_HOST.equals(loginUserId)){
            // 스피너 어댑터를 초기화한다
            categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category_host, R.layout.textview);
        } else {
            // 스피너 어댑터를 초기화한다
            categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category, R.layout.textview);
        }
        //해당 스피너에 어댑터를 연결한다
        boardCategory.setAdapter(categoryAdapter);
//        //초기 기본 선택값을 지정한다.
//        boardCategory.setSelection(0);
    }

    //기존 게시글 데이터를 불러온다
    class getBoardData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardModifyActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {
                jsonString = result;
                boardResult();

                //게시글 기본값 저장
                boardTitle.setText(boardInfoList.get(1));
                boardContent.setText(boardInfoList.get(2));

                int categoryPosition = categoryAdapter.getPosition(boardInfoList.get(0));
                boardCategory.setSelection(categoryPosition);

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0]; //URL 주소값
            String boardIdx = params[1]; //게시글 카테고리

            Log.e(TAG, "보내는 게시글 index : " + boardIdx);

            //게시글 카테고리, 제목, 내용, 작성자, 모임 index
            String postParameters = "boardIdx=" + boardIdx + "&whereTxt=" + "";

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.e(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    //모임 정보 JSON 파싱 후, 데이터 저장하기
    private void boardResult() {

        boardInfoList = new ArrayList<String>();

        String TAG_JSON = "groupBoard";
        String TAG_BOARD_CATEGORY = "boardCategory"; //게시글 카테고리
        String TAG_BOARD_TITLE = "boardTitle"; //게시글 제목
        String TAG_BOARD_CONTENT = "boardContent"; //게시글 내용

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String boardCategory = item.getString(TAG_BOARD_CATEGORY);
                String boardTitle = item.getString(TAG_BOARD_TITLE);
                String boardContent = item.getString(TAG_BOARD_CONTENT);

                boardInfoList.add(boardCategory);
                boardInfoList.add(boardTitle);
                boardInfoList.add(boardContent);

            }


        } catch (JSONException e) {
            Log.e(TAG, "boardInfoResult : ", e);
        }

    }

    //DB에 저장할 변수들을 보낸다.
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardModifyActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
//            Log.e(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0]; //URL 주소값
            String boardCategory = params[1]; //게시글 카테고리
            String boardTitle = params[2]; //게시글 제목
            String boardContent = params[3]; //게시글 내용
            String boardIdx = params[4]; //모임 index

            //게시글 카테고리, 제목, 내용, 게시글 index 순서
            String postParameters = "boardCategory=" + boardCategory + "&boardTitle=" + boardTitle + "&boardContent=" + boardContent + "&boardIdx=" + boardIdx;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.e(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
