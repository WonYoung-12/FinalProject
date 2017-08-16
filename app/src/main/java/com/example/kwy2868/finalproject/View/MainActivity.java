package com.example.kwy2868.finalproject.View;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.kwy2868.finalproject.Model.AlarmEvent;
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;
import com.example.kwy2868.finalproject.Util.MyAlarmManager;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
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

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        currentLocation = GlobalData.getCurrentLocation();
        GlobalData.setContext(this);

        getChartListFromServer();
        getPetListFromServer();
        viewPagerSetting();
        toolbarSetting();
        drawerSetting();
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

    public void getPetListFromServer(){
        Log.d("펫 리스트 가져온다", "가져온다");
        final NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Pet>> call = networkService.getPetList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if(response.isSuccessful()){
                    List<Pet> petList = response.body();
                    GlobalData.setPetList(petList);
                    System.out.println("이미지들");
                    for(int i=0; i<petList.size(); i++){
                        final Pet pet = petList.get(i);
                        System.out.println(pet.getImagePath());
                        // 등록된 경로가 있으면 이미지 받아오자.
                        if(!(pet.getImagePath() == null || pet.getImagePath().trim().equals(""))){
                            Call<ResponseBody> imageCall = networkService.getPetImage(pet.getImagePath());
                            imageCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()){
                                        File imgFile = convertToFile(response.body(), pet.getImagePath());
                                        pet.setImgFile(imgFile);
                                        Log.d("이미지 가져옴", "가져왔다");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("이미지 못 가져옴", "못 가져왔다.");
                                    t.printStackTrace();
                                }
                            });
                            Log.d("이미지 경로", pet.getImagePath());
                        }
                    }
                    Log.d("펫 리스트 받아옴", "펫 리스트 받아왔다");
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {

            }
        });
    }

    public File convertToFile(ResponseBody responseBody, String imgPath){
        try{
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File img = new File(path, "/" + imgPath);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            int read;

            try{
                byte[] fileReader = new byte[4096];

                long fileSize = responseBody.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = responseBody.byteStream();
                outputStream = new FileOutputStream(img);

                while((read = inputStream.read(fileReader)) != -1){

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    Log.d("다운로드", "file download : " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();

                return img;
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
            finally {
                if(inputStream != null)
                    inputStream.close();
                if(outputStream != null)
                    outputStream.close();
            }
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlarmEvent(AlarmEvent alarmEvent) {
        Log.d("Alerter", "Alerter");
        Alerter.create(this)
                .setTitle(alarmEvent.getTitle())
                .setBackgroundColorInt(getResources().getColor(R.color.alert_default_icon_color, null))
                .setText(alarmEvent.getDescription())
                .show();
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
            startActivity(new Intent(this, AddPetActivity.class));

        } else if (id == R.id.nav_mychart) {
            // 메인은 종료 되지는 않는다.
            startActivity(new Intent(this, AddChartActivity.class));
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

    @Override
    public void locationDeliver(Double latitude, Double longitude) {
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
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
            getPetListFromServer();
        }
    }
}