package com.example.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SettingActivity extends AppCompatActivity {

    //속성(변수) 선언
    private static String TAG = "SettingActivity";
    TextView homeNav; //하단메뉴 - 홈
    TextView manageNav; //하단메뉴 - 모임관리
    TextView settingNav; //하단메뉴 - 설정
    TextView profileBtn; //프로필 수정 버튼
    TextView termsBtn; //이용약관 버튼
    TextView privacyBtn; //개인정보처리방침
    TextView leaveBtn; //회원탈퇴 버튼
    TextView logoutBtn; //로그아웃 버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //초기화
        homeNav = findViewById(R.id.homeNav);
        manageNav = findViewById(R.id.manageNav);
        settingNav = findViewById(R.id.settingNav);
        profileBtn = findViewById(R.id.profileBtn);
        termsBtn = findViewById(R.id.termsBtn);
        privacyBtn = findViewById(R.id.privacyBtn);
        leaveBtn = findViewById(R.id.leaveBtn);
        logoutBtn = findViewById(R.id.logoutBtn);


        //하단메뉴 - 홈 버튼을 클릭했을 경우
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //하단메뉴 - 모임관리 버튼을 클릭했을 경우
        manageNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, ManageJoinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //하단메뉴 - 설정 버튼을 클릭했을 경우
        settingNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //프로필 수정 버튼을 클릭했을 경우
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SettingProfileActivity.class);
                startActivity(intent);
            }
        });

        //이용약관 수정 버튼을 클릭했을 경우
        termsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SettingTermsActivity.class);
                startActivity(intent);
            }
        });

        //개인정보취급방침 수정 버튼을 클릭했을 경우
        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SettingPrivacyActivity.class);
                startActivity(intent);
            }
        });

        //회원탈퇴 버튼을 클릭했을 경우
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, SettingLeaveActivity.class);
                startActivity(intent);
            }
        });


        //로그아웃 버튼을 클릭했을 경우
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다이얼로그 띄우기
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                View logoutView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.activity_popup_logout, null, false);
                builder.setView(logoutView);

                final Button cancelButton = logoutView.findViewById(R.id.cancelButton); // 취소 버튼
                final Button logoutButton = logoutView.findViewById(R.id.logoutButton); // 로그아웃 버튼
                final AlertDialog dialog = builder.create();


                //취소 버튼을 눌렀을 경우
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                //로그아웃 버튼을 눌렀을 경우
                logoutButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        //자동 로그인 정보 삭제
                        SharedPreferences pref = getSharedPreferences("sFile", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("USER_LOGIN_ID"); //자동 로그인 아이디값
                        editor.remove("USER_LOGIN_NAME"); //자동 로그인 비밀번호값
                        editor.apply();


                        Toast.makeText(SettingActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        // 기존에 담겨있던 모든 task를 제거하고, 로그인 activity만 보여줌
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
