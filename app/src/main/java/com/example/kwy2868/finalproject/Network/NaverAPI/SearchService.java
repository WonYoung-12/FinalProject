package com.example.kwy2868.finalproject.Network.NaverAPI;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by kwy2868 on 2017-08-02.
 */

public interface SearchService {
    // 블로그 검색
    @Headers({"X-NAVER-Client-Id: k12iubnrKe8aUjSSeEdZ",
        "X-NAVER-Client-Secret: ZnMdlSPj9w"})
    @GET("search/blog")
    Call<JsonObject> getBlogContentList(@Query("query") String keyword, @Query("display") int display);

    // 카페 글 검색.
    @Headers({"X-NAVER-Client-Id: k12iubnrKe8aUjSSeEdZ",
            "X-NAVER-Client-Secret: ZnMdlSPj9w"})
    @GET("search/cafearticle")
    Call<JsonObject> getCafeArticleList(@Query("query") String keyword, @Query("display") int display);

    // 지식인 검색.
    @Headers({"X-NAVER-Client-Id: k12iubnrKe8aUjSSeEdZ",
            "X-NAVER-Client-Secret: ZnMdlSPj9w"})
    @GET("search/kin")
    Call<JsonObject> getKinArticleList(@Query("query") String keyword, @Query("display") int display);
}
