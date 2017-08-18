package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.Favorite;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.FavoriteViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-18.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {
    private List<Favorite> favoriteList;

    public FavoriteAdapter(List<Favorite> favoriteList) {
        this.favoriteList = favoriteList;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_favorite, parent, shouldAttachToParentImmediately);
        FavoriteViewHolder favoriteViewHolder = new FavoriteViewHolder(view, favoriteList);

        return favoriteViewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }
}
