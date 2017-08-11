package com.example.kwy2868.finalproject.Model;

import android.location.Location;

/**
 * Created by kwy2868 on 2017-08-11.
 */

public class GlobalData {
    private static UserInfo user;
    private static Location currentLocation;

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
}
