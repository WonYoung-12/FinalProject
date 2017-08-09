package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-09.
 */

public class GetReviewResult {
    private int resultCode;

    private long userId;
    // 게시글 번호.
    private int num;
    // 병원 번호.
    private int hospitalNum;
    private String title;
    private int cost;
    private String content;
    private String date;

    // 유저 이메일.
    private String email;
    private String nickname;
    private String thumbnailImagePath;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getHospitalNum() {
        return hospitalNum;
    }

    public void setHospitalNum(int hospitalNum) {
        this.hospitalNum = hospitalNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    public void setThumbnailImagePath(String thumbnailImagePath) {
        this.thumbnailImagePath = thumbnailImagePath;
    }
}
