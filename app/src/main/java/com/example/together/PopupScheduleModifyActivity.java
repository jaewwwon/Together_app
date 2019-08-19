package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.together.StaticInit.IP_ADDRESS;
import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.doDiffOfDate;
import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.getDateWeek;

public class PopupScheduleModifyActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "PopupScheduleModifyActivity";
    TextView popupTitle;
    EditText scheduleTitle; //일정 제목 입력란
    EditText scheduleContent; //일정 내용 입력란
    TextView scheduleSelectDate; //일정 날짜 선택버튼
    DatePicker datePicker; //날짜 선택란
    TextView scheduleSelectTime; //일정 시간 선택버튼
    TimePicker timePicker; //시간 선택란
    TextView scheduleLocation; //일정 장소 입력란
    Button submitButton; //일정 추가 버튼
    Button cancleButton; //일정 추가취소 버튼
    int mYear;
    int mMonth;
    int mDay;
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
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 상단 타이틀 제거
        setContentView(R.layout.activity_popup_schedule_add);

        //초기화
        popupTitle = findViewById(R.id.popupTitle);
        scheduleTitle = findViewById(R.id.scheduleTitle);
        scheduleContent = findViewById(R.id.scheduleContent);
        scheduleSelectDate = findViewById(R.id.scheduleSelectDate);
        datePicker = findViewById(R.id.datePicker);
        scheduleSelectTime = findViewById(R.id.scheduleSelectTime);
        timePicker = findViewById(R.id.timePicker);
        scheduleLocation = findViewById(R.id.scheduleLocation);
        submitButton = findViewById(R.id.submitButton);
        cancleButton = findViewById(R.id.cancleButton);


        //인텐트값 넘겨받기
        Intent intent = getIntent();
        itemPositionKey = intent.getIntExtra("itemPositionOri", 0); // 아이템 위치값
        scheduleIdxKey = intent.getIntExtra("scheduleIdxOri", 0); // 일정 index
        scheduleTitleKey = intent.getStringExtra("scheduleTitleOri"); // 일정 제목
        scheduleContentKey = intent.getStringExtra("scheduleContentOri"); // 일정 내용
        scheduleDateKey = intent.getStringExtra("scheduleDateOri"); // 일정 날짜
        scheduleLocationKey = intent.getStringExtra("scheduleLocationOri"); // 일정 장소


        //일정 수정하기 팝업에 맞도록 문구 수정 및 기존값 가져오기
        popupTitle.setText("일정 수정하기"); //팝업 제목
        submitButton.setText("수정"); //팝업 완료 버튼
        scheduleTitle.setText(scheduleTitleKey); //일정 제목
        scheduleContent.setText(scheduleContentKey); //일정 내용
        scheduleLocation.setText(scheduleLocationKey); //일정 장소

        // TODO 소스 정리하기
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = inputFormat.parse(scheduleDateKey);
            date2 = inputFormat.parse(scheduleDateKey);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateText = dateFormat.format(date1);
        String timeText = timeFormat.format(date2);


        //현재 날짜를 Defalut값으로 입력
        scheduleSelectDate.setText(dateText);

        //날짜 선택 버튼을 클릭했을 경우
        //날짜 선택란의 활성화 상태에 따라 활성화/비활성화
        scheduleSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (datePicker.getVisibility() == View.GONE) {
                    datePicker.setVisibility(View.VISIBLE);
                } else if (datePicker.getVisibility() == View.VISIBLE) {
                    datePicker.setVisibility(View.GONE);
                }
            }
        });

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker picker, int yyyy, int mm, int dd) {
//                Log.e(TAG, String.valueOf(mYear));
                scheduleSelectDate.setText(yyyy + "." + (mm + 1) + "." + dd);
            }
        });

        //현재 시간을 Defalut값으로 입력
        scheduleSelectTime.setText(timeText);

        //시간 선택 버튼을 클릭했을 경우
        //시간 선택란의 활성화 상태에 따라 활성화/비활성화
        scheduleSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timePicker.getVisibility() == View.GONE) {
                    timePicker.setVisibility(View.VISIBLE);
                } else if (timePicker.getVisibility() == View.VISIBLE) {
                    timePicker.setVisibility(View.GONE);
                }
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                scheduleSelectTime.setText(hourOfDay + ":" + minute);
            }
        });


        // 수정 버튼을 눌렀을 경우
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "모임 index: " + PAGE_GROUP_INDEX);

