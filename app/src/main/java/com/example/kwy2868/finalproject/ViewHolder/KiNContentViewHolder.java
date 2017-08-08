package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.KiNContent;
import com.example.kwy2868.finalproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-03.
 */

public class KiNContentViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.contentTitle)
    TextView contentTitle;
    @BindView(R.id.contentLink)
    TextView contentLink;
    @BindView(R.id.contentDescription)
    TextView contentDescription;

    private List<KiNContent> kiNContentList;

    public KiNContentViewHolder(View itemView, List<KiNContent> kiNContentList) {
        super(itemView);
        this.kiNContentList = kiNContentList;
        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        KiNContent kiNContent = kiNContentList.get(position);
        contentTitle.setText(kiNContent.getTitle());
        contentLink.setText(kiNContent.getLink());
        contentDescription.setText(kiNContent.getDescription());
    }
}
