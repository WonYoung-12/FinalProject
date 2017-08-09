package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.GetReviewResult;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.ReviewViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-09.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private List<GetReviewResult> reviewList;

    public ReviewAdapter(List<GetReviewResult> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_review, parent, shouldAttachToParentImmediately);
        ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view, reviewList);

        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
