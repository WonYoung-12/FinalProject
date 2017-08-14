package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.KiNContent;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.SearchResultClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class KiNContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @BindView(R.id.contentTitle)
    TextView contentTitle;
    @BindView(R.id.contentLink)
    TextView contentLink;
    @BindView(R.id.contentDescription)
    TextView contentDescription;

    private List<KiNContent> kiNContentList;
    private SearchResultClickListener searchResultClickListener;

    public KiNContentViewHolder(View itemView, List<KiNContent> kiNContentList) {
        super(itemView);
        this.kiNContentList = kiNContentList;
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(int position){
        KiNContent kiNContent = kiNContentList.get(position);
        contentTitle.setText(kiNContent.getTitle());
        contentLink.setText(kiNContent.getLink());
        contentDescription.setText(kiNContent.getDescription());
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        searchResultClickListener = new SearchResultClickListener(kiNContentList.get(position));
        searchResultClickListener.itemClick();
    }
}
