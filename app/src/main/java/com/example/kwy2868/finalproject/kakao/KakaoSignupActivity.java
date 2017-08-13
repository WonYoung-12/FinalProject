package com.example.kwy2868.finalproject.kakao;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;
import com.example.kwy2868.finalproject.View.LoginActivity;
import com.example.kwy2868.finalproject.View.MainActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-07-26.
 */

public class KakaoSignupActivity extends Activity {
    private static final String USER = "User";

    private static final String LOCATION_TAG = "Current Location";
    private static final int KAKAO_FLAG = 1;

    private Location currentLocation;
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromLoginActivity();
        requestMe();
    }

    public void getDataFromLoginActivity(){
        if(getIntent() != null){
            currentLocation = getIntent().getParcelableExtra(LOCATION_TAG);
        }
    }

    // 유저의 정보를 받아오는 함수.
    // 세션 연결?
    protected void requestMe() {
        List<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "fail to get userInfo. msg = " + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                // 클라문제.
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            // 카카오 회원이 아닐 시.
            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                Logger.d("UserProfile : " + result);
                Log.d("UserProfile", "UserProfile : " + result);
                Log.d("유저 아이디", "아이디 : " + result.getId());
                Log.d("UserImagePath", result.getThumbnailImagePath() + " ");

                // 유저 세팅.
                UserInfo user = new UserInfo(result.getEmail(), result.getId(), result.getNickname(), result.getThumbnailImagePath(), KAKAO_FLAG);
                GlobalData.setUser(user);
                Log.d("유저", "User : " + user.toString());
                loginToServer(user);
            }
        }, propertyKeys, false);
    }

    public void loginToServer(final UserInfo user) {
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.login(user);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200)
                        redirectMainActivity(user);
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toast.makeText(KakaoSignupActivity.this, "네트워크 문제, 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                redirectLoginActivity();
            }
        });
    }

    private void redirectMainActivity(UserInfo user) {
//        Toast.makeText(this, "MainActivity로", Toast.LENGTH_SHORT).show();
        Parcelable wrappedUser = Parcels.wrap(user);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USER, wrappedUser);
//        intent.putExtra(LOCATION_TAG, location);

        startActivity(intent);
        finish();
    }

    protected void redirectLoginActivity() {
//        Toast.makeText(this, "LoginActivity로", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Login", true);
        startActivity(intent);
        finish();
    }
}
