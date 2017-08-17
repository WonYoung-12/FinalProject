package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.File;

/**
 * Created by kwy2868 on 2017-08-12.
 */
@Parcel(Parcel.Serialization.BEAN)
public class Pet {
    // 이름.
    private String name;
    // 나이.
    private int age;
    // 종.
    private String species;
    // 주인 유저 아이디.
    private long userId;
    // 안드로이드 로컬에 펫 이미지가 저장된 파일 이름.
    private String imagePath;
    // 주인이 네이버 유저인지 카카오 유저인지 구분을 위한 플래그.
    private int flag;

    // 서버에서 가져온 이미지 파일.
    File imgFile;

    @ParcelConstructor
    public Pet(String name, int age, String species, long userId, int flag) {
        this.name = name;
        this.age = age;
        this.species = species;
        this.userId = userId;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public File getImgFile() {
        return imgFile;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }
}
