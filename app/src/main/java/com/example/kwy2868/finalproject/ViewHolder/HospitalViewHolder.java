package com.example.kwy2868.finalproject.ViewHolder;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.View.HospitalDetailActivity;
import com.example.kwy2868.finalproject.View.MainActivity;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class HospitalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
    @BindView(R.id.checkbox)
    CheckBox checkBox;

    private static final String HOSPITAL_TAG = "Hospital";
    private static final String USER_TAG = "User";

    private List<Hospital> hospitalList;

    public HospitalViewHolder(View itemView, List<Hospital> hospitalList) {
        super(itemView);
        this.hospitalList = hospitalList;
        itemView.setOnClickListener(this);

        ButterKnife.bind(this, itemView);
    }

    public void bind(int position) {
        Hospital hospital = hospitalList.get(position);
        hospitalName.setText(hospital.getName());
        hospitalAddress.setText(hospital.getAddress());
        hospitalTel.setText(hospital.getTel());
        // TODO 이 방식 야매인것 같다.. 나중에 고쳐보자
        if(hospital.getDistanceFromCurrentLocation() == 0.0){
            distance.setVisibility(View.INVISIBLE);
        }
        else{
            distance.setText(hospital.getDistanceFromCurrentLocation() + "");
        }
    }

    @OnClick(R.id.checkbox)
    public void checkBoxClick() {
        Log.d("체크박스", "체크박스 설정했다가 풀었다가");
    }

    @Override
    public void onClick(View view) {
        // 클릭한 아이템의 포지션 값 받아오자.
        int position = getAdapterPosition();
        Hospital hospital = hospitalList.get(position);
        Parcelable wrappedHospital = Parcels.wrap(hospital);
        // TODO 야매 아니게 하는 법은 없을까..
        Parcelable wrappedUser = Parcels.wrap(MainActivity.getUser());
        Log.d("어플리케이션 유저", MainActivity.getUser() + " ");
        Intent intent = new Intent(view.getContext(), HospitalDetailActivity.class);
        intent.putExtra(HOSPITAL_TAG, wrappedHospital);
        intent.putExtra(USER_TAG, wrappedUser);
        view.getContext().startActivity(intent);
    }
}
