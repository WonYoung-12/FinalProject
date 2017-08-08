package com.example.kwy2868.finalproject.Model;

import android.support.annotation.NonNull;

import org.parceler.Parcel;

/**
 * Created by kwy2868 on 2017-07-28.
 */
@Parcel(Parcel.Serialization.BEAN)
public class Hospital implements Comparable<Hospital>{
    private int num;
    private String name;
    private String address;
    private String tel;

    // 지도 활용을 위한 위도 값. Naver에서는 Y로
    private double latitude;
    // 지도 활용을 위한 경도 값. Naver에서는 X로.
    private double longitude;

    // 현재위치부터 병원까지의 거리.
    private float distanceFromCurrentLocation;

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getDistanceFromCurrentLocation() {
        return distanceFromCurrentLocation;
    }

    // set 하면서 km 단위로 바꾸어주자.
    public void setDistanceFromCurrentLocation(float distanceFromCurrentLocation) {
        this.distanceFromCurrentLocation = distanceFromCurrentLocation / 1000;
    }

    @Override
    public int compareTo(@NonNull Hospital hospital) {
        if(this.getDistanceFromCurrentLocation() > hospital.getDistanceFromCurrentLocation())
            return 1;
        else if(this.getDistanceFromCurrentLocation() < hospital.getDistanceFromCurrentLocation())
            return -1;
        else
            return 0;
    }
}
