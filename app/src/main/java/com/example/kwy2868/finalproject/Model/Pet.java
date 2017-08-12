package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-12.
 */

public class Pet {
    // 이름.
    private String name;
    // 나이.
    private int age;
    // 종.
    private String species;
    // 주인 유저 아이디.
    private long userId;

    public Pet(String name, int age, String species, long userId) {
        this.name = name;
        this.age = age;
        this.species = species;
        this.userId = userId;
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
}
