package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.CafeArticle;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.SearchResultClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class CafeArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @BindView(R.id.contentTitle)
    TextView contentTitle;
    @BindView(R.id.contentDescription)
    TextView contentDescription;
    @BindView(R.id.cafeName)
    TextView cafeName;

    private List<CafeArticle> cafeArticleList;
    private SearchResultClickListener searchResultClickListener;

    public CafeArticleViewHolder(View itemView, List<CafeArticle> cafeArticleList) {
        super(itemView);
        this.cafeArticleList = cafeArticleList;
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(int position){
        CafeArticle cafeArticle = cafeArticleList.get(position);
        contentTitle.setText(cafeArticle.getTitle());
        contentDescription.setText(cafeArticle.getDescription());
        cafeName.setText("Cafe : " + cafeArticle.getCafename());
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        searchResultClickListener = new SearchResultClickListener(cafeArticleList.get(position));
        searchResultClickListener.itemClick();
    }
}
