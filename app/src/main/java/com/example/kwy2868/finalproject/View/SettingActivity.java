package com.example.kwy2868.finalproject.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.kakao.auth.Session;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-13.
 */

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.notiSettingButton)
    ToggleButton notiSettingButton;
    @BindView(R.id.logoutButton)
    Button logoutButton;

    private static final int NOTIFICATION_OFF = 0;
    private static final int NOTIFICATION_ON = 1;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        unbinder = ButterKnife.bind(this);
        setTitle("My Setting");
        // 사용자가 알림을 받고 싶은지 안 받고 싶은 상태인지 체크해서 온 오프 해주자.
        initNotiButton();
    }

    public void initNotiButton(){
        Log.d("노티 플래그", GlobalData.getUser().getNotiFlag() + "");
        // 알람을 받지 않는 상태이면.
        if(GlobalData.getUser().getNotiFlag() == 0){
            notiSettingButton.setChecked(false);
        }
        else{
            notiSettingButton.setChecked(true);
        }
    }

    @OnClick(R.id.notiSettingButton)
    public void notiSetting(){
        if(!notiSettingButton.isChecked()){
            GlobalData.getUser().setNotiFlag(NOTIFICATION_OFF);
            Log.d("알람 OFF", GlobalData.getUser().getNotiFlag() + "");
        }
        else{
            GlobalData.getUser().setNotiFlag(NOTIFICATION_ON);
            Log.d("알람 On", GlobalData.getUser().getNotiFlag() + "");
        }
        // 서버에도 써주자.
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.changeAlarmSetting(GlobalData.getUser());
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    if(response.body().getResultCode() == 200){
                        Toast.makeText(SettingActivity.this, "알람 세팅 변경 완료", Toast.LENGTH_SHORT).show();
                        // 바뀌었을 테니 화면에서도 바꿔줘야지!
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.logoutButton)
    public void logout(){
        if(GlobalData.getUser().getFlag() == 0){

        }
        // 사용자가 카카오 로그인한 경우 세션 해제해주고 다시 로그인 액티비티로 가게 해주자.
        else{
            Session session = Session.getCurrentSession();
            // 이거하면 로그아웃 할때마다?
//            session.close();
            // 로그아웃 되었으니 해제해주자.
            GlobalData.setUser(null);

            Intent intent = new Intent(this, LoginActivity.class);
            // 모든 액티비티 스택을 지워준다.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
