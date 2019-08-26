package com.example.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class SettingPrivacyActivity extends AppCompatActivity {

    //속성(변수) 선언
    ImageView prevButton; //뒤로가기 버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_privacy);

        //초기화
        prevButton = findViewById(R.id.prevButton);

        //뒤로가기 버튼을 클릭했을 경우
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