//                Log.e(TAG, String.valueOf(timePicker.getCurrentHour()));
//                Log.e(TAG, String.valueOf(timePicker.getCurrentMinute()));


                //각 폼의 입력값이 있는지 확인한다.
                if (scheduleTitle.getText().toString().length() == 0) {
                    Toast.makeText(PopupScheduleModifyActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (scheduleContent.getText().toString().length() == 0) {
                    Toast.makeText(PopupScheduleModifyActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (scheduleLocation.getText().toString().length() == 0) {
                    Toast.makeText(PopupScheduleModifyActivity.this, "장소를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //일정 추가하기 팝업 양식의 입력값을 변수에 저장한다.
                String scTitle = scheduleTitle.getText().toString(); //일정 제목
                String scContent = scheduleContent.getText().toString();//일정 내용
                String scDate = scheduleSelectDate.getText().toString();//일정 날짜
                String scClock;//일정 시간(오전/오후)
                String scTime = String.valueOf(timePicker.getCurrentHour());//시간(시)
                String scMinute = String.valueOf(timePicker.getCurrentMinute());//시간(분)
                String scLocation = scheduleLocation.getText().toString(); //일정 장소
                int group = PAGE_GROUP_INDEX;//모임 index

                if (timePicker.getCurrentHour() >= 12) {
                    scClock = "오후";
                    scTime = String.valueOf(timePicker.getCurrentHour() - 12);
                    if (scTime.equals("0")) {
                        scTime = "12";
                    }
                } else {
                    scClock = "오전";
                    if (scTime.equals("0")) {
                        scTime = "12";
                    }
                }


                //TODO 일정 값 제대로 넣고, DB에 값 저장하기!!!!
                //리사이클러뷰 저장
                SearchScheduleData data = new SearchScheduleData();
                String resetDateFormat = getDateFormat(scDate, "yyyy.MM.dd", "yyyy-MM-dd");
                data.setScheduleWeek(getDateWeek(resetDateFormat, "yyyy-MM-dd") + "요일");
                if (diffOfDate(resetDateFormat) < 0) {
                    data.setScheduleCount("종료");
                } else {
                    data.setScheduleCount(doDiffOfDate(resetDateFormat));
                }
                data.setScheduleDate(scDate + " " + scheduleSelectTime.getText().toString() + ":00");
                data.setScheduleTitle(scTitle);
                data.setScheduleContent(scContent);
                data.setScheduleLocation(scLocation);

//                GroupScheduleActivity.groupScheduleAdapter.addItem(0, data);
//                GroupScheduleActivity.groupScheduleAdapter.notifyDataSetChanged();

                GroupScheduleActivity.groupScheduleAdapter.listData.set(itemPositionKey, data);
                GroupScheduleActivity.groupScheduleAdapter.notifyItemChanged(itemPositionKey);

                //DB 저장
                InsertData task = new InsertData();
                //제목, 내용, 날짜, 시간(오전/오후), 시간(시), 시간(분), 장소, 일정 idex 순서
                task.execute("http://" + IP_ADDRESS + "/group/schedule_modify.php", scTitle, scContent, scDate, scClock, scTime, scMinute, scLocation, String.valueOf(scheduleIdxKey));

                //일정 추가를 완료했을 경우 해당 화면을 닫는다.
                finish();
            }
        });

        // 취소 버튼을 눌렀을 경우
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupScheduleModifyActivity.this,
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
            String scTitle = (String) params[1]; //일정 제목
            String scContent = (String) params[2];//일정 내용
            String scDate = (String) params[3];//일정 날짜
            String scClock = (String) params[4];//일정 시간(오전/오후)
            String scTime = (String) params[5];//시간(시)
            String scMinute = (String) params[6];//시간(분)
            String scLocation = (String) params[7]; //일정 장소
            int group = Integer.parseInt(params[8]);//모임 index

            //제목, 내용, 날짜, 시간(오전/오후), 시간(시), 시간(분), 장소, 일정 index 순서
            String postParameters = "scTitle=" + scTitle + "&scContent=" + scContent + "&scDate=" + scDate + "&scClock=" + scClock + "&scTime=" + scTime + "&scMinute=" + scMinute + "&scLocation=" + scLocation + "&scheduleIdx=" + scheduleIdxKey;

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

    // 두날짜의 차이 구하기
    public static long diffOfDate(String end) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

            Date date = new Date();
            String today = formatter.format(date);

            Date beginDate = null;
            try {
                beginDate = formatter.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();

            // 날짜 차이 결과
            long diffDays = diff / (24 * 60 * 60 * 1000);

            return diff;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
