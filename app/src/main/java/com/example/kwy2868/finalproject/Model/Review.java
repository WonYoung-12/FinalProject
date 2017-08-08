package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;

/**
 * Created by kwy2868 on 2017-08-08.
 */

// 병원에 대한 후기.
@Parcel(Parcel.Serialization.BEAN)
public class Review {
    // 어느 병원에 남긴건가.
    private Hospital hospital;

    // 어떤 유저가 쓴건가.
    private UserInfo user;

    private String title;
    private String cost;
    private String content;

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
