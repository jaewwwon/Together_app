package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SearchScheduleActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "SearchScheduleActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private SearchScheduleAdapter searchScheduleAdapter; //일정 어댑터
    private ArrayList<SearchScheduleData> listData;
    List<String> attendMemberList; // 일정 참석 멤버 정보 파싱 데이터 리스트
    private String jsonString; // json 데이터 파일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_schedule);

        //일정 참석 멤버 정보 JSON 파일 가져오기
        GetScheduleAttendData attendMemberTask = new GetScheduleAttendData();
        attendMemberTask.execute("http://" + IP_ADDRESS + "/db/schedule_attend.php", "");

        //초기화
        listData = new ArrayList<>();
        ScheduleInit(); //일정 목록 초기화
        //일정 정보 JSON 파일 가져오기
        GetScheduleData scheduleTask = new GetScheduleData();
        scheduleTask.execute("http://" + IP_ADDRESS + "/db/group_schedule.php", "");

    }


    //일정 목록 초기화
    private void ScheduleInit() {
        RecyclerView recyclerView = findViewById(R.id.scheduleAllList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);

        searchScheduleAdapter = new SearchScheduleAdapter();
        recyclerView.setAdapter(searchScheduleAdapter);
    }


    private class GetScheduleData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SearchScheduleActivity.this,
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
                ScheduleResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
//            String postParameters = params[1];
            String postParameters = "whereTxt=" + "WHERE DATE(sc_date) >= '" + getToday() + "' ORDER BY sc_date DESC";

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

    private void ScheduleResult() {

        String TAG_JSON = "groupSchedule";
        String TAG_SC_IDX = "scIdx"; //일정 index
        String TAG_SC_TITLE = "scTitle"; //일정 제목
        String TAG_SC_CONTENT = "scContent"; //일정 내용
        String TAG_SC_DATE = "scDate"; //일정 날짜
        String TAG_SC_LOCATION = "scLocation"; //일정 장소
        String TAG_GROUP_IDX = "groupIdx"; //모임 index


        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String scIdx = item.getString(TAG_SC_IDX);
                String scTitle = item.getString(TAG_SC_TITLE);
                String scContent = item.getString(TAG_SC_CONTENT);
                String scDate = item.getString(TAG_SC_DATE);
                String scLocation = item.getString(TAG_SC_LOCATION);
                String groupIdx = item.getString(TAG_GROUP_IDX);

                SearchScheduleData data = new SearchScheduleData();
                data.setScheduleWeek(getDateWeek(scDate, "yyyy-MM-dd"));
                data.setScheduleCount(doDiffOfDate(scDate));
                data.setScheduleDate(scDate);
                data.setScheduleTitle(scTitle);
                data.setScheduleContent(scContent);
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
                searchScheduleAdapter.addItem(0, data);
            }

            searchScheduleAdapter.notifyDataSetChanged();


        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //일정 참석 멤버 정보 JSON 가져오기
    private class GetScheduleAttendData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SearchScheduleActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.e(TAG, "response - " + result);

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


    // 오늘 날짜 구하는 함수
    public String getToday() {

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

        Date time = new Date();

        String today = format1.format(time);

        return today;
    }


    // 해당 날짜의 요일을 구하는 함수
    public String getDateWeek(String inputDate, String dateType) throws Exception {

        DateFormat df = new SimpleDateFormat(dateType, Locale.KOREA);
        DateFormat ddf = new SimpleDateFormat("E", Locale.KOREA);

        String week = ddf.format(df.parse(inputDate));

        return week + "요일";
    }

    // 두날짜의 차이 구하기
    public String doDiffOfDate(String end) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

            Date date = new Date();
            String today = formatter.format(date);

            Date beginDate = formatter.parse(today);
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();

            // 날짜 차이 결과
            long diffDays = (int) diff / (24 * 60 * 60 * 1000);

            diffDays = Math.abs(diffDays);

            if (diffDays == 0) {
                return "오늘";
            } else {
                return "D-" + diffDays;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return String.valueOf(0);
        }

    }


}
