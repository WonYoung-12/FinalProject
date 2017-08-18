package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by kwy2868 on 2017-08-12.
 */
@Parcel(Parcel.Serialization.BEAN)
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

    @ParcelConstructor
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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
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

    public String getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(String treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    public String getReTreatmentDate() {
        return reTreatmentDate;
    }

    public void setReTreatmentDate(String reTreatmentDate) {
        this.reTreatmentDate = reTreatmentDate;
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
