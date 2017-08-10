package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-10.
 */

public class Favorite {
    private long userId;
    // 병원 번호.
    private int num;

    public Favorite(long userId, int num) {
        this.userId = userId;
        this.num = num;
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
}
