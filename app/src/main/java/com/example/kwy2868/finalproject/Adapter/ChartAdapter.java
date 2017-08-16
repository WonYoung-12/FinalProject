package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.ViewHolder.ChartViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class ChartAdapter extends RecyclerView.Adapter<ChartViewHolder>{
    private List<Chart> chartList;

    public ChartAdapter(List<Chart> chartList) {
        this.chartList = chartList;
    }

    @Override
    public ChartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        return null;
    }

    @Override
    public void onBindViewHolder(ChartViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }
}
