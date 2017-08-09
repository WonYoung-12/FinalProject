package com.example.kwy2868.finalproject.Retrofit;

import com.example.kwy2868.finalproject.Model.GetReviewResult;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.Model.LoginResult;
import com.example.kwy2868.finalproject.Model.Review;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.Model.WriteResult;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by kwy2868 on 2017-07-28.
 */
public interface NetworkService {
    @POST("login")
    Call<LoginResult> login(@Body UserInfo userInfo);

    // 지역별로 볼 때 인자를 바꿔주자.
    @GET("hospital")
    Call<List<Hospital>> getHospitalList(@Query("district") String district);

    // 병원 전체 가져온다.
    @GET("everyHospital")
    Call<List<Hospital>> getAllHospital();

    @GET("enrollLatLng")
    Call<JsonObject> enrollLatLng(@Query("num") int num, @Query("latitude") double latitude, @Query("longitude") double longitude);

    // 리뷰를 쓴다.
    @POST("writeReview")
    Call<WriteResult> writeReview(@Body Review review);

    @GET("review")
    Call<List<GetReviewResult>> getReviewList(@Query("num") int num);
}
