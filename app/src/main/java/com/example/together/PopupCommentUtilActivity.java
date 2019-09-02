package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.together.StaticInit.IP_ADDRESS;

public class PopupCommentUtilActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "PopupCommentUtilActivity";
    ImageButton closePopupButton; //팝업 닫기 버튼
    Button commModifyButton; //댓글 수정 버튼
    Button commDeleteButton; //댓글 삭제 버튼

    //intent로 전달받은 값
    int itemPositionKey; //아이템 위치값
    int itemSizeKey; //아이템 크기
    int commIdxKey; //댓글 index
    String commContentKey; //댓글 내용
    String commNameKey; //작성자 이름
    String commEmailKey; //작성자 이메일
    String commProfileKey; //작성자 프로필
    String commDateKey; //작성일


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //상단 타이틀 제거
        setContentView(R.layout.activity_popup_comment_util);

        //초기화
        closePopupButton = findViewById(R.id.closePopupButton);
        commModifyButton = findViewById(R.id.commModifyButton);
        commDeleteButton = findViewById(R.id.commDeleteButton);


        // 인텐트값 넘겨받기
        Intent intent = getIntent();
        itemPositionKey = intent.getIntExtra("itemPositionOri", 0); //아이템 위치값
        itemSizeKey = intent.getIntExtra("itemSizeOri", 0); //아이템 크기
        commIdxKey = intent.getIntExtra("commIdxOri", 0); //댓글 index
        commContentKey = intent.getStringExtra("commContentOri"); //댓글 내용
        commNameKey = intent.getStringExtra("commNameOri"); //작성자 이름
        commEmailKey = intent.getStringExtra("commEmailOri"); //작성자 이메일
        commProfileKey = intent.getStringExtra("commProfileOri"); //작성자 프로필
        commDateKey = intent.getStringExtra("commDateOri"); //작성일


        Log.e(TAG, "댓글 index : " + commIdxKey);


        //팝업 닫기 버튼을 눌렀을 경우
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //댓글 수정하기 버튼을 눌렀을 경우
        commModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "댓글수정버튼 클릭함");

                Intent resultIntent = new Intent();
                resultIntent.putExtra("itemPositionOri", itemPositionKey); //아이템 위치값
                resultIntent.putExtra("commIdxOri", commIdxKey); //댓글 index
                resultIntent.putExtra("commContentOri", commContentKey); //댓글 내용
                resultIntent.putExtra("commNameOri", commNameKey); //작성자 이름
                resultIntent.putExtra("commEmailOri", commEmailKey); //작성자 이메일
                resultIntent.putExtra("commProfileOri", commProfileKey); //작성자 프로필
                resultIntent.putExtra("commDateOri", commDateKey); //작성일
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });


        //댓글 삭제하기 버튼을 눌렀을 경우
        commDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB 저장
                DeleteData task = new DeleteData();
                //댓글 index 보내기
                task.execute("http://" + IP_ADDRESS + "/group/comment_delete.php", String.valueOf(commIdxKey));

                //해당 아이템을 삭제하고, 리사이클러뷰 아이템을 재 정렬 해준다.
                GroupBoardViewActivity.groupBoardViewAdapter.removeItem(itemPositionKey);
                GroupBoardViewActivity.groupBoardViewAdapter.notifyItemRemoved(itemPositionKey);
                GroupBoardViewActivity.groupBoardViewAdapter.notifyItemRangeChanged(itemPositionKey, itemSizeKey - 1);

//                Intent resultIntent = new Intent();
//                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }

    //해당 댓글을 삭제하는 함수
    class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupCommentUtilActivity.this,
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
            String commIdx = params[1]; //댓글 index

            //댓글 index 보내기
            String postParameters = "commIdx=" + commIdx;

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

                Log.e(TAG, "DeleteData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
