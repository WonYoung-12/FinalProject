package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by kwy2868 on 2017-07-25.
 */
@Parcel(Parcel.Serialization.BEAN)
public class UserInfo {
    private String email;
    private long userId;
    private String nickname;
    private String thumbnailImagePath;

    // 네이버 카카오 로그인 구분을 위한 flag 값.
    // 0이면 네이버로 로그인, 1이면 카카오로 로그인.
    private int flag;
    // 알람 On Off를 위한 값. 기본 off 상태로 0 설정.
    private int notiFlag = 0;

    @ParcelConstructor
    public UserInfo(String email, long userId, String nickname, String thumbnailImagePath, int flag) {
        this.email = email;
        this.userId = userId;
        this.nickname = nickname;
        this.thumbnailImagePath = thumbnailImagePath;
        this.flag = flag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getNotiFlag() {
        return notiFlag;
    }

    public void setNotiFlag(int notiFlag) {
        this.notiFlag = notiFlag;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "email='" + email + '\'' +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", thumbnailImagePath='" + thumbnailImagePath + '\'' +
                '}';
    }
}
