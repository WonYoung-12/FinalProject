package com.example.kwy2868.finalproject.ViewHolder;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.View.AddPetActivity;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kwy2868 on 2017-08-16.
 */

public class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @BindView(R.id.petImage)
    CircleImageView petImage;
    @BindView(R.id.petName)
    TextView petName;
    @BindView(R.id.petAge)
    TextView petAge;
    @BindView(R.id.petSpecies)
    TextView petSpecies;

    // 바인드해주자.
    private List<Pet> petList;

    private static final String PET_TAG = "PET";

    public PetViewHolder(View itemView, List<Pet> petList) {
        super(itemView);
        this.petList = petList;
        itemView.setOnClickListener(this);

        ButterKnife.bind(this, itemView);
        // 아이템 개수가 3 이상이면 2개까지만 보여주자 우선은.
    }

    public void bind(int position){
        Pet pet = petList.get(position);
        // 서버에서 받아온 이미지가 있으면.
        if(pet.getImagePath() == null || pet.getImagePath().trim().equals("")){
            Glide.with(itemView.getContext())
                    .load(R.drawable.noimage)
                    .centerCrop()
                    .bitmapTransform(new CenterCrop(itemView.getContext()))
                    .into(petImage);
        }
        else{
            Glide.with(itemView.getContext())
                    .load(pet.getImagePath())
                    .centerCrop()
                    .bitmapTransform(new CenterCrop(itemView.getContext()))
                    .into(petImage);
        }
        petName.setText("이름 : " + pet.getName());
        petAge.setText("나이 : " + pet.getAge() + "살");
        petSpecies.setText("종 : " + pet.getSpecies());

    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        Pet pet = petList.get(position);

        Parcelable wrappedPet = Parcels.wrap(pet);

        Intent intent = new Intent(view.getContext(), AddPetActivity.class);
        intent.putExtra(PET_TAG, wrappedPet);
        view.getContext().startActivity(intent);
    }
}
