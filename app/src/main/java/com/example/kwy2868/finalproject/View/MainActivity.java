package com.example.kwy2868.finalproject.View;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;
import com.example.kwy2868.finalproject.Util.MyAlarmManager;
import com.kakao.auth.Session;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 사실 LocationDelivery를 통해 위치 새로 받아올 필요는 없다..!
// 그래도 프래그먼트를 가지고 있는 액티비티도 가지고 있어야 할 것 같고, 나중에 혹시 모르니까..!
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener, DistanceFragment.LocationDelivery {

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

    private static final String LOCATION_TAG = "Current Location";
    private static Location currentLocation;

    private ViewPagerAdapter viewPagerAdapter;

    private static final int FRAGMENT_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        currentLocation = GlobalData.getCurrentLocation();

        getChartListFromServer();
        viewPagerSetting();
        toolbarSetting();
        drawerSetting();
    }

    public void getChartListFromServer(){
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Chart>> call = networkService.getChartList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Chart>>() {
            @Override
            public void onResponse(Call<List<Chart>> call, Response<List<Chart>> response) {
                if(response.isSuccessful()){
                    GlobalData.setChartList(response.body());
                    Log.d("차트리스트", GlobalData.getChartList() + "");
                    MyAlarmManager.setAlarm();
                }
            }

            @Override
            public void onFailure(Call<List<Chart>> call, Throwable t) {

            }
        });
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

        user = GlobalData.getUser();
        Glide.with(this).load(user.getThumbnailImagePath())
                .centerCrop().bitmapTransform(new CropCircleTransformation(this))
                .into(userImage);
        userNickname.setText(user.getNickname());
        Log.d("유저 이메일 널 테스트", user.getEmail() + " ");
        userEmail.setText(user.getEmail());
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void viewPagerSetting() {
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

    // 앱을 종료하면 로그아웃 처리되서 실행 시마다 로그인을 할 수 있다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
        Log.d("Session Test", Session.getCurrentSession() + " ");
        Session session = Session.getCurrentSession();
//         이거하면 로그아웃 할때마다?
//        session.close();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            // Handle the camera action
            Toast.makeText(this, "카메라", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_blacklist) {

        } else if (id == R.id.nav_mypet) {
            startActivity(new Intent(this, PetActivity.class));

        } else if (id == R.id.nav_mychart) {
            // 메인은 종료 되지는 않는다.
            startActivity(new Intent(this, ChartActivity.class));
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingActivity.class));
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

    @Override
    public void locationDeliver(Double latitude, Double longitude) {
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
    }
}