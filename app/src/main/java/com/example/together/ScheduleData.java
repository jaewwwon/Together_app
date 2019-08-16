package com.example.together;

public class ScheduleData {
    private String groupCategory; //모임 카테고리
    private String scheduleDate; //일정 날짜
    private String scheduleTitle; //일정 제목
    private String scheduleLocation; //일정 장소
    private int scheduleMember; //일정 참석 멤버수
    private int groupIdx; //모임 index

    public String getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
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

    public int getGroupIdx() {
        return groupIdx;
    }

    public void setGroupIdx(int groupIdx) {
        this.groupIdx = groupIdx;
    }
}
