package com.example.kwy2868.finalproject.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.example.kwy2868.finalproject.R;

/**
 * Created by kwy2868 on 2017-08-23.
 */

public class SplashActivity extends Activity {
    private int SPLASH_TIME = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                overridePendingTransition(0,android.R.anim.fade_in);
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        },SPLASH_TIME);
    }
}
