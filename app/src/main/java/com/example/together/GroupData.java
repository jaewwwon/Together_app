package com.example.together;

public class GroupData {
    private String groupThumbnail; //모임 대표이미지
    private String groupCategory; //모임 카테고리
    private String groupTitle; //모임 이름
    private String groupIntro; //모임 소개
    private String groupLocation; //모임 장소
    private int groupMember; //모임회원 수
    private int groupIdx; //모임 index

    public String getGroupThumbnail() {
        return groupThumbnail;
    }

    public void setGroupThumbnail(String groupThumbnail) {
        this.groupThumbnail = groupThumbnail;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupIntro() {
        return groupIntro;
    }

    public void setGroupIntro(String groupIntro) {
        this.groupIntro = groupIntro;
    }

    public String getGroupLocation() {
        return groupLocation;
    }

    public void setGroupLocation(String groupLocation) {
        this.groupLocation = groupLocation;
    }

    public int getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(int groupMember) {
        this.groupMember = groupMember;
    }

    public int getGroupIdx() {
        return groupIdx;
    }

    public void setGroupIdx(int groupIdx) {
        this.groupIdx = groupIdx;
    }
}
