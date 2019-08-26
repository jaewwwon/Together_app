package com.example.together;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;

public class GroupPhotoSliderActivity extends AppCompatActivity {

    //속성(변수) 초기화
    GroupPhotoSliderAdapter adapter; //슬라이드 어댑터
    ViewPager viewPager; //뷰페이저

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_photo_slider);

        //초기화
        viewPager = findViewById(R.id.view);
        adapter = new GroupPhotoSliderAdapter(this);

        //뷰페이저에 어댑터를 연결해준다.
        viewPager.setAdapter(adapter);

    }
}
