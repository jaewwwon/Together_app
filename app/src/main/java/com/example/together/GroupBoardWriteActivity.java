package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.getToday;
import static com.example.together.StaticInit.loginUserId;

public class GroupBoardWriteActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupBoardWriteActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    Spinner boardCategory; //게시글 말머리 선택
    ArrayAdapter categoryAdapter; //스피너 어댑터
    EditText boardTitle; //게시글 제목 입력란
    EditText boardContent; //게시글 내용 입력란
    Button cancelButton; //취소 버튼
    Button submitButton; //작성버튼

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

        SharedPreferences preferences = getSharedPreferences("sFile", 0);
        loginUserId = preferences.getString("USER_LOGIN_ID", null);
        if (loginUserId != null) {
            Log.e(TAG, "로그인 id: " + loginUserId);

        }

        //취소버튼을 누렀을 경우 해당 액티비티를 종료시킨다.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "스피너 선택값: " + boardCategory.getSelectedItem().toString());

                //각 폼의 입력값이 있는지 확인한다.
                if (boardTitle.getText().toString().length() == 0) {
                    Toast.makeText(GroupBoardWriteActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (boardContent.getText().toString().length() == 0) {
                    Toast.makeText(GroupBoardWriteActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //일정 추가하기 팝업 양식의 입력값을 변수에 저장한다.
                String boardCategoryStr = boardCategory.getSelectedItem().toString(); //게시글 카테고리
                String boardTitleStr = boardTitle.getText().toString(); //게시글 제목
                String boardContentStr = boardContent.getText().toString();//게시글 내용

                //DB 저장
                int getBoardIdx = 0; //작성된 게시글 index 값
                InsertData task = new InsertData();
                //게시글 카테고리, 제목, 내용, 작성자, 모임 index
                try {
                    getBoardIdx = Integer.parseInt(task.execute("http://" + IP_ADDRESS + "/group/board_write_check.php", boardCategoryStr, boardTitleStr, boardContentStr, loginUserId, String.valueOf(PAGE_GROUP_INDEX)).get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //리사이클러뷰 저장
                GroupBoardData data = new GroupBoardData();
                data.setBoardCategory("");
                data.setBoardTitle("[" + boardCategoryStr + "] " + boardTitleStr);
                data.setBoardContent(boardContentStr);
                data.setBoardUser(StaticInit.loginUserName);
                data.setBoardDate(getToday("yyyy-MM-dd HH:mm:ss"));

                String boardDate = getToday("yyyy-MM-dd HH:mm:ss");
                //만약 작성일이 오늘날짜라면 시간만 보이도록 출력하고,
                //작성일이 오늘날짜가 아니라면 년,월,일 형식으로 출력한다.
                if (StaticInit.getToday("yyyy-MM-dd").equals(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"))) {
                    data.setBoardDate(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "HH:mm")); //게시글 작성시간
                } else {
                    data.setBoardDate(getDateFormat(boardDate, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd")); //게시글 작성날짜
                }

                //새로 작성한 게시글이므로 조회수는 0으로 입력한다.
                data.setBoardView(0);
                data.setBoardIdx(getBoardIdx);

                GroupBoardActivity.groupBoardAdapter.addItem(0, data);
                GroupBoardActivity.groupBoardAdapter.notifyDataSetChanged();


                //게시글 추가를 완료했을 경우 해당 화면을 닫는다.
                Intent intent = new Intent(GroupBoardWriteActivity.this, GroupBoardViewActivity.class);
                intent.putExtra("boardIdxOri", getBoardIdx);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        //스피너 기본 세팅
        setViews();

    }

    public void setViews() {
        // 스피너 어댑터를 초기화한다
        categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category, R.layout.textview);
        //해당 스피너에 어댑터를 연결한다
        boardCategory.setAdapter(categoryAdapter);
        //초기 기본 선택값을 지정한다.
        boardCategory.setSelection(0);
    }

    //DB에 저장할 변수들을 보낸다.
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupBoardWriteActivity.this,
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
            String userEmail = params[4]; //게시글 작성자
            String group = params[5]; //모임 index

            //게시글 카테고리, 제목, 내용, 작성자, 모임 index
            String postParameters = "boardCategory=" + boardCategory + "&boardTitle=" + boardTitle + "&boardContent=" + boardContent + "&userEmail=" + userEmail + "&group=" + group;

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
