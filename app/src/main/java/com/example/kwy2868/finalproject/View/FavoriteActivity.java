package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Adapter.FavoriteAdapter;
import com.example.kwy2868.finalproject.Model.Favorite;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-17.
 */

public class FavoriteActivity extends AppCompatActivity {
    @BindView(R.id.favoriteRecyclerView)
    RecyclerView favoriteRecyclerView;

    private List<Favorite> favoriteList;
    private FavoriteAdapter favoriteAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private static final int COLUMN_SPAN = 2;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        unbinder = ButterKnife.bind(this);
        setTitle("Favorite");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFavoriteListFromServer();
    }

    public void getFavoriteListFromServer(){
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Favorite>> call = networkService.getFavoriteList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                if(response.isSuccessful()){
                    // 받아온 항목이 있으면 화면에 띄워주자.
                    if (response.body().size() > 0){
                        favoriteList = response.body();
                        recyclerViewSetting();
                    }
                    else{
                        Toast.makeText(FavoriteActivity.this, "등록한 즐겨찾기가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {

            }
        });
    }

    public void recyclerViewSetting() {
        layoutManager = new StaggeredGridLayoutManager(COLUMN_SPAN, StaggeredGridLayoutManager.VERTICAL);
        favoriteRecyclerView.setLayoutManager(layoutManager);
        favoriteRecyclerView.setHasFixedSize(true);
        favoriteRecyclerView.setItemAnimator(null);
        favoriteAdapter = new FavoriteAdapter(favoriteList);
        favoriteRecyclerView.setAdapter(favoriteAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
