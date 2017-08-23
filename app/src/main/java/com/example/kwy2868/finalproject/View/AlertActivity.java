package com.example.kwy2868.finalproject.View;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;

import es.dmoral.toasty.Toasty;

/**
 * Created by kwy2868 on 2017-08-23.
 */

public class AlertActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String title =  intent.getStringExtra("Title");
        String description = intent.getStringExtra("Description");
        Log.d("액티비티 실행", "실행");
        Toasty.info(this, title, Toast.LENGTH_SHORT, true).show();

        Alerter.create(this)
                .setTitle(title)
                .setText(description)
                .setBackgroundColorInt(Color.CYAN)
                .setDuration(2000)
                .show();
        Log.d("Alerter 보여주자", "보여주자");
        Log.d("Title", title);
        Log.d("Text", description);

        try {
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            finish();
        }
    }
}
