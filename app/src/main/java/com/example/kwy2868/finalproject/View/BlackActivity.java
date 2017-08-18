package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Adapter.BlackAdapter;
import com.example.kwy2868.finalproject.Model.Black;
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

public class BlackActivity extends AppCompatActivity {
    @BindView(R.id.blackRecyclerView)
    RecyclerView blackRecyclerView;

    private List<Black> blackList;
    private BlackAdapter blackAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private static final int COLUMN_SPAN = 2;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black);
        unbinder = ButterKnife.bind(this);
        setTitle("BlackList");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getBlackListFromServer();
    }

    public void getBlackListFromServer(){
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Black>> call = networkService.getBlackList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Black>>() {
            @Override
            public void onResponse(Call<List<Black>> call, Response<List<Black>> response) {
                if(response.isSuccessful()){
                    if(response.body().size() > 0){
                        blackList = response.body();
                        recyclerViewSetting();
                    }
                    else{
                        Toast.makeText(BlackActivity.this, "등록한 블랙리스트가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Black>> call, Throwable t) {

            }
        });
    }

    public void recyclerViewSetting() {
        layoutManager = new StaggeredGridLayoutManager(COLUMN_SPAN, StaggeredGridLayoutManager.VERTICAL);
        blackRecyclerView.setLayoutManager(layoutManager);
        blackRecyclerView.setHasFixedSize(true);
        blackRecyclerView.setItemAnimator(null);
        blackAdapter = new BlackAdapter(blackList);
        blackRecyclerView.setAdapter(blackAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
