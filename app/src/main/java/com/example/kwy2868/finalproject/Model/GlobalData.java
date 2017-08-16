package com.example.kwy2868.finalproject.Model;

import android.content.Context;
import android.location.Location;

import com.example.kwy2868.finalproject.Util.ChartDBHelper;
import com.example.kwy2868.finalproject.Util.PetDBHelper;
import com.example.kwy2868.finalproject.kakao.GlobalApplication;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-11.
 */

public class GlobalData {
    private static Context context;
    private static UserInfo user;
    private static Location currentLocation;
    private static ChartDBHelper chartDBHelper = new ChartDBHelper(GlobalApplication.getAppContext(), null, null, 1);
    private static PetDBHelper petDBHelper = new PetDBHelper(GlobalApplication.getAppContext(), null, null, 1);
    private static List<Chart> chartList;
    private static List<Pet> petList;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        GlobalData.context = context;
    }

    public static UserInfo getUser() {
        return user;
    }

    public static void setUser(UserInfo user) {
        GlobalData.user = user;
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static void setCurrentLocation(Location currentLocation) {
        GlobalData.currentLocation = currentLocation;
    }

    public static ChartDBHelper getChartDBHelper() {
        return chartDBHelper;
    }

    public static PetDBHelper getPetDBHelper() {
        return petDBHelper;
    }

    public static List<Chart> getChartList() {
        return chartList;
    }

    public static void setChartList(List<Chart> chartList) {
        GlobalData.chartList = chartList;
    }

    public static List<Pet> getPetList() {
        return petList;
    }

    public static void setPetList(List<Pet> petList) {
        GlobalData.petList = petList;
    }
}
