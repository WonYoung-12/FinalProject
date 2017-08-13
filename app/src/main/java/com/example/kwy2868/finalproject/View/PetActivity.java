package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-12.
 */

public class PetActivity extends AppCompatActivity {
    @BindView(R.id.inputPetName)
    EditText inputPetName;
    @BindView(R.id.inputPetAge)
    EditText inputPetAge;
    @BindView(R.id.inputPetSpecies)
    EditText inputPetSpecies;
    @BindView(R.id.enrollPetButton)
    Button enrollPetButton;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        unbinder = ButterKnife.bind(this);
        setTitle("My Pet 등록");
    }

    @OnClick(R.id.enrollPetButton)
    public void enrollPet(){
        String name = inputPetName.getText().toString();
        int age = Integer.parseInt(inputPetAge.getText().toString());
        String species = inputPetSpecies.getText().toString();
        long userId = GlobalData.getUser().getUserId();

        Pet pet = new Pet(name, age, species, userId);
        GlobalData.getPetDBHelper().addPet(pet);
        Toast.makeText(this, "펫 등록 성공", Toast.LENGTH_SHORT).show();

        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.enrollPet(pet);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    if(response.body().getResultCode() == 200)
                        Toast.makeText(PetActivity.this, "펫 등록 성공!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
