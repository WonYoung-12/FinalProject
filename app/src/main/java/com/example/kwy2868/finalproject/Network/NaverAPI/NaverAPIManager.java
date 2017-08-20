package com.example.kwy2868.finalproject.Network.NaverAPI;

import com.example.kwy2868.finalproject.Network.BaseManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kwy2868 on 2017-08-02.
 */

public class NaverAPIManager extends BaseManager {
    private static SearchService searchService;
    private static GeocodingService geocodingService;

    static {
        BASE_URL = "https://openapi.naver.com/v1/";
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        searchService = retrofit.create(SearchService.class);
        geocodingService = retrofit.create(GeocodingService.class);
    }

    public static SearchService getSearchService() {
        return searchService;
    }

    public static GeocodingService getGeocodingService(){
        return geocodingService;
    }
}
