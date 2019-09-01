package com.example.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;

import java.util.ArrayList;

public class GroupPhotoSliderActivity extends AppCompatActivity {

    //속성(변수) 초기화
    private static String TAG = "GroupPhotoSliderActivity";
    GroupPhotoSliderAdapter adapter; //슬라이드 어댑터
    ViewPager viewPager; //뷰페이저
    ArrayList<String> imageSlideList; //사진 슬라이드 리스트
    View sliderDescScreen; //설명 화면 영역
    ImageButton closeDescButton; //설명화면 닫기 버튼
    ImageButton closePopupButton; //액티비티 닫기 버튼
    String screenCheck; //화면 설명 확인 유무

    //인텐트로 전달받은 변수
    ArrayList<String> photoListKey; //사진 정보 리스트
    String photoUrlKey; //선택한 사진경로


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_photo_slider);

        //초기화
        imageSlideList = new ArrayList<>();
        sliderDescScreen = findViewById(R.id.sliderDescScreen);
        closeDescButton = findViewById(R.id.closeDescButton);
        closePopupButton = findViewById(R.id.closePopupButton);

        //사진첩 화면 스와이프 설명 확인 유무값 불러오기
        SharedPreferences preferences = getSharedPreferences("sFile", 0);
        screenCheck = preferences.getString("SLIDER_DESC_SCREEN", null);

        if (screenCheck != null) {
            //만약 화면 설명을 봤을경우, 다음부터는 설명화면이 보이지 않도록 한다.
            if (screenCheck.equals("true")) {
                //설명화면영역 숨기기
                sliderDescScreen.setVisibility(View.GONE);
            }
        }

        //설명화면 닫기 버튼을 클릭했을 경우
        closeDescButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //슬라이드 설명 화면을 봤을 경우, SharedPreferences에 화면 확인 여부를 저장한다.
                SharedPreferences preferences = getSharedPreferences("sFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SLIDER_DESC_SCREEN", "true");
                editor.apply();

                //설명화면영역 숨기기
                sliderDescScreen.setVisibility(View.GONE);
            }
        });

        //액티비티 닫기 버튼을 클릭했을 경우
        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현재 액티비티 종료
                finish();
            }
        });

        //액티비티 전달 값 받기
        Intent intent = getIntent();
        photoListKey = intent.getStringArrayListExtra("photoListOri"); //사진 정보 리스트
        photoUrlKey = intent.getStringExtra("photoUrlOri"); //사진 경로

        for (int i = 0; i < photoListKey.size(); i++) {
            imageSlideList.add(photoListKey.get(i));
        }

        //뷰페이저 어댑터 초기화
        viewPager = findViewById(R.id.view);
        adapter = new GroupPhotoSliderAdapter(this, imageSlideList);

        //뷰페이저에 어댑터를 연결해준다.
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(imageSlideList.indexOf(photoUrlKey));

    }
}
