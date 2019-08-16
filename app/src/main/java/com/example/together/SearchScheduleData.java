package com.example.together;

public class SearchScheduleData {
    private String scheduleWeek; //일정 날짜 요일
    private String scheduleCount; //일정 날짜 카운트
    private String scheduleDate; //일정 날짜
    private String scheduleTitle; //일정 제목
    private String scheduleContent; //일정 내용
    private String scheduleLocation; //일정 장소
    private int scheduleMember; //일정참여 멤버 수
    private int scheduleIdx; //일정 index
    private int groupIdx; //모임 index


    public String getScheduleWeek() {
        return scheduleWeek;
    }

    public void setScheduleWeek(String scheduleWeek) {
        this.scheduleWeek = scheduleWeek;
    }

    public String getScheduleCount() {
        return scheduleCount;
    }

    public void setScheduleCount(String scheduleCount) {
        this.scheduleCount = scheduleCount;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public String getScheduleContent() {
        return scheduleContent;
    }

    public void setScheduleContent(String scheduleContent) {
        this.scheduleContent = scheduleContent;
    }

    public String getScheduleLocation() {
        return scheduleLocation;
    }

    public void setScheduleLocation(String scheduleLocation) {
        this.scheduleLocation = scheduleLocation;
    }

    public int getScheduleMember() {
        return scheduleMember;
    }

    public void setScheduleMember(int scheduleMember) {
        this.scheduleMember = scheduleMember;
    }

    public int getScheduleIdx() {
        return scheduleIdx;
    }

    public void setScheduleIdx(int scheduleIdx) {
        this.scheduleIdx = scheduleIdx;
    }

    public int getGroupIdx() {
        return groupIdx;
    }

    public void setGroupIdx(int groupIdx) {
        this.groupIdx = groupIdx;
    }
}
