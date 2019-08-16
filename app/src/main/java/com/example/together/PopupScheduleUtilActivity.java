package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.together.StaticInit.IP_ADDRESS;

public class PopupScheduleUtilActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "PopupScheduleUtilActivity";
    TextView popupTitle; //일정 제목란
    Button scheduleModifyButton; //일정 수정하기 버튼
    Button scheduleDeleteButton; //일정 삭제하기 버튼
    ImageButton closePopupButton; //팝업 닫기 버튼
    int itemPositionKey; // 아이템 위치값
    int itemSizeKey; // 아이템 크기값
    int scheduleIdxKey; // 일정 index
    String scheduleTitleKey; // 일정 제목
    String scheduleContentKey; // 일정 내용
    String scheduleDateKey; // 일정 날짜
    String scheduleLocationKey; // 일정 장소

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 상단 타이틀 제거
        setContentView(R.layout.activity_popup_schedule_util);

        //초기화
        popupTitle = findViewById(R.id.popupTitle);
        scheduleModifyButton = findViewById(R.id.scheduleModifyButton);
        scheduleDeleteButton = findViewById(R.id.scheduleDeleteButton);
        closePopupButton = findViewById(R.id.closePopupButton);

        //인텐트값 넘겨받기
        Intent intent = getIntent();
        itemPositionKey = intent.getIntExtra("itemPositionOri", 0); // 아이템 위치값
        scheduleIdxKey = intent.getIntExtra("scheduleIdxOri", 0); // 일정 index
        scheduleTitleKey = intent.getStringExtra("scheduleTitleOri"); // 일정 제목
        scheduleContentKey = intent.getStringExtra("scheduleContentOri"); // 일정 내용
        scheduleDateKey = intent.getStringExtra("scheduleDateOri"); // 일정 날짜
        scheduleLocationKey = intent.getStringExtra("scheduleLocationOri"); // 일정 장소


        //팝업 제목 설정
        popupTitle.setText(scheduleTitleKey);

        //일정 수정하기 버튼을 눌렀을 경우
        scheduleModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PopupScheduleUtilActivity.this, PopupScheduleModifyActivity.class);

                intent.putExtra("itemPositionOri", itemPositionKey); //아이템 위치값
                intent.putExtra("scheduleIdxOri", scheduleIdxKey); //일정 index
                intent.putExtra("scheduleTitleOri", scheduleTitleKey); //일정 제목
                intent.putExtra("scheduleContentOri", scheduleContentKey); //일정 내용
                intent.putExtra("scheduleDateOri", scheduleDateKey); //일정 날짜
                intent.putExtra("scheduleLocationOri", scheduleLocationKey); //일정 장소

                startActivity(intent);
                finish();
            }
        });

        //일정 삭제하기 버튼을 눌렀을 경우
        scheduleDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DB 저장
                DeleteData task = new DeleteData();
                //일정 idex 보내기
                task.execute("http://" + IP_ADDRESS + "/group/schedule_delete.php", String.valueOf(scheduleIdxKey));

                //해당 아이템을 삭제하고, 리사이클러뷰 아이템을 재 정렬 해준다.
                GroupScheduleActivity.groupScheduleAdapter.removeItem(itemPositionKey);
                GroupScheduleActivity.groupScheduleAdapter.notifyItemRemoved(itemPositionKey);
                GroupScheduleActivity.groupScheduleAdapter.notifyItemRangeChanged(itemPositionKey, itemSizeKey - 1);

                finish();
            }
        });


        //취소 버튼을 눌렀을 경우
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupScheduleUtilActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.e(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0]; //URL 주소값
            int schedule = Integer.parseInt(params[1]);//일정 index

            //일정 index 보내기
            String postParameters = "schedule=" + schedule;

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
