package com.example.together;

public class GroupBoardData {
    private String boardCategory; //게시글 카테고리
    private String boardTitle; //게시글 제목
    private String boardContent; //게시글 내용
    private String boardUser; //게시글 작성자
    private String boardDate; //게시글 작성일
    private int boardView; //게시글 조회수
    private int boardIdx; //게시글 index
    private int boardComment; //게시글 댓글 수

    public String getBoardCategory() {
        return boardCategory;
    }

    public void setBoardCategory(String boardCategory) {
        this.boardCategory = boardCategory;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public String getBoardUser() {
        return boardUser;
    }

    public void setBoardUser(String boardUser) {
        this.boardUser = boardUser;
    }

    public String getBoardDate() {
        return boardDate;
    }

    public void setBoardDate(String boardDate) {
        this.boardDate = boardDate;
    }

    public int getBoardView() {
        return boardView;
    }

    public void setBoardView(int boardView) {
        this.boardView = boardView;
    }

    public int getBoardIdx() {
        return boardIdx;
    }

    public void setBoardIdx(int boardIdx) {
        this.boardIdx = boardIdx;
    }

    public int getBoardComment() {
        return boardComment;
    }

    public void setBoardComment(int boardComment) {
        this.boardComment = boardComment;
    }
}
