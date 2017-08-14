package com.example.kwy2868.finalproject.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class BaseManager {
    protected static String BASE_URL;
    protected static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    protected static OkHttpClient client =
            new OkHttpClient.Builder().addInterceptor(getHttpLoggingInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).build();
    protected static Retrofit retrofit;

    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }
}
