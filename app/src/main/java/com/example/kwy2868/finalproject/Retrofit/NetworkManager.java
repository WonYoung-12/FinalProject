package com.example.kwy2868.finalproject.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kwy2868 on 2017-07-24.
 */

public class NetworkManager extends BaseManager {
    private static final String IPADDRESS = "203.252.166.230";
    private static final String PORT = "3000";
    private static NetworkService networkService;

    static {
        BASE_URL = "http://" + IPADDRESS + ":" + PORT + "/";
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        networkService = retrofit.create(NetworkService.class);
    }

    public static NetworkService getNetworkService() {
        return networkService;
    }
}
