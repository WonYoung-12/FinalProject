package com.example.kwy2868.finalproject.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.ViewHolder.PetViewHolder;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class PetAdapter extends RecyclerView.Adapter<PetViewHolder> {
    private List<Pet> petList;

    public PetAdapter(List<Pet> petList) {
        this.petList = petList;
    }

    @Override
    public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
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
