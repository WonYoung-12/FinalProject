package com.example.kwy2868.finalproject.kakao;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.KakaoSDK;

/**
 * Created by kwy2868 on 2017-07-25.
 */

public class GlobalApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        GlobalApplication.context = getApplicationContext();
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static Context getAppContext() {
        return GlobalApplication.context;
    }

}