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
    public static String PAGE_GROUP_HOST; //현재 페이지의 모임장
    public static String IP_ADDRESS = "13.125.221.240";
    public static String loginUserId = "";
    public static String loginUserName = "";

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


    //해당 법정지역의 시군구를 구하는 함수
    public static String countyResult(String county){
        switch (county) {
            //제주특별자치도
            case "5011":
                county = "제주시";
                break;
            case "5013":
                county = "서귀포시";
                break;

            //경상남도
            case "4812":
                county = "창원시";
                break;
            case "4817":
                county = "진주시";
                break;
            case "4822":
                county = "통영시";
                break;
            case "4824":
                county = "사천시";
                break;
            case "4825":
                county = "김해시";
                break;
            case "4827":
                county = "밀양시";
                break;
            case "4831":
                county = "거제시";
                break;
            case "4833":
                county = "양산시";
                break;
            case "4872":
                county = "의령군";
                break;
            case "4873":
                county = "함안군";
                break;
            case "4874":
                county = "창녕군";
                break;
            case "4882":
                county = "고성군";
                break;
            case "4884":
                county = "남해군";
                break;
            case "4885":
                county = "하동군";
                break;
            case "4886":
                county = "산청군";
                break;
            case "4887":
                county = "함양군";
                break;
            case "4888":
                county = "거창군";
                break;
            case "4889":
                county = "합천군";
                break;

            //경상북도
            case "4711":
                county = "포항시";
                break;
            case "4713":
                county = "경주시";
                break;
            case "4715":
                county = "김천시";
                break;
            case "4717":
                county = "안동시";
                break;
            case "4719":
                county = "구미시";
                break;
            case "4721":
                county = "영주시";
                break;
            case "4723":
                county = "영천시";
                break;
            case "4725":
                county = "상주시";
                break;
            case "4728":
                county = "문경시";
                break;
            case "4729":
                county = "경산시";
                break;
            case "4772":
                county = "군위군";
                break;
            case "4773":
                county = "의성군";
                break;
            case "4775":
                county = "청송군";
                break;
            case "4776":
                county = "영양군";
                break;
            case "4777":
                county = "영덕군";
                break;
            case "4782":
                county = "청도군";
                break;
            case "4783":
                county = "고령군";
                break;
            case "4784":
                county = "성주군";
                break;
            case "4785":
                county = "칠곡군";
                break;
            case "4790":
                county = "예천군";
                break;
            case "4792":
                county = "봉화군";
                break;
            case "4793":
                county = "울진군";
                break;
            case "4794":
                county = "울릉군";
                break;

            //전라남도
            case "4611":
                county = "목포시";
                break;
            case "4613":
                county = "여수시";
                break;
            case "4615":
                county = "순천시";
                break;
            case "4617":
                county = "나주시";
                break;
            case "4623":
                county = "광양시";
                break;
            case "4671":
                county = "담양군";
                break;
            case "4672":
                county = "곡성군";
                break;
            case "4673":
                county = "구례군";
                break;
            case "4677":
                county = "고흥군";
                break;
            case "4678":
                county = "보성군";
                break;
            case "4679":
                county = "화순군";
                break;
            case "4680":
                county = "장흥군";
                break;
            case "4681":
                county = "강진군";
                break;
            case "4682":
                county = "해남군";
                break;
            case "4683":
                county = "영암군";
                break;
            case "4684":
                county = "무안군";
                break;
            case "4686":
                county = "함평군";
                break;
            case "4687":
                county = "영광군";
                break;
            case "4688":
                county = "장성군";
                break;
            case "4689":
                county = "완도군";
                break;
            case "4690":
                county = "진도군";
            case "4691":
                county = "신안군";

                //전라북도
            case "4511":
                county = "전주시";
            case "4513":
                county = "군산시";
            case "4514":
                county = "익산시";
            case "4518":
                county = "정읍시";
            case "4519":
                county = "남원시";
            case "4521":
                county = "김제시";
            case "4571":
                county = "완주군";
            case "4572":
                county = "진안군";
            case "4573":
                county = "무주군";
            case "4574":
                county = "장수군";
                break;
            case "4575":
                county = "임실군";
                break;
            case "4577":
                county = "순창군";
                break;
            case "4579":
                county = "고창군";
                break;
            case "4580":
                county = "부안군";
                break;

            //충청남도
            case "4413":
                county = "천안시";
                break;
            case "4415":
                county = "공주시";
                break;
            case "4418":
                county = "보령시";
                break;
            case "4420":
                county = "아산시";
                break;
            case "4421":
                county = "서산시";
                break;
            case "4423":
                county = "논산시";
                break;
            case "4425":
                county = "계룡시";
                break;
            case "4427":
                county = "당진시";
                break;
            case "4471":
                county = "금산군";
                break;
            case "4476":
                county = "부여군";
                break;
            case "4477":
                county = "서천군";
                break;
            case "4479":
                county = "청양군";
                break;
            case "4480":
                county = "홍성군";
                break;
            case "4481":
                county = "예산군";
                break;
            case "4482":
                county = "태안군";
                break;

            //충청북도
            case "4311":
                county = "청주시";
                break;
            case "4313":
                county = "충주시";
                break;
            case "4315":
                county = "제천시";
                break;
            case "4370":
                county = "청원군";
                break;
            case "4372":
                county = "보은군";
                break;
            case "4373":
                county = "옥천군";
                break;
            case "4374":
                county = "영동군";
                break;
            case "4375":
                county = "진천군";
                break;
            case "4376":
                county = "괴산군";
                break;
            case "4377":
                county = "음성군";
                break;
            case "4380":
                county = "단양군";
                break;

            //강원도도
            case "4211":
               county = "춘천시";
                break;
            case "4213":
                county = "원주시";
                break;
            case "4215":
                county = "강릉시";
                break;
            case "4217":
                county = "동해시";
                break;
            case "4219":
                county = "태백시";
                break;
            case "4221":
                county = "속초시";
                break;
            case "4223":
                county = "삼척시";
                break;
            case "4272":
                county = "홍천군";
                break;
            case "4273":
                county = "횡성군";
                break;
            case "4275":
                county = "영월군";
                break;
            case "4276":
                county = "평창군";
                break;
            case "4277":
                county = "정선군";
                break;
            case "4278":
                county = "철원군";
                break;
            case "4279":
                county = "화천군";
                break;
            case "4280":
                county = "양구군";
                break;
            case "4281":
                county = "인제군";
                break;
            case "4282":
                county = "고성군";
                break;
            case "4283":
                county = "양양군";
                break;

            //경기도
            case "4111":
                county = "수원시";
                break;
            case "4113":
                county = "성남시";
                break;
            case "4115":
                county = "의정부시";
                break;
            case "4117":
                county = "안양시";
                break;
            case "4119":
                county = "부천시";
                break;
            case "4121":
                county = "광명시";
                break;
            case "4122":
                county = "평택시";
                break;
            case "4125":
                county = "동두천시";
                break;
            case "4127":
                county = "안산시";
                break;
            case "4128":
                county = "고양시";
                break;
            case "4129":
                county = "과천시";
                break;
            case "4131":
                county = "구리시";
                break;
            case "4136":
                county = "남양주시";
                break;
            case "4137":
                county = "오산시";
                break;
            case "4139":
                county = "시흥시";
                break;
            case "4141":
                county = "군포시";
                break;
            case "4143":
                county = "의왕시";
                break;
            case "4145":
                county = "하남시";
                break;
            case "4146":
                county = "용인시";
                break;
            case "4148":
                county = "파주시";
                break;
            case "4150":
                county = "이천시";
                break;
            case "4155":
                county = "안성시";
                break;
            case "4157":
                county = "김포시";
                break;
            case "4159":
                county = "화성시";
                break;
            case "4161":
                county = "광주시";
                break;
            case "4163":
                county = "양주시";
                break;
            case "4165":
                county = "포천시";
                break;
            case "4167":
                county = "여주시";
                break;
            case "4180":
                county = "연천군";
                break;
            case "4182":
                county = "가평군";
                break;
            case "4183":
                county = "양평군";
                break;

            //울산광역시
            case "3111":
                county = "중구";
                break;
            case "3114":
                county = "남구";
                break;
            case "3117":
                county = "동구";
                break;
            case "3120":
                county = "북구";
                break;
            case "3171":
                county = "울주군";
                break;

            //대전광역시
            case "3011":
                county = "동구";
                break;
            case "3014":
                county = "중구";
                break;
            case "3017":
                county = "서구";
                break;
            case "3020":
                county = "유성구";
                break;
            case "3023":
                county = "대덕구";
                break;

            //광주광역시
            case "2911":
                county = "동구";
                break;
            case "2914":
                county = "서구";
                break;
            case "2915":
                county = "남구";
                break;
            case "2917":
                county = "북구";
                break;
            case "2920":
                county = "광산구";
                break;

            //인천광역시
            case "2811":
                county = "중구";
                break;
            case "2814":
                county = "동구";
                break;
            case "2817":
                county = "미추홀구";
                break;
            case "2818":
                county = "연수구";
                break;
            case "2820":
                county = "남동구";
                break;
            case "2823":
                county = "부평구";
                break;
            case "2824":
                county = "계양구";
                break;
            case "2826":
                county = "서구";
                break;
            case "2871":
                county = "강화군";
                break;
            case "2872":
                county = "옹진군";
                break;

            //대전광역시
            case "2711":
                county = "중구";
                break;
            case "2714":
                county = "동구";
                break;
            case "2717":
                county = "서구";
                break;
            case "2720":
                county = "남구";
                break;
            case "2723":
                county = "북구";
                break;
            case "2726":
                county = "수성구";
                break;
            case "2729":
                county = "달서구";
                break;
            case "2771":
                county = "달성군";
                break;

            //부산광역시
            case "2611":
                county = "중구";
                break;
            case "2614":
                county = "서구";
                break;
            case "2617":
                county = "동구";
                break;
            case "2620":
                county = "영도구";
                break;
            case "2623":
                county = "부산진구";
                break;
            case "2626":
                county = "동래구";
                break;
            case "2629":
                county = "남구";
                break;
            case "2632":
                county = "북구";
                break;
            case "2635":
                county = "해운대구";
                break;
            case "2638":
                county = "사하구";
                break;
            case "2641":
                county = "금정구";
                break;
            case "2644":
                county = "강서구";
                break;
            case "2647":
                county = "연제구";
                break;
            case "2650":
                county = "수영구";
                break;
            case "2653":
                county = "사상구";
                break;
            case "2671":
                county = "기장군";
                break;

            //서울특별시
            case "1111":
                county = "종로구";
                break;
            case "1114":
                county = "중구";
                break;
            case "1117":
                county = "용산구";
                break;
            case "1120":
                county = "성동구";
                break;
            case "1121":
                county = "광진구";
                break;
            case "1123":
                county = "동대문구";
                break;
            case "1126":
                county = "중랑구";
                break;
            case "1129":
                county = "성북구";
                break;
            case "1130":
                county = "강북구";
                break;
            case "1132":
                county = "도봉구";
                break;
            case "1135":
                county = "노원구";
                break;
            case "1138":
                county = "은평구";
                break;
            case "1141":
                county = "서대문구";
                break;
            case "1144":
                county = "마포구";
                break;
            case "1147":
                county = "양천구";
                break;
            case "1150":
                county = "강서구";
                break;
            case "1153":
                county = "구로구";
                break;
            case "1154":
                county = "금천구";
                break;
            case "1156":
                county = "영등포구";
                break;
            case "1159":
                county = "동작구";
                break;
            case "1162":
                county = "관악구";
                break;
            case "1165":
                county = "서초구";
                break;
            case "1168":
                county = "강남구";
                break;
            case "1171":
                county = "송파구";
                break;
            case "1174":
                county = "강동구";
                break;

            default:
                county = " ";
        }

        return county;
    }
}