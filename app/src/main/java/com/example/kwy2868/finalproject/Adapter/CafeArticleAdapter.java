package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.CafeArticle;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.CafeArticleViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class CafeArticleAdapter extends RecyclerView.Adapter<CafeArticleViewHolder>{
    private List<CafeArticle> cafeArticleList;

    public CafeArticleAdapter(List<CafeArticle> cafeArticleList) {
        this.cafeArticleList = cafeArticleList;
    }

    @Override
    public CafeArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_cafearticle, parent, shouldAttachToParentImmediately);
        CafeArticleViewHolder cafeArticleViewHolder = new CafeArticleViewHolder(view, cafeArticleList);

        return cafeArticleViewHolder;
    }

    @Override
    public void onBindViewHolder(CafeArticleViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cafeArticleList.size();
    }
}
