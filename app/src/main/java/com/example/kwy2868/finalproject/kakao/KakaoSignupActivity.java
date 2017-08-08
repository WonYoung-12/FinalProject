package com.example.kwy2868.finalproject.kakao;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;

import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.R;
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

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by kwy2868 on 2017-07-26.
 */

public class KakaoSignupActivity extends Activity {
    private static final String USER = "User";

    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        requestMe();
    }

    // 카카오 로그인 화면의 StatusBar 색상도 바꿔주자.
    public void setStatusBarColor(){
        if (Build.VERSION.SDK_INT >= M) {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.kakaoColor));
        }
    }

    // 유저의 정보를 받아오는 함수.
    // 세션 연결?
    protected void requestMe(){
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
                if(result == ErrorCode.CLIENT_ERROR_CODE){
                    finish();
                }
                else{
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

                // 유저 세팅.
                UserInfo user = new UserInfo();
                user.setEmail(result.getEmail());
                user.setUserId(result.getId());
                user.setNickname(result.getNickname());
                user.setThumbnailImagePath(result.getThumbnailImagePath());

                Log.d("유저", "User : " + user.toString());

                redirectMainActivity(user);
            }
        }, propertyKeys, false);
    }

    private void redirectMainActivity(UserInfo user){
//        Toast.makeText(this, "MainActivity로", Toast.LENGTH_SHORT).show();
        Parcelable wrappedUser = Parcels.wrap(user);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USER, wrappedUser);

        startActivity(intent);
        finish();
    }

    protected void redirectLoginActivity(){
//        Toast.makeText(this, "LoginActivity로", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Login", true);
        startActivity(intent);
        finish();
    }
}
