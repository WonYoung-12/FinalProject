package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class ChartViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.petName)
    TextView petName;
    @BindView(R.id.treatmentDate)
    TextView treatmentDate;
    @BindView(R.id.reTreatmentDate)
    TextView reTreatementDate;
    @BindView(R.id.chartTitle)
    TextView chartTitle;
    @BindView(R.id.chartDescription)
    TextView chartDescription;

    private List<Chart> chartList;

    public ChartViewHolder(View itemView, List<Chart> chartList) {
        super(itemView);
        this.chartList = chartList;

        ButterKnife.bind(this, itemView);
    }

    public void bind(int position){
        Chart chart = chartList.get(position);
        petName.setText("반려 동물 : " + chart.getPetName());
        treatmentDate.setText("진료(접종) 날짜 : " + chart.getTreatmentDate());
        reTreatementDate.setText("재진료(접종) 날짜 : " + chart.getTreatmentDate());
        chartTitle.setText("차트 제목 : " + chart.getTitle());
        chartDescription.setText("진료(접종) 내용" + System.getProperty("line.separator") + System.getProperty("line.separator") + chart.getDescription());
    }
}
