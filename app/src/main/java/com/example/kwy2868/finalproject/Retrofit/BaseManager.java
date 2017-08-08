package com.example.kwy2868.finalproject.Retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class BaseManager {
    protected static String BASE_URL;
    protected static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    protected static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(getHttpLoggingInterceptor()).build();
    protected static Retrofit retrofit;

    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }
}
