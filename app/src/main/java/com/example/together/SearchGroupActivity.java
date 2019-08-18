package com.example.together;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SearchGroupActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "SearchGroupActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    private SearchGroupAdapter searchgroupAdapter; //모임 어댑터
    List<Integer> groupMemberList; // 모임 멤버 정보 파싱 데이터 리스트


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        //모임 멤버 정보 JSON 파일 가져오기
        GetGroupMemberData groupMemberTask = new GetGroupMemberData();
        groupMemberTask.execute("http://" + IP_ADDRESS + "/db/group_member.php", "");

        GroupInit(); //모임 목록 초기화
        //일정 정보 JSON 파일 가져오기
        GetGroupData groupTask = new GetGroupData();
        groupTask.execute("http://" + IP_ADDRESS + "/db/group_info.php", "");
    }

    //모임 목록 초기화
    private void GroupInit() {
        RecyclerView recyclerView = findViewById(R.id.groupAllList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        searchgroupAdapter = new SearchGroupAdapter();
        recyclerView.setAdapter(searchgroupAdapter);
    }

    //모임 정보 JSON 가져오기
    private class GetGroupData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SearchGroupActivity.this,
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
                groupResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
//            String postParameters = params[1];
            String postParameters = "whereTxt=" + "ORDER BY group_idx DESC";

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
    private void groupResult() {

//        Log.e(TAG, "_모임 멤버 수 파싱: "+String.valueOf(groupMemberList));


        // 모임 목록 데이터 저장
        String TAG_JSON = "groupInfo";
        String TAG_GROUP_LIST_IDX = "groupIdx"; //모임 index
        String TAG_GROUP_CATEGORY = "groupCategory"; //모임 카테고리
        String TAG_GROUP_NAME = "groupName"; //모임 이름
        String TAG_GROUP_INTRO = "groupIntro"; //모임 소개
        String TAG_GROUP_IMG = "groupImg"; //모임 대표이미지
        String TAG_GROUP_CITY = "groupCity"; //모임 시도
        String TAG_GROUP_COUNTY = "groupCounty"; //모임 시군구
        String TAG_GROUP_DISTRICT = "groupDistrict"; //모임 읍면동

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            int loopCount = jsonArray.length();

            // 최대 10개까지만 출력함.
            if (loopCount > 10) {
                loopCount = 10;
            }
            for (int i = 0; i < loopCount; i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                int groupIdx = Integer.parseInt(item.getString(TAG_GROUP_LIST_IDX));
                String groupCategory = item.getString(TAG_GROUP_CATEGORY);
                String groupName = item.getString(TAG_GROUP_NAME);
                String groupIntro = item.getString(TAG_GROUP_INTRO);
                String groupImg = item.getString(TAG_GROUP_IMG);
                String groupCity = item.getString(TAG_GROUP_CITY);
                String groupCounty = item.getString(TAG_GROUP_COUNTY);
                String groupDistrict = item.getString(TAG_GROUP_DISTRICT);

                GroupData data = new GroupData();
                data.setGroupIdx(groupIdx);
                data.setGroupCategory(groupCategory);
                data.setGroupTitle(groupName);
                data.setGroupIntro(String.valueOf(Html.fromHtml((groupIntro).replaceAll("<img.+?>", ""))));

                if (groupImg.length() > 0) {
                    data.setGroupThumbnail(groupImg);
                } else {
                    data.setGroupThumbnail("http://www.togetherme.tk/static/images/bg_default_small.gif");
                }

                data.setGroupLocation(groupCity + groupCounty + groupDistrict);

                if (groupMemberList.contains(groupIdx)) {
                    int memberCont = 0;
                    for (int j = 0; j < groupMemberList.size(); j++) {
                        if (groupMemberList.get(j).equals(groupIdx)) {
                            memberCont++;
                        }
                    }
                    data.setGroupMember(memberCont + 1);
                } else {
                    data.setGroupMember(1);
                }


                // listData.add(data);
                searchgroupAdapter.addItem(0, data);

            }

            searchgroupAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            Log.d(TAG, "groupResult : ", e);
        }

    }

    //모임 멤버 정보 JSON 가져오기
    private class GetGroupMemberData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SearchGroupActivity.this,
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
                groupMemberResult();
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

    //모임 멤버 정보 JSON 파싱 후, 데이터 저장하기
    private void groupMemberResult() {

        groupMemberList = new ArrayList<Integer>();

        String TAG_JSON = "groupMember";
        String TAG_GROUP_IDX = "groupIdx"; //모임 index

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String groupIdx = item.getString(TAG_GROUP_IDX);

                groupMemberList.add(Integer.valueOf(groupIdx));
            }

//            Log.e(TAG, "_모임 멤버 수 파싱: "+String.valueOf(groupMemberList));

        } catch (JSONException e) {

            Log.e(TAG, "groupMemberResult : ", e);
        }

    }

}
