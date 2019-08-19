package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.PAGE_GROUP_NAME;
import static com.example.together.StaticInit.doDiffOfDate;
import static com.example.together.StaticInit.getDateFormat;
import static com.example.together.StaticInit.getDateWeek;
import static com.example.together.StaticInit.getToday;

public class GroupScheduleActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupScheduleActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    static GroupScheduleAdapter groupScheduleAdapter; //일정 어댑터
    private ArrayList<SearchScheduleData> listData;
    List<String> attendMemberList; // 일정 참석 멤버 정보 파싱 데이터 리스트
    private String jsonString; // json 데이터 파일
    TextView pageGroupTit; //페이지 상단 모임 이름
    TextView infoTab; //정보탭 버튼
    TextView scheduleTab; //일정탭 버튼
    TextView boardTab; //게시판탭 버튼a
    TextView photoTab; //사진첩탭 버튼
    TextView chatTab; //채팅탭 버튼
    ImageView scheduleAddBtn; //일정 추가 버튼

    private int PAGE_LOAD_NUM = 10; //페이지 로드 수
    private int JSON_TOTAL_NUM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_schedule);

        Log.e(TAG, "모임 index: " + PAGE_GROUP_INDEX);

        //초기화
        pageGroupTit = findViewById(R.id.pageGroupTit);
        infoTab = findViewById(R.id.infoTab);
        scheduleTab = findViewById(R.id.scheduleTab);
        boardTab = findViewById(R.id.boardTab);
        photoTab = findViewById(R.id.photoTab);
        chatTab = findViewById(R.id.chatTab);
        scheduleAddBtn = findViewById(R.id.scheduleAddBtn);

        //TODO 일정 추가 버튼 조건에 따라 보이기
//        if(모임장일 경우에만 버튼 표시){
//            scheduleAddBtn.setVisibility(View.GONE);
//        }

        //정보탭 버튼을 클릭했을 경우
        infoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupScheduleActivity.this, GroupInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //일정탭 버튼을 클릭했을 경우
        scheduleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupScheduleActivity.this, GroupScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //게시판탭 버튼을 클릭했을 경우
        boardTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupScheduleActivity.this, GroupBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //사진첩탭 버튼을 클릭했을 경우
        photoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupScheduleActivity.this, GroupPhotoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //채팅탭 버튼을 클릭했을 경우
        chatTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupScheduleActivity.this, GroupChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //일정 추가 버튼을 클릭했을 경우
        scheduleAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupScheduleActivity.this, PopupScheduleAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //모임 정보 JSON 파일 가져오기
        GetGroupIntoData groupInfoTask = new GetGroupIntoData();
        groupInfoTask.execute("http://" + IP_ADDRESS + "/db/group_info.php", "");


        //일정 참석 멤버 정보 JSON 파일 가져오기
        GetScheduleAttendData attendMemberTask = new GetScheduleAttendData();
        attendMemberTask.execute("http://" + IP_ADDRESS + "/db/schedule_attend.php", "");

        //초기화
        listData = new ArrayList<>();
        ScheduleInit(); //일정 목록 초기화
        //일정 정보 JSON 파일 가져오기
        GetScheduleData scheduleTask = new GetScheduleData();
        scheduleTask.execute("http://" + IP_ADDRESS + "/db/group_schedule.php", String.valueOf(PAGE_GROUP_INDEX));
    }

    //일정 목록 초기화
    private void ScheduleInit() {
        RecyclerView recyclerView = findViewById(R.id.groupScheduleList);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);

        groupScheduleAdapter = new GroupScheduleAdapter();
        recyclerView.setAdapter(groupScheduleAdapter);

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

                if(itemTotalCount != JSON_TOTAL_NUM){
                    //리스트 마지막(바닥) 도착!!!!! 다음 페이지 데이터 로드!!
                    if (lastVisibleItemPosition == itemTotalCount - 1) {


                        Log.e(TAG, "itemTotalCount : " + itemTotalCount);
                        Log.e(TAG, "JSON_TOTAL_NUM : " + JSON_TOTAL_NUM); //불러오는 JSON의 총 개수

                        int resultTest = JSON_TOTAL_NUM - itemTotalCount;

                        if(resultTest >= 0){
                            //일정 정보 JSON 파일 가져오기
                            GetScheduleData scheduleTask = new GetScheduleData();
                            scheduleTask.execute("http://" + IP_ADDRESS + "/db/group_schedule.php", String.valueOf(PAGE_GROUP_INDEX));
                        }

                    }
                }


            }
        });

    }


    //일정 정보 JSON 가져오기
    private class GetScheduleData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupScheduleActivity.this,
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
            PAGE_LOAD_NUM += 1;
            String serverURL = params[0];
            String groupIdx = params[1];
//            String postParameters = "groupIdx=" + PAGE_GROUP_INDEX + "&whereTxt=" + "WHERE DATE(sc_date) >= '" + getToday() + "' ORDER BY sc_date DESC";
            String postParameters = "groupIdx=" + PAGE_GROUP_INDEX + "&whereTxt=" + "ORDER BY sc_date DESC LIMIT 0, " + PAGE_LOAD_NUM;

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

    //일정 정보 파싱 및 데이터 저장
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

            JSON_TOTAL_NUM = jsonArray.length();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String scIdx = item.getString(TAG_SC_IDX);
                String scTitle = item.getString(TAG_SC_TITLE);
                String scContent = item.getString(TAG_SC_CONTENT);
                String scDate = item.getString(TAG_SC_DATE);
                String scLocation = item.getString(TAG_SC_LOCATION);
                String groupIdx = item.getString(TAG_GROUP_IDX);

                // 해당 모임에 등록된 일정만 출력한다.
                if (groupIdx.equals(String.valueOf(PAGE_GROUP_INDEX))) {
                    SearchScheduleData data = new SearchScheduleData();
                    data.setScheduleWeek(getDateWeek(scDate, "yyyy-MM-dd") + "요일");

                    data.setScheduleDate(scDate);
                    // 날짜, 요일, 시간순
//                    data.setScheduleDate(getDateFormat(scDate, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd") + "(" + getDateWeek(scDate, "yyyy-MM-dd HH:mm:ss") + ") " + getDateFormat(scDate, "yyyy-MM-dd HH:mm:ss", "HH:mm"));
                    data.setScheduleTitle(scTitle);
                    data.setScheduleContent(scContent);
                    data.setScheduleLocation(scLocation);

                    if (diffOfDate(scDate) < 0) {
                        data.setScheduleCount("종료");
                    } else {
                        data.setScheduleCount(doDiffOfDate(scDate));
                    }

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
                    data.setScheduleIdx(Integer.parseInt(scIdx));

                    // listData.add(data);
                    groupScheduleAdapter.addItem(data);
                }
            }

            groupScheduleAdapter.notifyDataSetChanged();


        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //모임 정보 JSON 파일 가져오기
    private class GetGroupIntoData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupScheduleActivity.this,
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
        String TAG_GROUP_NAME = "groupName"; //모임 이름

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String groupName = item.getString(TAG_GROUP_NAME);

                pageGroupTit.setText(groupName);
                PAGE_GROUP_NAME = groupName;

            }

        } catch (JSONException e) {

            Log.e(TAG, "groupInfoResult : ", e);
        }

    }

    //일정 참석 멤버 정보 JSON 가져오기
    private class GetScheduleAttendData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupScheduleActivity.this,
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

    // 두날짜의 차이 구하기
    public static long diffOfDate(String end) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

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
