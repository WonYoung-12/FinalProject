package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.BlogContent;
import com.example.kwy2868.finalproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class BlogContentViewHolder extends RecyclerView.ViewHolder {
    // ButterKnife로 바인딩 해주자.
    @BindView(R.id.contentTitle)
    TextView contentTitle;
    @BindView(R.id.contentLink)
    TextView contentLink;
    @BindView(R.id.contentDescription)
    TextView contentDescription;
    @BindView(R.id.bloggerName)
    TextView bloggerName;
    @BindView(R.id.bloggerLink)
    TextView bloggerLink;
    @BindView(R.id.postDate)
    TextView postDate;

    private List<BlogContent> blogContentList;

    public BlogContentViewHolder(View itemView, List<BlogContent> blogContentList) {
        super(itemView);
        this.blogContentList = blogContentList;
        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        BlogContent blogContent = blogContentList.get(position);
        contentTitle.setText(blogContent.getTitle());
        contentLink.setText(blogContent.getLink());
        contentDescription.setText(blogContent.getDescription());
        bloggerName.setText(blogContent.getBloggername());
        bloggerLink.setText(blogContent.getBloggerlink());
        postDate.setText(blogContent.getPostdate());
    }
}
