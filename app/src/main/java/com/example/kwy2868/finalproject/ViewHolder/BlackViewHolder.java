package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.example.kwy2868.finalproject.Model.Black;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.kakao.GlobalApplication;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-18.
 */

public class BlackViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.hospitalName)
    TextView hospitalName;
    @BindView(R.id.hospitalImage)
    ImageView hospitalImage;
    @BindView(R.id.hospitalAddress)
    TextView hospitalAddress;
    @BindView(R.id.hospitalTel)
    TextView hospitalTel;
    @BindView(R.id.hospitalRating)
    RatingBar hospitalRating;

    private List<Black> blackList;

    public BlackViewHolder(View itemView, List<Black> blackList) {
        super(itemView);
        this.blackList = blackList;
        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        Black black = blackList.get(position);
        hospitalName.setText(black.getName());
        if (black.getImgPath() == null || black.getImgPath().trim().equals("")) {
            Glide.with(GlobalApplication.getAppContext())
                    .load(R.drawable.imgready)
                    .centerCrop().bitmapTransform(new FitCenter(GlobalApplication.getAppContext()))
                    .into(hospitalImage);

        } else {
            Glide.with(GlobalApplication.getAppContext())
                    .load(black.getImgPath())
                    .centerCrop().bitmapTransform(new FitCenter(GlobalApplication.getAppContext()))
                    .into(hospitalImage);
        }
        hospitalAddress.setText(black.getAddress());
        hospitalTel.setText(black.getTel());
        hospitalRating.setRating(black.getRating_avg());
    }
}
