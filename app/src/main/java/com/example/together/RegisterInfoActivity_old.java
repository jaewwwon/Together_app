package com.example.together;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
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


public class RegisterInfoActivity_old extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "RegisterInfoActivity";
    ImageView profileButton; //프로필사진 버튼
    EditText inputName; //이름 입력란
    EditText inputEmail; //이메일 입력란
    Button emailCheckBtn; //이메일 중복검사 버튼
    EditText inputPassword; //비밀번호 입력란
    EditText inputPasswordRe; //비밀번호 재입력란
    Button submitButton; //가입하기 버튼

    //////
    int SELECT_FILE1 = 200;
    int SELECT_FILE2 = 201;
    String selectedPath1;
    String selectedPath2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        //초기화
        profileButton = findViewById(R.id.profileButton);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        emailCheckBtn = findViewById(R.id.emailCheckBtn);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordRe = findViewById(R.id.inputPasswordRe);
        submitButton = findViewById(R.id.submitButton);

        //프로필사진 버튼을 클릭했을 경우
        profileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openGallery(SELECT_FILE1);
            }
        });
    }

    public void openGallery(int req_code) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select file to upload "), req_code);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            if (requestCode == SELECT_FILE1) {
                selectedPath1 = getPath(selectedImageUri);
                Log.e(TAG, "selectedPath1 : " + selectedPath1);
                Log.e(TAG, "selectedPath1 requestCode : " + requestCode);
            }

            if (requestCode == SELECT_FILE2) {
                selectedPath2 = getPath(selectedImageUri);
                Log.e(TAG, "selectedPath2 : " + selectedPath2);
                Log.e(TAG, "selectedPath1 requestCode : " + requestCode);
            }

            Log.e(TAG, "Selected File paths : " + selectedPath1 + "," + selectedPath2);
        }
    }

    // 갤러리에서 선택한 이미지의 경로를 얻는 함수
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
