package com.example.kwy2868.finalproject.Retrofit;

import com.example.kwy2868.finalproject.Model.Hospital;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kwy2868 on 2017-07-28.
 */
public interface NetworkService {
    // 지역별로 볼 때 인자를 바꿔주자.
    @GET("hospital")
    Call<List<Hospital>> getHospitalList(@Query("district") String district);

    // 병원 전체 가져온다.
    @GET("everyhospital")
    Call<List<Hospital>> getAllHospital();

    @GET("enrollLatLng")
    Call<JsonObject> enrollLatLng(@Query("num") int num, @Query("latitude") double latitude, @Query("longitude") double longitude);
}
