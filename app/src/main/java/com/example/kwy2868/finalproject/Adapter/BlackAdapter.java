package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.Black;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.BlackViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-18.
 */

public class BlackAdapter extends RecyclerView.Adapter<BlackViewHolder> {
    private List<Black> blackList;

    public BlackAdapter(List<Black> blackList) {
        this.blackList = blackList;
    }

    @Override
    public BlackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_favorite, parent, shouldAttachToParentImmediately);
        BlackViewHolder blackViewHolder = new BlackViewHolder(view, blackList);

        return blackViewHolder;
    }

    @Override
    public void onBindViewHolder(BlackViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return blackList.size();
    }
}
