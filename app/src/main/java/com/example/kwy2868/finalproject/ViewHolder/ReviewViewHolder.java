package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kwy2868.finalproject.Model.GetReviewResult;
import com.example.kwy2868.finalproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by kwy2868 on 2017-08-09.
 */

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.userImage)
    ImageView userImage;
    @BindView(R.id.userNickname)
    TextView userNickname;
    @BindView(R.id.reviewTitle)
    TextView reviewTitle;
    @BindView(R.id.reviewCost)
    TextView reviewCost;
    @BindView(R.id.reviewContent)
    TextView reviewContent;

    private List<GetReviewResult> reviewList;

    public ReviewViewHolder(View itemView, List<GetReviewResult> reviewList) {
        super(itemView);
        this.reviewList = reviewList;

        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        // 여기서 유저 이미지를 서버에서 받아와야 할듯. 받아 왔으니 글라이드로 해주자.
        if(reviewList.get(position).getThumbnailImagePath() == null || reviewList.get(position).getThumbnailImagePath().trim().equals("")){
            Glide.with(itemView.getContext()).load(R.drawable.img_noprofile)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                    .into(userImage);
        }
        else{
            Glide.with(itemView.getContext()).load(reviewList.get(position).getThumbnailImagePath())
                    .centerCrop().bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                    .into(userImage);
        }
        userNickname.setText(reviewList.get(position).getNickname());
        reviewTitle.setText(reviewList.get(position).getTitle());
        reviewCost.setText(reviewList.get(position).getCost() + "원");
        reviewContent.setText(reviewList.get(position).getContent());
    }
}
