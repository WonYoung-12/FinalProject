package com.example.kwy2868.finalproject.View;

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
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwy2868.finalproject.Adapter.ViewPagerAdapter;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.R;
import com.kakao.auth.Session;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private static final String USER = "User";
    private static final int DISTRICT = 0;
    private static final int DISTANCE = 1;
    private static final int SEARCH = 2;

    // 백버튼을 2초 이내로 두번 누르면 종료한다.
    private static final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private String[] tabTitles = new String[]{"지역별", "거리순", "검색"};

    private static UserInfo user;

    ///////////////// 퍼미션 체크하고 현재 위치 받아오는 부분 //////////////////
    private static final int REQUEST_CODE = 0;

    private static final String LOCATION_TAG = "Current Location";
    private Location currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    // 이걸로 안 막아두면 가만히 있어도 계속 위치 바껴서 refresh 된다..!
    private Boolean isFirst = true;

    // 현재 위치의 좌표값. 이걸 이용해서 이제 거리를 구할 수 있겠지?
    private double currentLatitude;
    private double currentLongitude;

    private ViewPagerAdapter viewPagerAdapter;

    private static final int FRAGMENT_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbarSetting();
        drawerSetting();

        permissionCheck();
    }

    public void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    public void drawerSetting() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final ImageView userImage = navigationView.getHeaderView(0).findViewById(R.id.userImage);
        Log.d("유저 이미지뷰 널 테스트", userImage + " ");
        TextView userNickname = navigationView.getHeaderView(0).findViewById(R.id.userNickname);
        TextView userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);

        // TODO 여기 꼭 바꿔줘야해! 지금은 테스트라 프로필 정보 안넘기는거다!!
        if (getIntent() != null) {
            Intent intent = getIntent();
            user = Parcels.unwrap(intent.getParcelableExtra(USER));
            if (user != null) {
                if (user.getThumbnailImagePath() != null)
                    Glide.with(this).load(user.getThumbnailImagePath())
                            .centerCrop().bitmapTransform(new CropCircleTransformation(this))
                            .into(userImage);
                userNickname.setText(user.getNickname());
                Log.d("유저 이메일 널 테스트", user.getEmail() + " ");
                userEmail.setText(user.getEmail());
            }
        }
        navigationView.setNavigationItemSelectedListener(this);
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
    }

    public void viewPagerSetting(){
        // TabLayout과 ViewPager 연동을 하자.
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), currentLocation);
        viewPager.setAdapter(viewPagerAdapter);
        // 미리 3개를 만들어 놓는다.
        viewPager.setOffscreenPageLimit(FRAGMENT_COUNT);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);

        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                "서울시 구별로 동물 병원을 조회할 수 있습니다.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        tabLayoutSetting();
    }

    public void tabLayoutSetting() {
        tabLayout.addOnTabSelectedListener(this);
        // 아이콘 설정.
        int[] tabIcons = new int[]{R.drawable.ic_room_white_24dp, R.drawable.ic_transfer_within_a_station_white_24dp,
                R.drawable.ic_search_white_24dp};
        for (int i = 0; i < FRAGMENT_COUNT; i++)
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
    }

    // TODO 이런식으로 하면 백타 안될 것 같은데...
    public static UserInfo getUser() {
        return user;
    }

    // 앱을 종료하면 로그아웃 처리되서 실행 시마다 로그인을 할 수 있다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
        Log.d("Session Test", Session.getCurrentSession() + " ");
        Session session = Session.getCurrentSession();
        // 이거하면 로그아웃 할때마다?
        // session.close();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case DISTRICT:
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "서울시 구별로 동물 병원을 조회할 수 있습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case DISTANCE:
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "현재 위치 기준으로 가까운 동물 병원을 15개 조회할 수 있습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case SEARCH:
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "네이버 블로그, 카페, 지식iN에서 유사한 20가지의 결과를 검색 할 수 있습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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

    public void getCurrentLocation() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(isFirst){
                    isFirst = !isFirst;
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    Log.d("Latitude", "Latitude : " + currentLatitude);
                    Log.d("Longitude", "Longitude : " + currentLongitude);
                    Toast.makeText(getApplicationContext(), "현재 위치를 받아오는데 성공 했습니다.", Toast.LENGTH_SHORT).show();

                    currentLocation = new Location(LOCATION_TAG);
                    currentLocation.setLatitude(currentLatitude);
                    currentLocation.setLongitude(currentLongitude);
                    viewPagerSetting();
                }
                else
                    return;
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Toast.makeText(this, "카메라", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // 드로어 열려 있으면 우선 드로어 닫아주자.
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        // 드로어 안 열려 있을때.
        else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;
            if (intervalTime >= 0 && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(this, "종료하려면 한번 더 누르세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void floatingButtonClick() {
        Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Toast.makeText(this, "플로팅 버튼 클릭.", Toast.LENGTH_SHORT).show();
    }
}