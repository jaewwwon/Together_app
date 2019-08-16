package com.example.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class RegisterAgreeActivity extends AppCompatActivity {

    final static String TAG = "registerActivity01";

    //속성(변수) 선언
    private int checkCount = 0; //체크박스 체크 개수
    CheckBox termsCheck; //이용약관 체크박스
    CheckBox policyCheck; //개인정보취급처리방침 체크박스
    Button agreeButton; //전체동의 버튼
    Button submitButton; //확인 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_agree);

        //초기화
        termsCheck = findViewById(R.id.termsCheck);
        policyCheck = findViewById(R.id.policyCheck);
        submitButton = findViewById(R.id.submitButton);
        agreeButton = findViewById(R.id.agreeButton);


        // 이용약관 체크박스 함수
        termsCheck.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) { //체크가 된경우
                    checkCount += 1;
                    if(checkCount == 2){ //체크박스가 모두 체크된 경우
                        submitButton.setEnabled(true);
                        submitButton.setAlpha(1);
                    } else{
                        submitButton.setEnabled(false);
                        submitButton.setAlpha(0.5f);
                    }
                } else {
                    checkCount -= 1;
                    submitButton.setEnabled(false);
                    submitButton.setAlpha(0.5f);
                }
            }
        });

        //개인정보취급방침 체크박스 함수
        policyCheck.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) { //체크가 된경우
                    checkCount += 1;
                    if(checkCount == 2){ //체크박스가 모두 체크된 경우
                        submitButton.setEnabled(true);
                        submitButton.setAlpha(1);
                    } else{
                        submitButton.setEnabled(false);
                        submitButton.setAlpha(0.5f);
                    }
                } else {
                    checkCount -= 1;
                    submitButton.setEnabled(false);
                    submitButton.setAlpha(0.5f);
                }
            }
        });

        //전체동의 버튼을 눌렀을 경우
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCount = 2;
                termsCheck.setChecked(true); //이용약관 체크박스 활성화
                policyCheck.setChecked(true); //개인정보취급방침 체크박스 활성화
                submitButton.setEnabled(true); //확인 버튼 활성화
                submitButton.setAlpha(1);
            }
        });


        //확인 버튼을 눌렀을 경우
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAgreeActivity.this, RegisterInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
