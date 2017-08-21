package com.example.kwy2868.finalproject.Network;

import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.Black;
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.Favorite;
import com.example.kwy2868.finalproject.Model.GetReviewResult;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.Model.LoginResult;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.Model.Review;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.Model.WriteResult;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    // 병원 번호가 num인 리뷰를 가져온다.
    @GET("review")
    Call<List<GetReviewResult>> getReviewList(@Query("num") int num);

    // 유저 아이디와 병원 번호를 가지고 즐겨찾기를 등록한다.
    @POST("enrollFavorite")
    Call<BaseResult> enrollFavorite(@Body Favorite favorite);

    @GET("getFavoriteList")
    Call<List<Favorite>> getFavoriteList(@Query("userId") long userId, @Query("flag") int flag);

    // 유저가 선택한 병원을 블랙리스트에 추가한다.
    @POST("enrollBlack")
    Call<BaseResult> enrollBlack(@Body Black black);

    @GET("getBlackList")
    Call<List<Black>> getBlackList(@Query("userId") long userId, @Query("flag") int flag);

    @POST("writeChart")
    Call<BaseResult> writeChart(@Body Chart chart);

    @POST("enrollPet")
    Call<BaseResult> enrollPet(@Body Pet pet);

    // 사용자의 알람 세팅이 변경되게 해주자.
    @POST("changeAlarmSetting")
    Call<BaseResult> changeAlarmSetting(@Body UserInfo userInfo);

    @GET("getChartList")
    Call<List<Chart>> getChartList(@Query("userId") long userId, @Query("flag") int flag);

    @Multipart
    @POST("addPetImage")
    Call<ResponseBody> addPetImage(@Part MultipartBody.Part image, @Part("json") RequestBody json);

    @GET("ratingHospital")
    Call<BaseResult> ratingHospital(@Query("num") int num, @Query("rating") float rating);

    @GET("getPetList")
    Call<List<Pet>> getPetList(@Query("userId") long userId, @Query("flag") int flag);

    // 펫의 사진들 가져오자.
    @GET("getPetImage")
    Call<ResponseBody> getPetImage(@Query("filePath") String filePath);

    // 병원 번호, 유저 아이디, 유저 플래그 보내주자.
    @GET("checkAddedFavorite")
    Call<BaseResult> checkAddedFavorite(@Query("num") int num, @Query("userId") long userId, @Query("flag") int flag);

    @GET("checkAddedBlack")
    Call<BaseResult> checkAddedBlack(@Query("num") int num, @Query("userId") long userId, @Query("flag") int flag);

    @GET("deleteFavorite")
    Call<BaseResult> deleteFavorite(@Query("num") int num, @Query("userId") long userId, @Query("flag") int flag);

    @GET("deleteBlack")
    Call<BaseResult> deleteBlack(@Query("num") int num, @Query("userId") long userId, @Query("flag") int flag);
}
