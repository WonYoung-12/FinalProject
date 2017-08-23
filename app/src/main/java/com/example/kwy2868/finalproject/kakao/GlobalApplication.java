package com.example.kwy2868.finalproject.kakao;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.KakaoSDK;
import com.tsengvn.typekit.Typekit;

/**
 * Created by kwy2868 on 2017-07-25.
 */

public class GlobalApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        GlobalApplication.context = getApplicationContext();
        KakaoSDK.init(new KakaoSDKAdapter());
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumBarunGothicBold.ttf"))
                .addCustom1(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.ttf"))
                .addCustom2(Typekit.createFromAsset(this, "fonts/NanumBarunGothicUltraLight.ttf"));
    }

    public static Context getAppContext() {
        return GlobalApplication.context;
    }

}