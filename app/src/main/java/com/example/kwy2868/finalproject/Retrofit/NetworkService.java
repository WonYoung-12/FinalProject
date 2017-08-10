package com.example.kwy2868.finalproject.Retrofit;

import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.Black;
import com.example.kwy2868.finalproject.Model.Favorite;
import com.example.kwy2868.finalproject.Model.GetReviewResult;
import com.example.kwy2868.finalproject.Model.Hospital;
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
    Call<BaseResult> login(@Body UserInfo userInfo);

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

    // 병원 번호가 num인 리뷰를 가져온다.
    @GET("review")
    Call<List<GetReviewResult>> getReviewList(@Query("num") int num);

    // TODO 그런데 즐겨찾기랑 블랙리스트를 동시에 추가할 수 없게 디테일한 내용까지 지금 잡아줄 여유가 있을까...?

    // 유저 아이디와 병원 번호를 가지고 즐겨찾기를 등록한다.
    @POST("enrollFavorite")
    Call<BaseResult> enrollFavorite(@Body Favorite favorite);

    // TODO 유저 아이디에 해당하는 즐겨찾기를 가져온다.
    @GET("getFavoriteList")
    Call<List<Favorite>> getFavoriteList(@Query("userId") long userId);

    // 유저가 선택한 병원을 블랙리스트에 추가한다.
    @POST("enrollBlackList")
    Call<BaseResult> enrollBlackList(@Body Black black);

    @GET("getBlackList")
    Call<List<Black>> getBlackList(@Query("userId") long userId);
}
