package com.example.kwy2868.finalproject.Retrofit.NaverAPI;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by kwy2868 on 2017-08-04.
 */

public interface GeocodingService {
    // 블로그 검색
    @Headers({"X-NAVER-Client-Id: k12iubnrKe8aUjSSeEdZ",
            "X-NAVER-Client-Secret: ZnMdlSPj9w"})
    @GET("map/geocode")
    Call<JsonObject> getLatLng(@Query("query") String address);
}
