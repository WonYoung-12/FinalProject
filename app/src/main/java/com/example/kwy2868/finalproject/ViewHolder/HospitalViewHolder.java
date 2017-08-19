package com.example.kwy2868.finalproject.ViewHolder;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.Model.SpeciesResult;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.View.HospitalDetailActivity;
import com.example.kwy2868.finalproject.kakao.GlobalApplication;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class HospitalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.hospitalCardView)
    CardView hospitalCardView;
    @BindView(R.id.hospitalName)
    TextView hospitalName;
    @BindView(R.id.hospitalImage)
    ImageView hospitalImage;
    @BindView(R.id.hospitalAddress)
    TextView hospitalAddress;
    @BindView(R.id.hospitalTel)
    TextView hospitalTel;
    @BindView(R.id.hospitalSpecies)
    TextView hospitalSpecies;
    @BindView(R.id.hospitalDistance)
    TextView hospitalDistance;
    @BindView(R.id.hospitalRating)
    RatingBar hospitalRating;

    private static final String HOSPITAL_TAG = "Hospital";
    private static final String LOCATION_TAG = "Location";

    private List<Hospital> hospitalList;

    public HospitalViewHolder(View itemView, List<Hospital> hospitalList) {
        super(itemView);
        this.hospitalList = hospitalList;
        itemView.setOnClickListener(this);

        ButterKnife.bind(this, itemView);
    }

    public void bind(int position) {
        Hospital hospital = hospitalList.get(position);
        if (hospital.getImgPath() == null || hospital.getImgPath().trim().equals("")) {
            Glide.with(GlobalApplication.getAppContext())
                    .load(R.drawable.imgready)
                    .centerCrop().bitmapTransform(new FitCenter(GlobalApplication.getAppContext()))
                    .into(hospitalImage);

        } else {
            Glide.with(GlobalApplication.getAppContext())
                    .load(hospital.getImgPath())
                    .centerCrop().bitmapTransform(new FitCenter(GlobalApplication.getAppContext()))
                    .into(hospitalImage);
        }

        // TODO 우선은 테스트 위해 한명이라도 추가하면 배경 색 바꿔주자.
        if (hospital.getBlackcount() >= 1)
            hospitalCardView.setBackgroundColor(GlobalApplication.getAppContext().getColor(R.color.blackColor));
        hospitalName.setText(hospital.getName());
        hospitalAddress.setText(hospital.getAddress());
        hospitalTel.setText(hospital.getTel());

        List<String> department = hospital.getSpecies();
        if (department == null) {
            hospitalSpecies.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < department.size(); i++) {
                if (i == 0)
                    hospitalSpecies.setText(department.get(i));
                else {
                    hospitalSpecies.append(", " + department.get(i));
                }
            }
        }

        if (hospital.getDistanceFromCurrentLocation() == 0.0) {
            hospitalDistance.setVisibility(View.GONE);
        } else {
            hospitalDistance.setText(hospital.getDistanceFromCurrentLocation() + "");
        }
        hospitalRating.setRating(hospital.getRating_avg());
    }

//    @OnClick(R.id.checkbox)
//    public void checkBoxClick() {
//        Log.d("체크박스", "체크박스 설정했다가 풀었다가");
//    }

    @Override
    public void onClick(View view) {
        // 클릭한 아이템의 포지션 값 받아오자.
        int position = getAdapterPosition();
        Hospital hospital = hospitalList.get(position);
        Parcelable wrappedHospital = Parcels.wrap(hospital);
//        Parcelable wrappedUser = Parcels.wrap(GlobalData.getUser());

        Intent intent = new Intent(view.getContext(), HospitalDetailActivity.class);
        intent.putExtra(HOSPITAL_TAG, wrappedHospital);
        intent.putExtra(LOCATION_TAG, GlobalData.getCurrentLocation());
        view.getContext().startActivity(intent);
    }

    public void hideDistance() {
        if (hospitalDistance != null)
            hospitalDistance.setVisibility(View.GONE);
    }
}
