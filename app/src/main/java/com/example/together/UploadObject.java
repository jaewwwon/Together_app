package com.example.together;

// UploadObject 클래스는 업로드 성공 여부에 따라 서버응답 오브젝트를 맵핑하는데 사용된다.

public class UploadObject {

    private String success;

    public UploadObject(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }
}
