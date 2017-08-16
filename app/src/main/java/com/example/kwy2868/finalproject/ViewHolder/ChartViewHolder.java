package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.kwy2868.finalproject.Model.Chart;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class ChartViewHolder extends RecyclerView.ViewHolder {
    //// 바인드 해줄거.

    private List<Chart> chartList;

    public ChartViewHolder(View itemView, List<Chart> chartList) {
        super(itemView);
        this.chartList = chartList;

        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        Chart chart = chartList.get(position);
    }
}
