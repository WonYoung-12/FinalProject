package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.kwy2868.finalproject.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kwy2868 on 2017-08-17.
 */

public class FavoriteActivity extends AppCompatActivity {
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        unbinder = ButterKnife.bind(this);
        setTitle("Favorite");
    }
}
