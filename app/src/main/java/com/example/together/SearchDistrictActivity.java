package com.example.together;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.together.StaticInit.countyResult;


public class SearchDistrictActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "GroupPhotoActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.togetherme.tk/";
    private SearchDistrictAdapter searchDistrictAdapter; //읍면동목록 어댑터
    private ArrayList<SearchDistrictData> listData;
    private String jsonString; // json 데이터 파일
    private Uri uri;
    ImageView prevButton; //이전 액티비티로 이동하는 버튼
    EditText inputDistrict; //읍면동 입력란


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_district);

        //초기화
        prevButton = findViewById(R.id.prevButton);
        inputDistrict = findViewById(R.id.inputDistrict);

        //이전으로 버튼 클릭시
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        districtInit(); //읍면동 목록 초기화

        //읍면동 입력란의 텍스트에 변화가 있을 경우
        inputDistrict.addTextChangedListener(new TextWatcher() {
            // 입력하기 전에
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            // 입력되는 텍스트에 변화가 있을 때
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //읍면동 JSON 파일 가져오기
                GetDistrictData districtTask = new GetDistrictData();
                districtTask.execute("http://" + IP_ADDRESS + "/db/district_data.php", inputDistrict.getText().toString());
            }

            // 입력이 끝났을 때
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    //읍면동 목록 초기화
    private void districtInit() {
        RecyclerView recyclerView = findViewById(R.id.districtList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
        recyclerView.setLayoutManager(linearLayoutManager);

        searchDistrictAdapter = new SearchDistrictAdapter();
        recyclerView.setAdapter(searchDistrictAdapter);
    }

    //일정 정보 JSON 가져오기
    private class GetDistrictData extends AsyncTask<String, Void, String> {

        //        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(SearchDistrictActivity.this, "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {

                jsonString = result;
                districtResult();
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String districtName = params[1];
            String postParameters = "districtName=" + districtName;

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

    //읍면동 정보 JSON 파싱 후, 데이터 저장하기
    private void districtResult() {
        searchDistrictAdapter.removeAllItem();

        String TAG_JSON = "districtData";
        String TAG_DISTRICT_CODE = "districtCode"; //읍면동 코드
        TAG_DISTRICT_CODE = TAG_DISTRICT_CODE.trim(); //문자열 여백 제거
        String TAG_DISTRICT_NAME = "districtName"; //읍면동 명
        TAG_DISTRICT_NAME = TAG_DISTRICT_NAME.trim(); //문자열 여백 제거

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String districtCode = item.getString(TAG_DISTRICT_CODE);
                String districtName = item.getString(TAG_DISTRICT_NAME);


                String city = districtCode.substring(0, 2);
                //해당 법정지역의 시/도를 구하는 함수
                switch (city) {
                    case "36":
                        city = "세종특별자치시";
                        break;
                    case "50":
                        city = "제주특별자치도";
                        break;
                    case "48":
                        city = "경상남도";
                        break;
                    case "47":
                        city = "경상북도";
                        break;
                    case "46":
                        city = "전라남도";
                        break;
                    case "45":
                        city = "전라북도";
                        break;
                    case "44":
                        city = "충청남도";
                        break;
                    case "43":
                        city = "충청북도";
                        break;
                    case "42":
                        city = "강원도";
                        break;
                    case "41":
                        city = "경기도";
                        break;
                    case "31":
                        city = "울산광역시";
                        break;
                    case "30":
                        city = "대전광역시";
                        break;
                    case "29":
                        city = "광주광역시";
                        break;
                    case "28":
                        city = "인천광역시";
                        break;
                    case "27":
                        city = "대구광역시";
                        break;
                    case "26":
                        city = "부산광역시";
                        break;
                    case "11":
                        city = "서울특별시";
                        break;
                    default:
                        city = "정보없음";
                }

                String county = districtCode.substring(0, 4);
                //해당 법정지역의 시군구를 구하는 함수
                countyResult(county);

                // 데이터 저장
                SearchDistrictData data = new SearchDistrictData();
                data.setDistrictName(districtName);
                data.setDistrictCounty(countyResult(county));
                data.setDistrictCity(city);

                searchDistrictAdapter.addItem(0, data);
            }

            searchDistrictAdapter.notifyDataSetChanged();

        } catch (JSONException e) {

            Log.e(TAG, "scheduleResult : ", e);
        }

    }


}
