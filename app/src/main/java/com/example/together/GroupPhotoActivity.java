package com.example.together;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.together.StaticInit.PAGE_GROUP_HOST;
import static com.example.together.StaticInit.PAGE_GROUP_INDEX;
import static com.example.together.StaticInit.PAGE_GROUP_NAME;
import static com.example.together.StaticInit.loginUserId;

public class GroupPhotoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //속성(변수) 선언
    private static String TAG = "GroupPhotoActivity";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.togetherme.tk/";
    private String jsonString; // json 데이터 파일
    List<String> groupMemberList; //모임 멤버 정보 파싱 데이터 리스트
    List<String> photoList = new ArrayList<>(); //사진 정보 파싱 데이터 리스트
    static GroupPhotoAdapter groupPhotoAdapter; //모임 사진첩 어댑터
    private Uri uri;
    TextView pageGroupTit; //페이지 상단 모임 이름
    TextView infoTab; //정보탭 버튼
    TextView scheduleTab; //일정탭 버튼
    TextView boardTab; //게시판탭 버튼a
    TextView photoTab; //사진첩탭 버튼
    TextView chatTab; //채팅탭 버튼
    ImageButton photoAddBtn; //일정 추가 버튼
    TextView noneContent; //등록된 사진이 없을경우 표시되는 문구

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_photo);

//        Log.e(TAG, "모임 index: " + PAGE_GROUP_INDEX);

        //초기화
        pageGroupTit = findViewById(R.id.pageGroupTit);
        infoTab = findViewById(R.id.infoTab);
        scheduleTab = findViewById(R.id.scheduleTab);
        boardTab = findViewById(R.id.boardTab);
        photoTab = findViewById(R.id.photoTab);
        chatTab = findViewById(R.id.chatTab);
        photoAddBtn = findViewById(R.id.photoAddBtn);
        noneContent = findViewById(R.id.noneContent);


        //페이지 모임 이름 설정
        pageGroupTit.setText(PAGE_GROUP_NAME);

        //정보탭 버튼을 클릭했을 경우
        infoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupPhotoActivity.this, GroupInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //일정탭 버튼을 클릭했을 경우
        scheduleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupPhotoActivity.this, GroupScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //게시판탭 버튼을 클릭했을 경우
        boardTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupPhotoActivity.this, GroupBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //사진첩탭 버튼을 클릭했을 경우
        photoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupPhotoActivity.this, GroupPhotoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //채팅탭 버튼을 클릭했을 경우
        chatTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupPhotoActivity.this, GroupChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        // 이미지 추가 버튼을 클릭했을 경우
        photoAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        //모임 멤버 정보 JSON 가져오기
        GetgroupMemberData groupMemberTask = new GetgroupMemberData();
        groupMemberTask.execute("http://" + IP_ADDRESS + "/db/group_member.php", "");


        //로그인한 이메일과 모임장의 이메일이 다를 경우에는 이미지 추가 버튼을 숨긴다.
