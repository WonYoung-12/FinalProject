package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.kwy2868.finalproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class MyChartActivity extends AppCompatActivity{

    @BindView(R.id.myChartRecyclerView)
    RecyclerView myChartRecyclerView;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mychart);
        unbinder = ButterKnife.bind(this);

        setTitle("My Chart");
        recyclerViewSetting();
    }

    public void recyclerViewSetting(){

    }
}