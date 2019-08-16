package com.example.together;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

// Retrofit 2는 인터페이스 클래스를 사용하여 원격 서버와 API 엔드 포인트 브릿지를 빌드합니다.
// 이 인터페이스 클래스는 주석을 사용하여 네트워크 호출이 구성되는 방식을 지정합니다.
// 또한 Retrofit은 대부분의 http 프로토콜 및 메소드를 지원합니다.
// Interface 클래스 안에서 메소드를 정의 할 것입니다.
// 메소드의 이름을 uploadFile로 지정하십시오. 파일 작업을하고 있으므로 멀티 파트 주석을 추가하는 것이 중요합니다.

public interface UploadImageInterface {
    @Multipart
    @POST("/androidImage/android_upload.php")
    Call<UploadObject> uploadFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);
}