//        Log.e(TAG, "로그인 ID: " + loginUserId);
//        Log.e(TAG, "모임장 ID: " + PAGE_GROUP_HOST);
        if(!PAGE_GROUP_HOST.equals(loginUserId)){
            photoAddBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PhotoInit(); //사진첩 목록 초기화
        //모임 사진 정보 JSON 가져오기
        GetGroupInfoData groupPhotoTask = new GetGroupInfoData();
        groupPhotoTask.execute("http://" + IP_ADDRESS + "/db/group_gallery.php", "");
    }

    //사진첩 목록 초기화
    private void PhotoInit() {
        RecyclerView recyclerView = findViewById(R.id.groupPhotoList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 스크롤 방향 설정 VERTICAL or HORIZONTAL
//        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setNestedScrollingEnabled(false); // 스크롤 중복 방지

        groupPhotoAdapter = new GroupPhotoAdapter();
        recyclerView.setAdapter(groupPhotoAdapter);

        //리사이클러뷰 아이템 클릭 이벤트
        groupPhotoAdapter.setOnItemClickListener(new GroupPhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, GroupPhotoData data) {

                Log.e(TAG, "불러온 사진 경로: " + photoList);
                // 아이템 클릭 이벤트를 Activity에서 처리
                    Intent intent = new Intent(GroupPhotoActivity.this, GroupPhotoSliderActivity.class);
//                    intent.putExtra("placeName", data.getPlaceName());
//                    intent.putExtra("placeAddress", data.getPlaceAddress());
                    startActivity(intent);
            }
        });
    }

    //모임 사진 정보 JSON 가져오기
    private class GetGroupInfoData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupPhotoActivity.this,
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
                groupPhotoResult();
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

    //모임 사진진 정보 JSON 파싱 후, 데이터 저장하기
    private void groupPhotoResult() {

        String TAG_JSON = "groupGallery";
        String TAG_GALLERY_IDX = "galleryIdx"; //사진 index
        String TAG_GALLERY_IMG = "galleryImg"; //사진첩 사진
        String TAG_GROUP_IDX = "groupIdx"; //모임 index

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String galleryIdx = item.getString(TAG_GALLERY_IDX);
                String galleryImg = item.getString(TAG_GALLERY_IMG);
                String groupIdx = item.getString(TAG_GROUP_IDX);

                // 각 List의 값들을 data 객체에 set 해줍니다.
                GroupPhotoData data = new GroupPhotoData();
                data.setPhotoIdx(Integer.parseInt(galleryIdx)); //사진 index
                data.setGroupPhoto(galleryImg); //사진 이미지 경로

                // 각 값이 들어간 data를 adapter에 추가합니다.
                groupPhotoAdapter.addItem(0, data);

                //불러온 사진 경로 저장하기
                photoList.add(galleryImg);


            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            groupPhotoAdapter.notifyDataSetChanged();

            if(groupPhotoAdapter.listData.size() == 0){
                noneContent.setVisibility(View.VISIBLE);
            } else {
                noneContent.setVisibility(View.GONE);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, GroupPhotoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, GroupPhotoActivity.this);
                File file = new File(filePath);
                Log.e(TAG, "Filename " + file.getName());
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_PATH)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
                Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
                fileUpload.enqueue(new Callback<UploadObject>() {
                    @Override
                    public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
//                        Toast.makeText(GroupPhotoActivity.this, "Response_onResponse " + response.raw().message(), Toast.LENGTH_LONG).show();
//                        Toast.makeText(GroupPhotoActivity.this, "Success_onResponse " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
//                        Log.e(TAG, "저장된 이미지경로 : " + response.body().getSuccess());

                        if(response.body().getSuccess().equals("error uploading file")){
                            Toast.makeText(GroupPhotoActivity.this, "이미지 업로드를 실패했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            String photoPath = SERVER_PATH + response.body().getSuccess();

                            //리사이클러뷰 저장
                            GroupPhotoData data = new GroupPhotoData();
                            data.setGroupPhoto(photoPath); //상단 멤버 수 정보

                            groupPhotoAdapter.addItem(0, data);
                            groupPhotoAdapter.notifyDataSetChanged();

                            if(groupPhotoAdapter.listData.size() == 0){
                                noneContent.setVisibility(View.VISIBLE);
                            } else {
                                noneContent.setVisibility(View.GONE);
                            }

                            //DB 저장
                            InsertData task = new InsertData();
                            //이미지 경로, 모임 인덱스 순서
                            task.execute("http://" + IP_ADDRESS + "/group/photo_add.php",
                                    photoPath,
                                    String.valueOf(PAGE_GROUP_INDEX)
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadObject> call, Throwable t) {
                        Log.e(TAG, "Error " + t.getMessage());
                    }
                });
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (uri != null) {
            String filePath = getRealPathFromURIPath(uri, GroupPhotoActivity.this);
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
            Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
//                    Toast.makeText(GroupPhotoActivity.this, "Success_3 " + response.message(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(GroupPhotoActivity.this, "Success_4 " + response.body().toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<UploadObject> call, Throwable t) {
                    Log.e(TAG, "Error " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "Permission has been denied");
    }

    //DB에 모임 이미지 저장하는 함수
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupPhotoActivity.this,
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
            String photoUrl = params[1]; //이미지 경로
            String groupIdx = params[2]; //모임 인덱스

            //이미지 경로, 모임 인덱스 순서
            String postParameters = "photoUrl=" + photoUrl + "&groupIdx=" + groupIdx;

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

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    //모임 게시판 정보 JSON 가져오기
    private class GetgroupMemberData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(GroupPhotoActivity.this,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
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
                groupMemberResult();


                //로그인한 이메일과 모임장 또는 모임가입 회원의 이메일이 다를 경우에는 게시글 추가 버튼을 나타낸다.
                if(PAGE_GROUP_HOST.equals(loginUserId) || groupMemberList.contains(loginUserId)){
                    //
                } else {
                    Toast.makeText(GroupPhotoActivity.this, "모임멤버만 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "groupIdx=" + PAGE_GROUP_INDEX;

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

    //모임 게시판 정보 JSO 파싱 후, 데이터 저장하기
    private void groupMemberResult() {

        groupMemberList = new ArrayList<>();

        String TAG_JSON = "groupMember"; //멤버 이메일
        String TAG_MEMBER_USER = "memberUser"; //멤버 이메일

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String memberEmail = item.getString(TAG_MEMBER_USER);
                groupMemberList.add(memberEmail);
            }

        } catch (JSONException e) {

            Log.e(TAG, "showResult : ", e);
        }

    }
}
