package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-14.
 */

public class AlarmEvent {
    // 이거 차트 번호인데 이걸로 나중에 알람 구분해서 취소할 일이 있으면 취소하자.
    private String num;
    private String title;
    private String description;

    private boolean flag;

    public AlarmEvent(String num, String title, String description, boolean flag) {
        this.num = num;
        this.title = title;
        this.description = description;
        this.flag = flag;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}

