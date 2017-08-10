package com.example.kwy2868.finalproject.kakao;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.LocationHelper;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by kwy2868 on 2017-07-25.
 */

//// 여기서 현재 위치 받아오자..! 로딩 화면을 통해서
//// 이미지 움직이는거 어떻게 할까
public class LoginActivity extends AppCompatActivity implements LocationHelper {
//    @BindView(R.id.loginBackground)
//    ImageView loginBackground;
    @BindView(R.id.loadingIndicator)
    AVLoadingIndicatorView loadingIndicator;
    @BindView(R.id.loadingText)
    TextView loadingText;

    // 콜백 선언.
    private SessionCallback callback;

    private Unbinder unbinder;

    private static final String IMG_URL = "";
    private static final int REQUEST_CODE = 0;

    private static final boolean LOCATION_SET = false;
    private static final String LOCATION_TAG = "Current Location";

    private Location currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    // 현재 위치의 좌표값. 이걸 이용해서 이제 거리를 구할 수 있겠지?
    private double currentLatitude;
    private double currentLongitude;

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

        YoYo.with(Techniques.FadeIn)
                .duration(1500).repeat(YoYo.INFINITE)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .playOn(loadingText);

        permissionCheck();
    }

    public void permissionCheck() {
        // 퍼미션 체크. 권한이 있는 경우.
        // 현재 안드로이드 버전이 마시멜로 이상인 경우 퍼미션 체크가 추가로 필요함.
        Log.d("내 버전 정보", Build.VERSION.SDK_INT + " ");
        Log.d("마시멜로 정보", M + " ");
        if (Build.VERSION.SDK_INT >= M) {
            // 퍼미션이 없는 경우 퍼미션을 요구해야겠지?
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {
                // 사용자가 다시 보지 않기에 체크 하지 않고, 퍼미션 체크를 거절한 이력이 있는 경우. (처음 거절한 경우에도 들어감.)
                // 최초 요청시에는 false를 리턴해서 아래 else에 들어간다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.d("다시 물어본다", "다시 물어본다.");
                }
                // 사용자가 다시 보지 않기에 체크하고, 퍼미션 체크를 거절한 이력이 있는 경우.
                // 퍼미션을 요구하는 새로운 창을 띄워줘야 겠지.
                // 최초 요청시에도 들어가게 됨. 다시 보지 않기에 체크하는 창은 물어보지 않음.
                else {
                    Log.d("다시 물어보지 않는다", "다시 물어보지 않는다.");
                }
                // 액티비티, permission String 배열, requestCode를 인자로 받음.
                // 퍼미션을 요구하는 다이얼로그 창을 띄운다.
                // requestCode 다르게 하면 다르게 처리할 수 있을듯?
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            // 퍼미션이 있는 경우.
            else {
                Toast.makeText(this, "현재 위치를 받아오는 중입니다.", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
        }
        // 버전 낮은거.
        else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // requestPermission 메소드의 requestCode와 일치하는지 확인.
        if (requestCode == REQUEST_CODE) {
            Log.d("퍼미션 요구", "퍼미션 요구");
            // 요구하는 퍼미션이 한개이기 때문에 하나만 확인한다.
            // 해당 퍼미션이 승낙된 경우.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("퍼미션 승인", "퍼미션 승인");
                Toast.makeText(this, "현재 위치를 받아오는 중입니다.", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
            // 해당 퍼미션이 거절된 경우.
            else {
                Log.d("퍼미션 거절", "퍼미션 거절");
                Toast.makeText(this, "퍼미션을 승인 해주셔야 이용이 가능합니다", Toast.LENGTH_SHORT).show();
                finish();
                // 앱 정보 화면을 통해 퍼미션을 다시 요구해보자.
                requestPermissionInSettings();
            }
        }
    }

    // 사용자에게 설정 창으로 넘어가게 하여 퍼미션 설정하도록 유도.
    public void requestPermissionInSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void backGroundSetting(){
//        Glide.with(this)
//                .load(R.drawable.main)
//                .into(loginBackground);
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

    @Override
    public void getCurrentLocation() {
        Log.d("현재 위치 받아오자.", "받아오자");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                afterLocationUpdated(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("onStatusChanged", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("onProviderDisabled", "onProviderDisabled");
                Toast.makeText(getApplicationContext(), "현재 위치를 받아오는데 실패 했습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // 0.1초마다, 1m 변하면 업데이트.
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            // redirectSignupActivity(); // 세션 연결시 SignupActivity로.
            redirectSignupActivity(currentLocation);
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
    protected void redirectSignupActivity(Location location){
//        Toast.makeText(this, "SignupActivity로", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(LOCATION_TAG, location);

        startActivity(intent);
        finish();
    }

    public void afterLocationUpdated(Location location){
        Toast.makeText(getApplicationContext(), "현재 위치를 받아오는데 성공 했습니다.", Toast.LENGTH_SHORT).show();

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.d("Latitude", "Latitude : " + currentLatitude);
        Log.d("Longitude", "Longitude : " + currentLongitude);
        // 일반 쓰레드니까 따로 메소드로 빼주자.

        currentLocation = new Location(LOCATION_TAG);
        currentLocation.setLatitude(currentLatitude);
        currentLocation.setLongitude(currentLongitude);
        locationManager.removeUpdates(locationListener);

        // 여기서 UI 바꿔주자.
        loadingIndicator.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
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
