package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.example.kwy2868.finalproject.Model.Favorite;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.kakao.GlobalApplication;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-18.
 */

public class FavoriteViewHolder extends RecyclerView.ViewHolder {
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

    private List<Favorite> favoriteList;

    public FavoriteViewHolder(View itemView, List<Favorite> favoriteList) {
        super(itemView);
        this.favoriteList = favoriteList;
        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        Favorite favorite = favoriteList.get(position);
        hospitalName.setText(favorite.getName());
        if (favorite.getImgPath() == null || favorite.getImgPath().trim().equals("")) {
            Glide.with(GlobalApplication.getAppContext())
                    .load(R.drawable.imgready)
                    .centerCrop().bitmapTransform(new FitCenter(GlobalApplication.getAppContext()))
                    .into(hospitalImage);

        } else {
            Glide.with(GlobalApplication.getAppContext())
                    .load(favorite.getImgPath())
                    .centerCrop().bitmapTransform(new FitCenter(GlobalApplication.getAppContext()))
                    .into(hospitalImage);
        }
        hospitalAddress.setText(favorite.getAddress());
        hospitalTel.setText(favorite.getTel());
        hospitalRating.setRating(favorite.getRating_avg());
    }
}
