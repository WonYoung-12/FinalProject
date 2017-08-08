package com.example.kwy2868.finalproject.Model;

import org.parceler.Parcel;

/**
 * Created by kwy2868 on 2017-08-02.
 */
@Parcel(Parcel.Serialization.BEAN)
public class CafeArticle extends BaseModel{
    // 카페명.
    private String cafename;
    // 카페주소.
    private String cafeurl;

    public String getCafename() {
        return cafename;
    }

    public String getCafeurl() {
        return cafeurl;
    }

    public void setCafename(String cafename) {
        this.cafename = cafename;
    }

    public void setCafeurl(String cafeurl) {
        this.cafeurl = cafeurl;
    }
}
