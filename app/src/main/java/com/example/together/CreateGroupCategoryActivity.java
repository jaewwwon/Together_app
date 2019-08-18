package com.example.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateGroupCategoryActivity extends AppCompatActivity {

    //속성(변수) 선언
    RadioGroup groupCategory; //카테고리 그룹
    Button prevButton; //이전으로 버튼
    Button submitButton; //다음으로 버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_category);

        //초기화
        groupCategory = findViewById(R.id.groupCategory);
        prevButton = findViewById(R.id.prevButton);
        submitButton = findViewById(R.id.submitButton);


        //이전으로 버튼을 클릭했을 경우
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //다음으로 버튼을 클릭했을 경우
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //public int getCheckedRadioButtonId() : 선택된 라디오버튼의 ID값을 반환
                RadioButton radioValue = (RadioButton) findViewById(groupCategory.getCheckedRadioButtonId());
                String groupCategoryOri = radioValue.getText().toString();

                //모임 정보 상세입력 페이지로 선택한 모임카테고리값을 전달한다.
                Intent intent = new Intent(CreateGroupCategoryActivity.this, CreateGroupInfoActivity.class);
                intent.putExtra("groupCategoryOri", groupCategoryOri);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

    }
}
