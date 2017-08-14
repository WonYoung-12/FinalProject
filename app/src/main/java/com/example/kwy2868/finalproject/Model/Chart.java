package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-12.
 */

public class Chart {
    private int num;
    private String petName;
    private long userId;
    // 사용자가 네이버 로그인인지 카카오 로그인인지 구분.
    private int flag;
    private String treatmentDate;
    private String reTreatmentDate;
    private String title;
    private String description;

    public Chart(String petName, long userId, int flag, String treatmentDate, String reTreatmentDate, String title, String description) {
        this.petName = petName;
        this.userId = userId;
        this.flag = flag;
        this.treatmentDate = treatmentDate;
        this.reTreatmentDate = reTreatmentDate;
        this.title = title;
        this.description = description;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getReTreatmentDate() {
        return reTreatmentDate;
    }

    public void setReTreatmentDate(String reTreatmentDate) {
        this.reTreatmentDate = reTreatmentDate;
    }

    public String getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(String treatmentDate) {
        this.treatmentDate = treatmentDate;
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
}
