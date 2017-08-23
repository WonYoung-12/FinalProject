package com.example.kwy2868.finalproject.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.example.kwy2868.finalproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-23.
 */

public class SplashActivity extends Activity {
    @BindView(R.id.splashImage)
    ImageView splashImage;

    private int SPLASH_TIME = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.main)
                .centerCrop().bitmapTransform(new FitCenter(this))
                .into(splashImage);

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
