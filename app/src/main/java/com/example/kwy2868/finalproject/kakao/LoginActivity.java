package com.example.kwy2868.finalproject.kakao;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kwy2868.finalproject.R;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by kwy2868 on 2017-07-25.
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.loginBackground)
    ImageView loginBackground;

    // 콜백 선언.
    private SessionCallback callback;

    private Unbinder unbinder;

    private static final String IMG_URL = "";
    private static final int REQUEST_CODE = 0;

    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        unbinder = ButterKnife.bind(this);

        // 툴바 처리.
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // 자바 코드로 키 해시 받아오는 거.
//        Log.d("hash", "key : " + getKeyHash(this));

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        // 이거 호출하면 자동으로 로그인 처리가 됨.
        // 호출하지 않으면 로그인 버튼 눌러야 다음 화면으로 넘어간다.
//        Session.getCurrentSession().checkAndImplicitOpen();
        backGroundSetting();
    }

    public void backGroundSetting(){
        Glide.with(this)
                .load(R.drawable.main)
                .into(loginBackground);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
        unbinder.unbind();
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            // redirectSignupActivity(); // 세션 연결시 SignupActivity로.
            redirectSignupActivity();
        }
        // 세션 연결이 실패했을 때 로그인 화면 다시 불러온다.
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_login);
            if(Build.VERSION.SDK_INT >= L){
                getWindow().setStatusBarColor(getResources().getColor(R.color.kakaoColor));
            }
        }
    }

    // 세션 연결 성공 시 SignupActivity로.
    protected void redirectSignupActivity(){
//        Toast.makeText(this, "SignupActivity로", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    // 카카오 디벨로퍼 키 값을 등록하기 위해 자바로 받아오는 메소드.
//    public static String getKeyHash(final Context context) {
//        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
//        for (Signature signature : packageInfo.signatures) {
//            try {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
//            } catch (NoSuchAlgorithmException e) {
//                Log.w("key", "Unable to get MessageDigest. signature=" + signature, e);
//            }
//        }
//        return null;
//    }
//
//    private void requestAccessTokenInfo() {
//        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//            }
//
//            @Override
//            public void onNotSignedUp() {
//                // not happened
//            }
//
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                Logger.e("failed to get access token info. msg=" + errorResult);
//                Dialog.showDialog(LoginActivity.this, "네트워크 오류입니다. 잠시 후에 시도해주세요");
//            }
//
//            @Override
//            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
//                long userId = accessTokenInfoResponse.getUserId();
//                Log.d("up","this access token is for userId=" + userId);
//
//                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
//                Log.d("up","this access token expires after " + expiresInMilis + " milliseconds.");
//                requestMe(userId);
//            }
//        });
//    }
//
//    private void requestMe(final long userId) {
//        List<String> propertyKeys = new ArrayList<>();
//        propertyKeys.add("nickname");
//        propertyKeys.add("thumbnail_image");
//
//        UserManagement.requestMe(new MeResponseCallback() {
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                String message = "failed to get user info. msg=" + errorResult;
//                Logger.d(message);
//            }
//
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//            }
//
//            @Override
//            public void onSuccess(UserProfile userProfile) {
//                UserInfo user = new UserInfo();
//
//                user.setNickname(userProfile.getNickname());
//                user.setThumbnailImagePath(userProfile.getThumbnailImagePath());
//                user.setUserId(userId);
//            }
//
//            @Override
//            public void onNotSignedUp() {
//
//            }
//        }, propertyKeys, false);
//    }
}
