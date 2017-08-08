package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.CafeArticle;
import com.example.kwy2868.finalproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class CafeArticleViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.contentTitle)
    TextView contentTitle;
    @BindView(R.id.contentLink)
    TextView contentLink;
    @BindView(R.id.contentDescription)
    TextView contentDescription;
    @BindView(R.id.cafeName)
    TextView cafeName;
    @BindView(R.id.cafeUrl)
    TextView cafeUrl;

    private List<CafeArticle> cafeArticleList;

    public CafeArticleViewHolder(View itemView, List<CafeArticle> cafeArticleList) {
        super(itemView);
        this.cafeArticleList = cafeArticleList;
        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        CafeArticle cafeArticle = cafeArticleList.get(position);
        contentTitle.setText(cafeArticle.getTitle());
        contentLink.setText(cafeArticle.getLink());
        contentDescription.setText(cafeArticle.getDescription());
        cafeName.setText(cafeArticle.getCafename());
        cafeUrl.setText(cafeArticle.getCafeurl());
    }
}
