package com.example.kwy2868.finalproject.View;


import android.content.Intent;
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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwy2868.finalproject.Adapter.ViewPagerAdapter;
import com.example.kwy2868.finalproject.Model.AlarmEvent;
import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;
import com.example.kwy2868.finalproject.Util.MyAlarmManager;
import com.kakao.auth.Session;
import com.nhn.android.naverlogin.OAuthLogin;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 그래도 프래그먼트를 가지고 있는 액티비티도 가지고 있어야 할 것 같고, 나중에 혹시 모르니까..!
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener{

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
    @BindView(R.id.notiSwitch)
    Switch notiSwitch;
    @BindView(R.id.logoutButton)
    Button logoutButton;

    private static final int DISTRICT = 0;
    private static final int DISTANCE = 1;
    private static final int SEARCH = 2;

    // 백버튼을 2초 이내로 두번 누르면 종료한다.
    private static final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private String[] tabTitles = new String[]{"지역별", "거리순", "검색"};

    private static UserInfo user;


    private ViewPagerAdapter viewPagerAdapter;

    private static final int FRAGMENT_COUNT = 3;

    private boolean isFirst = true;

    private static final int NOTIFICATION_OFF = 0;
    private static final int NOTIFICATION_ON = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GlobalData.setContext(this);

        getChartListFromServer();
        viewPagerSetting();
        toolbarSetting();
        drawerSetting();
        initNotiButton();
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
        TextView userNickname = navigationView.getHeaderView(0).findViewById(R.id.userNickname);
        TextView userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);

        user = GlobalData.getUser();
        Glide.with(this).load(user.getThumbnailImagePath())
                .centerCrop().bitmapTransform(new CropCircleTransformation(this))
                .into(userImage);
        userNickname.setText(user.getNickname());
        userEmail.setText(user.getEmail());
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void viewPagerSetting() {
        // TabLayout과 ViewPager 연동을 하자.
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
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
        int[] tabIcons = new int[]{R.drawable.ic_room_white_24dp, R.drawable.ic_search_white_24dp,
                R.drawable.ic_account_circle_white_24dp};
        for (int i = 0; i < FRAGMENT_COUNT; i++)
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
    }

    public void getChartListFromServer() {
        Log.d("차트 리스트 가져온다", "가져온다");
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Chart>> call = networkService.getChartList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Chart>>() {
            @Override
            public void onResponse(Call<List<Chart>> call, Response<List<Chart>> response) {
                if (response.isSuccessful()) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlarmEvent(AlarmEvent alarmEvent) {
        Log.d("Alerter", "Alerter");
        Alerter.create(this)
                .setTitle(alarmEvent.getTitle())
                .setText(alarmEvent.getDescription())
                .show();
        Log.d("Alerter 보여주자", "보여주자");
        Log.d("Title", alarmEvent.getTitle());
        Log.d("Text", alarmEvent.getDescription());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case DISTRICT:
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "서울시 내의 동물 병원을 조회할 수 있습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case DISTANCE:
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "네이버 블로그, 카페, 지식iN에서 유사한 20가지의 결과를 검색 할 수 있습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case SEARCH:
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "등록한 반려 동물과 진료 기록들을 볼 수 있습니다.", Snackbar.LENGTH_LONG)
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
            startActivity(new Intent(this, FavoriteActivity.class));
            Toast.makeText(this, "즐겨찾기", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_blacklist) {
            startActivity(new Intent(this, BlackActivity.class));
        } else if (id == R.id.nav_addpet) {
            startActivity(new Intent(this, AddPetActivity.class));
        } else if (id == R.id.nav_addchart) {
            // 메인은 종료 되지는 않는다.
            startActivity(new Intent(this, AddChartActivity.class));
        } else if (id == R.id.nav_mypet) {
            startActivity(new Intent(this, MyPetActivity.class));
        } else if (id == R.id.nav_mychart) {
            startActivity(new Intent(this, MyChartActivity.class));
        } else if (id == R.id.nav_setting) {
//            startActivity(new Intent(this, SettingActivity.class));
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
    protected void onResume() {
        super.onResume();
        // onCreate에서 호출 되고 onResume에서도 호출되는 것을 막아준다.
        if(isFirst) {
            isFirst = !isFirst;
        }
        else{
            getChartListFromServer();
        }
    }

    public void initNotiButton(){
        Log.d("노티 플래그", GlobalData.getUser().getNotiFlag() + "");
        // 알람을 받지 않는 상태이면.
        if(GlobalData.getUser().getNotiFlag() == 0){
            notiSwitch.setChecked(false);
        }
        else{
            notiSwitch.setChecked(true);
        }
    }

    @OnClick(R.id.notiSwitch)
    public void notiSetting(){
        if(!notiSwitch.isChecked()){
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
                        Toast.makeText(MainActivity.this, "알람 세팅 변경 완료", Toast.LENGTH_SHORT).show();
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
        // 네이버 로그인한 경우.
        if(GlobalData.getUser().getFlag() == 0){
            OAuthLogin oAuthLoginModule = OAuthLogin.getInstance();
            oAuthLoginModule.logoutAndDeleteToken(this);
        }
        // 사용자가 카카오 로그인한 경우 세션 해제해주고 다시 로그인 액티비티로 가게 해주자.
        else{
            Session session = Session.getCurrentSession();
            // 이거하면 로그아웃 할때마다?
            session.close();
        }

        // 로그아웃 되었으니 해제해주자.
            GlobalData.setUser(null);

            Intent intent = new Intent(this, LoginActivity.class);
            // 모든 액티비티 스택을 지워준다.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
    }
}