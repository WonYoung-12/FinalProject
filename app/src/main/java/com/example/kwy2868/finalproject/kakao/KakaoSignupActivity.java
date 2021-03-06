package com.example.kwy2868.finalproject.kakao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.LoginResult;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.example.kwy2868.finalproject.View.LoginActivity;
import com.example.kwy2868.finalproject.View.MainActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-07-26.
 */

public class KakaoSignupActivity extends Activity {

    private static final int KAKAO_FLAG = 1;

    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
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
        Call<LoginResult> call = networkService.login(user);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200){
                        user.setNotiFlag(response.body().getNotiFlag());
                        redirectMainActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(KakaoSignupActivity.this, "네트워크 문제, 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                redirectLoginActivity();
            }
        });
    }

    private void redirectMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
