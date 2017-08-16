package com.example.kwy2868.finalproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.kwy2868.finalproject.Model.Pet;

import java.util.List;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class PetViewHolder extends RecyclerView.ViewHolder {
    // 바인드해주자.
    private List<Pet> petList;

    public PetViewHolder(View itemView, List<Pet> petList) {
        super(itemView);
        this.petList = petList;
    }

    public void bind(int position){
        Pet pet = petList.get(position);
    }
}
