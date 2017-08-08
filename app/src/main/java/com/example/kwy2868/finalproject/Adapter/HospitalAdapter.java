package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.HospitalViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class HospitalAdapter extends RecyclerView.Adapter<HospitalViewHolder> {
    private List hospitalList;

    public HospitalAdapter(List hospitalList) {
        this.hospitalList = hospitalList;
    }

    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_hospital, parent, shouldAttachToParentImmediately);
        HospitalViewHolder hospitalViewHolder = new HospitalViewHolder(view, hospitalList);

        return hospitalViewHolder;
    }

    @Override
    public void onBindViewHolder(HospitalViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(hospitalList != null)
            return hospitalList.size();
        else
            return -1;
    }

    public void setHospitalList(List hospitalList) {
        this.hospitalList = hospitalList;
    }
}
