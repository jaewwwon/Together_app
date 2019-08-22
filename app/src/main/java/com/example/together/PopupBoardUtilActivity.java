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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

import static com.example.together.StaticInit.IP_ADDRESS;
import static com.example.together.StaticInit.PAGE_GROUP_INDEX;

public class PopupBoardUtilActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "PopupBoardUtilActivity";
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.togetherme.tk/";
    private Uri uri;
    Button boardModifyButton; //게시글 수정하기 버튼
    Button boardDeleteButton; //게시글 삭제하기 버튼
    ImageButton closePopupButton; //팝업 닫기 버튼
    private int itemPositionKey; //intent로 전달받은 아이템 위치값
    private int itemSizeKey; //intent로 전달받은 아이템 크기값
    private int boardIdxKey; //intent로 전달받은 게시글 index 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 상단 타이틀 제거
        setContentView(R.layout.activity_popup_board_util);

        //초기화
        boardModifyButton = findViewById(R.id.boardModifyButton);
        boardDeleteButton = findViewById(R.id.boardDeleteButton);
        closePopupButton = findViewById(R.id.closePopupButton);

        // 인텐트값 넘겨받기
        Intent intent = getIntent();
        itemPositionKey = intent.getIntExtra("itemPositionOri", 0); //해당 게시글 아이템 위치값
        itemSizeKey = intent.getIntExtra("itemSizeOri", 0); //해당 게시글 아이템 사이즈
        boardIdxKey = intent.getIntExtra("boardIdxOri", 0); //게시글 index
//        Log.e(TAG, "게시글 index 정보: " + boardIdxKey);


        //게시글 수정하기 버튼을 눌렀을 경우
        boardModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PopupBoardUtilActivity.this, GroupBoardModifyActivity.class);
                intent.putExtra("itemPositionOri", itemPositionKey); //해당 게시글 아이템 위치값
                intent.putExtra("itemSizeOri", itemSizeKey); //해당 게시글 아이템 사이즈
                intent.putExtra("boardIdxOri", boardIdxKey); //게시글 index
                startActivity(intent);
                finish();
            }
        });

        //게시글 삭제하기 버튼을 눌렀을 경우
        boardDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DB 저장
                DeleteData task = new DeleteData();
                //일정 index 보내기
                task.execute("http://" + IP_ADDRESS + "/group/board_delete.php", String.valueOf(boardIdxKey));

                //해당 아이템을 삭제하고, 리사이클러뷰 아이템을 재 정렬 해준다.
                GroupBoardActivity.groupBoardAdapter.removeItem(itemPositionKey);
                GroupBoardActivity.groupBoardAdapter.notifyItemRemoved(itemPositionKey);
                GroupBoardActivity.groupBoardAdapter.notifyItemRangeChanged(itemPositionKey, itemSizeKey - 1);

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
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

    //해당 사진을 삭제하는 함수
    class DeleteData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PopupBoardUtilActivity.this,
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
            int galleryIdx = Integer.parseInt(params[1]);//일정 index

            //일정 index 보내기
            String postParameters = "board=" + boardIdxKey;

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
