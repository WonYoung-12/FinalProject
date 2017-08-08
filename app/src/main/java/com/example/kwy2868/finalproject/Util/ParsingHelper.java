package com.example.kwy2868.finalproject.Util;

import android.util.Log;

import com.example.kwy2868.finalproject.Model.BlogContent;
import com.example.kwy2868.finalproject.Model.CafeArticle;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.Model.KiNContent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwy2868 on 2017-08-02.
 */

public class ParsingHelper {
    private static final int BLOG = 0;
    private static final int CAFE = 1;
    private static final int KIN = 2;

    private static Gson gson = new Gson();

    public static ArrayList searchParsing(JsonObject jsonObject, int flag) {
        ArrayList list;
        if (jsonObject.has("items")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("items");
            if (jsonArray.size() > 0) {
                String jsonString = jsonArray.toString();
                switch (flag) {
                    case BLOG:
                        list = gson.fromJson(jsonString, new TypeToken<ArrayList<BlogContent>>() {
                        }.getType());
                        ConvertHelper.convert(list);
                        return list;
                    case CAFE:
                        list = gson.fromJson(jsonString, new TypeToken<ArrayList<CafeArticle>>() {
                        }.getType());
                        ConvertHelper.convert(list);
                        return list;
                    case KIN:
                        list = gson.fromJson(jsonString, new TypeToken<ArrayList<KiNContent>>() {
                        }.getType());
                        ConvertHelper.convert(list);
                        return list;
                    default:
                        return null;
                }
            }
        }
        return null;
    }

    public static void geocodingParsing(Hospital hospital, JsonObject jsonObject) {
        if (jsonObject.has("result")) {
            Log.d("result", "result 갖고 있다.");
            JsonObject result = jsonObject.getAsJsonObject("result");
            if(result.has("items")){
                Log.d("items", "items 갖고 있다.");
                JsonArray items = result.getAsJsonArray("items");
                // 첫 번째거 사용.
                JsonElement item = items.get(0);
                if(item.getAsJsonObject().has("point")){
                    Log.d("point", "point 갖고 있다.");
                    JsonObject point = item.getAsJsonObject().getAsJsonObject("point");
                    Double longitude = point.get("x").getAsDouble();
                    Double latitude = point.get("y").getAsDouble();
                    Log.d("Longitude", "Longitude : " + longitude);
                    Log.d("Latitude", "Latitude : " + latitude);
                    if(hospital.getNum()==1181){
                        Log.d("마지막", "이거 찍히면 버튼 누르자");
                    }
                    hospital.setLongitude(longitude);
                    hospital.setLatitude(latitude);
                }
            }
        }
    }
}
