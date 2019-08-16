package com.example.together;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SearchLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //속성(변수) 선언
    private static String TAG = "SearchLocationActivity";
    private GoogleMap mMap; //구글맵
    private Geocoder geocoder; //지오코더
    private EditText inputLocation; //장소 입력란
    private Button searchButton; //검색 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        //초기화
        inputLocation = findViewById(R.id.inputLocation);
        searchButton = findViewById(R.id.searchButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // 구글맵 객체를 불러온다.
        mMap = googleMap;

        // 지오코더
        geocoder = new Geocoder(this);

        // 맵 터치 이벤트 구현 //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("마커 좌표");
                Double latitude = point.latitude; // 위도
                Double longitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정

                String address = "";

//                List<Address> addresses;
//                addresses = geocoder.getFromLocation(
//                        point.latitude,
//                        point.longitude,
//                        7);


                mOptions.snippet(address);
//                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                // 마커(핀) 추가
                googleMap.addMarker(mOptions);
            }
        });

        // 마커 클릭에 대한 이벤트 처리
        mMap.setOnMarkerClickListener(this);


        // 버튼 이벤트
        searchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = inputLocation.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e(TAG, "addressList : " + addressList.get(0).toString());

                // 콤마를 기준으로 split
                String[] splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
                Log.e(TAG, "address : " + address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                Log.e(TAG, "위도 : " + latitude);
                Log.e(TAG, "경도 : " + longitude);

                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                // 마커 생성
                MarkerOptions mOptions2 = new MarkerOptions();
//                mOptions2.title("search result");
                mOptions2.snippet(address);
                mOptions2.position(point);

                // 마커 생성 (마커를 나타낸다.)
                mMap.addMarker(mOptions2);


                // 해당 좌표로 화면 줌
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
            }
        });


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //결과 값을 resultIntent 에 담아서 PopupScheduleAddActivity 로 전달하고 현재 Activity는 종료.
        Intent resultIntent = new Intent();
        resultIntent.putExtra("locationResult", marker.getSnippet());
        setResult(RESULT_OK,resultIntent);
        finish();
        return false;
    }
}

//package com.example.together;
//
//        import android.content.Intent;
//        import android.location.Address;
//        import android.location.Geocoder;
//        import android.net.Uri;
//        import android.support.v4.app.FragmentActivity;
//        import android.os.Bundle;
//        import android.view.View;
//        import android.widget.Button;
//        import android.widget.EditText;
//        import android.util.Log;
//
//        import com.google.android.gms.maps.CameraUpdateFactory;
//        import com.google.android.gms.maps.GoogleMap;
//        import com.google.android.gms.maps.OnMapReadyCallback;
//        import com.google.android.gms.maps.SupportMapFragment;
//        import com.google.android.gms.maps.model.LatLng;
//        import com.google.android.gms.maps.model.Marker;
//        import com.google.android.gms.maps.model.MarkerOptions;
//
//        import java.io.IOException;
//        import java.util.List;
//
//public class SearchLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
//
//    //속성(변수) 선언
//    private static String TAG = "SearchLocationActivity";
//    private GoogleMap mMap; //구글맵
//    private Geocoder geocoder; //지오코더
//    private EditText inputLocation; //장소 입력란
//    private Button searchButton; //검색 버튼
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_location);
//
//        //초기화
//        inputLocation = findViewById(R.id.inputLocation);
//        searchButton = findViewById(R.id.searchButton);
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(final GoogleMap googleMap) {
//
//        // 구글맵 객체를 불러온다.
//        mMap = googleMap;
//
//        // 지오코더
//        geocoder = new Geocoder(this);
//
//        // 맵 터치 이벤트 구현 //
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng point) {
//                MarkerOptions mOptions = new MarkerOptions();
//                // 마커 타이틀
//                mOptions.title("마커 좌표");
//                Double latitude = point.latitude; // 위도
//                Double longitude = point.longitude; // 경도
//                // 마커의 스니펫(간단한 텍스트) 설정
//
//                // 위도,경도 입력 후 변환 버튼 클릭
//                List<Address> list = null;
//                try {
//                    list = geocoder.getFromLocation(
//                            latitude, // 위도
//                            longitude, // 경도
//                            7); // 얻어올 값의 개수
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생");
//                }
////                if (list != null) {
////                    if (list.size()==0) {
////                        tv.setText("해당되는 주소 정보는 없습니다");
////                    } else {
////                        tv.setText(list.get(0).toString());
////                    }
////                }
//
//                // 콤마를 기준으로 split
//                String[] splitStr = list.get(0).toString().split(",");
//                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
//
//                mOptions.snippet(address);
////                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
//
//                // LatLng: 위도 경도 쌍을 나타냄
//                mOptions.position(new LatLng(latitude, longitude));
//
//                // 마커(핀) 추가
//                googleMap.addMarker(mOptions);
//            }
//        });
//
////        // 마커 클릭에 대한 이벤트 처리
////        mMap.setOnMarkerClickListener(this);
//
//        //정보창 클릭 리스너
//        mMap.setOnInfoWindowClickListener(this);
//
//
//        // 버튼 이벤트
//        searchButton.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String str = inputLocation.getText().toString();
//                List<Address> addressList = null;
//                try {
//                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
//                    addressList = geocoder.getFromLocationName(
//                            str, // 주소
//                            10); // 최대 검색 결과 개수
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Log.e(TAG, "addressList : " + addressList.get(0).toString());
//
//                // 콤마를 기준으로 split
//                String[] splitStr = addressList.get(0).toString().split(",");
//                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
//                Log.e(TAG, "address : " + address);
//
//                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
//                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
//                Log.e(TAG, "위도 : " + latitude);
//                Log.e(TAG, "경도 : " + longitude);
//
//                // 좌표(위도, 경도) 생성
//                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
//
//                // 마커 생성
//                MarkerOptions mOptions2 = new MarkerOptions();
////                mOptions2.title("search result");
//                mOptions2.snippet(address);
//                mOptions2.position(point);
//
//                // 마커 생성 (마커를 나타낸다.)
//                mMap.addMarker(mOptions2);
//
//
//                // 해당 좌표로 화면 줌
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
//            }
//        });
//
//
//        //TODO 현재위치로 시작되도록 수정하기
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(37.5197889, 126.9403083);
//        mMap.addMarker(new MarkerOptions().position(sydney));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
//
//
//    //정보창 클릭 리스너
//    @Override
//    public void onInfoWindowClick(Marker marker) {
//        //결과 값을 resultIntent 에 담아서 PopupScheduleAddActivity 로 전달하고 현재 Activity는 종료.
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("locationResult", marker.getSnippet());
//        setResult(RESULT_OK,resultIntent);
//        finish();
//    }
//
////    //마커 클릭 리스너
////    @Override
////    public boolean onMarkerClick(Marker marker) {
//////        //결과 값을 resultIntent 에 담아서 PopupScheduleAddActivity 로 전달하고 현재 Activity는 종료.
//////        Intent resultIntent = new Intent();
//////        resultIntent.putExtra("locationResult", marker.getSnippet());
//////        setResult(RESULT_OK,resultIntent);
//////        finish();
////        return false;
////    }
//
//
//}