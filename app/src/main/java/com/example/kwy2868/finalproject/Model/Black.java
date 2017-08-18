package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-10.
 */

public class Black {
    private int num;

    private String name;
    private String address;
    private String tel;
    private float rating_avg;
    private String imgPath;

    private long userId;
    // 유저의 가입 경로가 네이버인지 카카오인지 구분.
    private int flag;

    public Black(int num, long userId, int flag) {
        this.num = num;
        this.userId = userId;
        this.flag = flag;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public float getRating_avg() {
        return rating_avg;
    }

    public void setRating_avg(float rating_avg) {
        this.rating_avg = rating_avg;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
