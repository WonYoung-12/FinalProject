package com.example.kwy2868.finalproject.Util;

import android.util.Log;

import com.example.kwy2868.finalproject.Model.Hospital;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-05.
 */

public class GeocodingListener implements Callback<JsonObject> {
    private Hospital hospital;

    public GeocodingListener(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        if(response.isSuccessful()){
            Log.d("지오코딩 되는걸까", "된다");
            JsonObject jsonObject = response.body();
            ParsingHelper.geocodingParsing(hospital, jsonObject);
        }
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {

    }
}
