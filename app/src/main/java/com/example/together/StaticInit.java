package com.example.together;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaticInit extends AppCompatActivity {

    public static int PAGE_GROUP_INDEX; //현재 페이지의 모임 index
    public static String PAGE_GROUP_NAME; //현재 페이지의 모임 이름

    public static String IP_ADDRESS = "13.125.221.240";
    //TODO 로그인 임시 메일
    public static String loginUserId = "jaewons1218@gmail.com";
//    public static List<String> userInfoList = new ArrayList<String>(); // 회원정보 리스트

    // 원하는 날짜 형식 출력하기
    public static String getDateFormat(String inputDate, String dateType, String dateType2) {

        DateFormat df = new SimpleDateFormat(dateType, Locale.KOREA);
        DateFormat ddf = new SimpleDateFormat(dateType2, Locale.KOREA);

        String today = null;
        try {
            today = ddf.format(df.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return today;
    }


    // 오늘 날짜 구하는 함수
    public static String getToday(String dateType) {

//        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        SimpleDateFormat format = new SimpleDateFormat(dateType, Locale.KOREA);

        Date time = new Date();

        String today = format.format(time);

        return today;
    }

    // 두날짜의 차이 구하기
    public static String doDiffOfDate(String end) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

            Date date = new Date();
            String today = formatter.format(date);

            Date beginDate = null;
            try {
                beginDate = formatter.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();

            // 날짜 차이 결과
            long diffDays = (int) diff / (24 * 60 * 60 * 1000);

            diffDays = Math.abs(diffDays);

            if (diffDays == 0) {
                return "오늘";
            } else {
                return "D-" + diffDays;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return String.valueOf(0);
        }

    }

    // 해당 날짜의 요일을 구하는 함수
    public static String getDateWeek(String inputDate, String dateType) {

        DateFormat df = new SimpleDateFormat(dateType, Locale.KOREA);
        DateFormat ddf = new SimpleDateFormat("E", Locale.KOREA);

        String week = null;
        try {
            week = ddf.format(df.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return week;
    }
}
