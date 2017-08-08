package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.BlogContent;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.BlogContentViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class BlogContentAdapter extends RecyclerView.Adapter<BlogContentViewHolder> {
    private List<BlogContent> blogContentList;

    public BlogContentAdapter(List<BlogContent> blogContentList) {
        this.blogContentList = blogContentList;
    }

    @Override
    public BlogContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_blogcontent, parent, shouldAttachToParentImmediately);
        BlogContentViewHolder blogContentViewHolder = new BlogContentViewHolder(view, blogContentList);

        return blogContentViewHolder;
    }

    @Override
    public void onBindViewHolder(BlogContentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return blogContentList.size();
    }
}
