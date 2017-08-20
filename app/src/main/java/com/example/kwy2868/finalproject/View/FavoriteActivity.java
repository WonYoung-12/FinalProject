package com.example.kwy2868.finalproject.View;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Adapter.FavoriteAdapter;
import com.example.kwy2868.finalproject.Model.Favorite;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import co.dift.ui.SwipeToAction;
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
    private LinearLayoutManager layoutManager;
    private static final int COLUMN_SPAN = 2;

    private SwipeToAction swipeToAction;

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
        layoutManager = new LinearLayoutManager(this);
        favoriteRecyclerView.setLayoutManager(layoutManager);
        favoriteRecyclerView.setHasFixedSize(true);
        favoriteRecyclerView.setItemAnimator(null);
        favoriteAdapter = new FavoriteAdapter(favoriteList);
        favoriteRecyclerView.setAdapter(favoriteAdapter);

        swipeToAction = new SwipeToAction(favoriteRecyclerView, new SwipeToAction.SwipeListener<Favorite>() {
            @Override
            public boolean swipeLeft(Favorite itemData) {
                showDeleteFavoriteDialog();
                displaySnackbar(itemData.getName() + " removed", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(Favorite itemData) {
                displaySnackbar(itemData.getName() + " loved", null, null);
                return true;
            }

            @Override
            public void onClick(Favorite itemData) {

            }

            @Override
            public void onLongClick(Favorite itemData) {

            }
        });
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    public void showDeleteFavoriteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("즐겨찾기 삭제")
                .setMessage("즐겨찾기에서 삭제하시겠습니까?")
                .setCancelable(true)
                // 여기서 서버에 보내서 삭제해주자.
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
