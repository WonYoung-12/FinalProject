package com.example.kwy2868.finalproject.View;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Adapter.BlackAdapter;
import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.Black;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.TypefaceSpan;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import co.dift.ui.SwipeToAction;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-17.
 */

public class MyBlackActivity extends BaseActivity {
    @BindView(R.id.blackRecyclerView)
    RecyclerView blackRecyclerView;

    private List<Black> blackList;
    private BlackAdapter blackAdapter;
    private LinearLayoutManager layoutManager;
    private static final int COLUMN_SPAN = 2;

    private SwipeToAction swipeToAction;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black);
        unbinder = ButterKnife.bind(this);

        SpannableString title = new SpannableString("BlackList");
        title.setSpan(new TypefaceSpan(this, "NanumBarunpenB.ttf"), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getBlackListFromServer();
    }

    public void getBlackListFromServer(){
        Toasty.Config.getInstance()
                .setInfoColor(ContextCompat.getColor(this, android.R.color.black))
                .apply();

        NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Black>> call = networkService.getBlackList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Black>>() {
            @Override
            public void onResponse(Call<List<Black>> call, Response<List<Black>> response) {
                if(response.isSuccessful()){
                    if(response.body().size() > 0){
                        blackList = response.body();
                        recyclerViewSetting();
                        Toasty.info(MyBlackActivity.this, "등록된 블랙리스트를 삭제할 수 있습니다.", Toast.LENGTH_SHORT, true).show();
                    }
                    else{
                        Toasty.info(MyBlackActivity.this, "등록된 블랙리스트가 없습니다.", Toast.LENGTH_SHORT, true).show();
                    }
                }
                Toasty.Config.reset();
            }

            @Override
            public void onFailure(Call<List<Black>> call, Throwable t) {
                Toasty.error(MyBlackActivity.this, "네트워크 오류입니다", Toast.LENGTH_SHORT, true).show();
                Toasty.Config.reset();
            }
        });
    }

    public void recyclerViewSetting() {
        layoutManager = new LinearLayoutManager(this);
        blackRecyclerView.setLayoutManager(layoutManager);
        blackRecyclerView.setHasFixedSize(true);
        blackRecyclerView.setItemAnimator(null);
        blackAdapter = new BlackAdapter(blackList);
        blackRecyclerView.setAdapter(blackAdapter);

        swipeToAction = new SwipeToAction(blackRecyclerView, new SwipeToAction.SwipeListener<Black>() {
            @Override
            public boolean swipeLeft(Black itemData) {
                showDeleteBlackDialog(itemData);
                return true;
            }

            @Override
            public boolean swipeRight(Black itemData) {
                return true;
            }

            @Override
            public void onClick(Black itemData) {

            }

            @Override
            public void onLongClick(Black itemData) {

            }
        });
    }

    public void showDeleteBlackDialog(final Black black){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블랙리스트 삭제")
                .setMessage("블랙리스트에서 해제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteBlack(black);
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

    public void deleteBlack(final Black black){
        final int position = blackList.indexOf(black);
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.deleteBlack(black.getNum(), black.getUserId(), black.getFlag());
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    if(response.body().getResultCode() == 200){
                        blackList.remove(position);
                        blackAdapter.notifyItemRemoved(position);
                        Toasty.success(MyBlackActivity.this, "정상적으로 삭제 되었습니다.", Toast.LENGTH_SHORT, true).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toasty.error(MyBlackActivity.this, "네트워크 오류입니다", Toast.LENGTH_SHORT, true).show();
            }
        });
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
