package com.example.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class FindPasswordActivity extends AppCompatActivity {

    //속성(변수) 선언
    EditText findInputEmail; //이메일 입력란
    TextView findRegisterButton; //회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        //초기화
        findInputEmail = findViewById(R.id.findInputEmail);
        findRegisterButton = findViewById(R.id.findRegisterButton);

        //회원가입 문구 넣기
        String registerBtnString = "아직회원이 아니세요? <b>회원가입하기</b>";
        findRegisterButton.setText(Html.fromHtml(registerBtnString));

        //회원가입 버튼을 클릭했을 경우
        findRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindPasswordActivity.this, RegisterAgreeActivity.class);
                startActivity(intent);
            }
        });

    }
}
