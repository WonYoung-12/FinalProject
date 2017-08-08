package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.KiNContent;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.KiNContentViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class KiNContentAdapter extends RecyclerView.Adapter<KiNContentViewHolder>{
    private List<KiNContent> kiNContentList;

    public KiNContentAdapter(List<KiNContent> kiNContentList) {
        this.kiNContentList = kiNContentList;
    }

    @Override
    public KiNContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_kincontent, parent, shouldAttachToParentImmediately);
        KiNContentViewHolder kiNContentViewHolder = new KiNContentViewHolder(view, kiNContentList);

        return kiNContentViewHolder;
    }

    @Override
    public void onBindViewHolder(KiNContentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return kiNContentList.size();
    }
}
