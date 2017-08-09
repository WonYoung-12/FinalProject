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

    @ParcelConstructor
    public UserInfo(String email, long userId, String nickname, String thumbnailImagePath) {
        this.email = email;
        this.userId = userId;
        this.nickname = nickname;
        this.thumbnailImagePath = thumbnailImagePath;
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
