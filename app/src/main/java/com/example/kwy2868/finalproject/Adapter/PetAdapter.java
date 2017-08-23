package com.example.kwy2868.finalproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.ViewHolder.PetViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class PetAdapter extends RecyclerView.Adapter<PetViewHolder> {
    private List<Pet> petList;

    public PetAdapter(List<Pet> petList) {
        Log.i("펫 어댑터 생성", "생성");
        this.petList = petList;
    }

    @Override
    public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.cardview_mypet, parent, shouldAttachToParentImmediately);
        PetViewHolder petViewHolder = new PetViewHolder(view, petList);

        return petViewHolder;
    }

    @Override
    public void onBindViewHolder(PetViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }
}
