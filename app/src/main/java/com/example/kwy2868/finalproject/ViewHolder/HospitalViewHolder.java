package com.example.kwy2868.finalproject.ViewHolder;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.View.HospitalDetailActivity;
import com.example.kwy2868.finalproject.kakao.GlobalApplication;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class HospitalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
    @BindView(R.id.distance)
    TextView distance;
//    @BindView(R.id.checkbox)
//    CheckBox checkBox;

    private static final String HOSPITAL_TAG = "Hospital";
    private static final String USER_TAG = "User";
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
        // TODO 우선은 테스트 위해 한명이라도 추가하면 배경 색 바꿔주자.
        if(hospital.getBlackcount() >= 1)
            hospitalCardView.setBackgroundColor(GlobalApplication.getAppContext().getColor(android.R.color.darker_gray));
        hospitalName.setText(hospital.getName());
        hospitalAddress.setText(hospital.getAddress());
        hospitalTel.setText(hospital.getTel());
        // TODO 이 방식 야매인것 같다.. 나중에 고쳐보자
        if(hospital.getDistanceFromCurrentLocation() == 0.0){
            distance.setVisibility(View.GONE);
        }
        else{
            distance.setText(hospital.getDistanceFromCurrentLocation() + "");
        }
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
//        intent.putExtra(USER_TAG, wrappedUser);
        intent.putExtra(LOCATION_TAG, GlobalData.getCurrentLocation());
        view.getContext().startActivity(intent);
    }
}
