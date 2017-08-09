package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by kwy2868 on 2017-08-08.
 */

// 병원에 대한 후기.
@Parcel(Parcel.Serialization.BEAN)
public class Review {
    private int num;
    // 어느 병원에 남긴건가.
    private int hospitalNum;
    // 어떤 유저가 쓴건가.
    private long userId;
    private String title;
    private int cost;
    private String content;
    private String date;

    @ParcelConstructor
    public Review(int hospitalNum, long userId, String title, int cost, String content, String date) {
        this.hospitalNum = hospitalNum;
        this.userId = userId;
        this.title = title;
        this.cost = cost;
        this.content = content;
        this.date = date;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
}
