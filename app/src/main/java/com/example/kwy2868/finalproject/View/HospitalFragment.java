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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.kwy2868.finalproject.Adapter.HospitalAdapter;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.LocationHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class HospitalFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, PullRefreshLayout.OnRefreshListener, CheckBox.OnCheckedChangeListener, LocationHelper {
    @BindView(R.id.districtSpinner)
    Spinner districtSpinner;

    @BindView(R.id.hospitalRefreshLayout)
    PullRefreshLayout hospitalRefreshLayout;
    @BindView(R.id.hospitalRecyclerView)
    RecyclerView hospitalRecyclerView;

    @BindView(R.id.byDistance)
    CheckBox checkBox;

    private StaggeredGridLayoutManager layoutManager;

    private HospitalAdapter hospitalAdapter;

    private Unbinder unbinder;

    private String currentDistrict;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean isSetLocation = false;
    private boolean isFirst = true;
    private Location currentLocation;
    private double currentLatitude;
    private double currentLongitude;

    private List<Hospital> hospitalList;
    private List<Hospital> sortedHospitalList;

    private static final int REQUEST_CODE = 0;
    private static final int COLUMN_SPAN = 2;

    private boolean byDistance = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital, container, false);
        unbinder = ButterKnife.bind(this, view);
        districtSpinner.setOnItemSelectedListener(this);
        hospitalRefreshLayout.setOnRefreshListener(this);
//        enrollLatLngToDB();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerViewSetting();
        checkBox.setOnCheckedChangeListener(this);
    }

    public void recyclerViewSetting() {
        layoutManager = new StaggeredGridLayoutManager(COLUMN_SPAN, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        hospitalRecyclerView.setLayoutManager(layoutManager);
        hospitalRecyclerView.setHasFixedSize(true);
        hospitalRecyclerView.setItemAnimator(null);
        hospitalAdapter = new HospitalAdapter(hospitalList, byDistance);
        hospitalRecyclerView.setAdapter(hospitalAdapter);
    }

    public void refreshRecyclerView(List<Hospital> hospitalList){
        layoutManager = new StaggeredGridLayoutManager(COLUMN_SPAN, StaggeredGridLayoutManager.VERTICAL);
        hospitalRecyclerView.setLayoutManager(layoutManager);
        hospitalAdapter = new HospitalAdapter(hospitalList, byDistance);
        hospitalRecyclerView.setAdapter(hospitalAdapter);
    }

    // 거리 구할때 직선거리 말고는 없나..?
    public void getHospitalList(String district) {
        final NetworkService networkService = NetworkManager.getNetworkService();

        Call<List<Hospital>> call = networkService.getHospitalList(district);
        call.enqueue(new Callback<List<Hospital>>() {
            @Override
            public void onResponse(Call<List<Hospital>> call, Response<List<Hospital>> response) {
                if (response.isSuccessful()) {
                    hospitalList = response.body();
                    refreshRecyclerView(hospitalList);
                } else {
//                    Log.d("씨발", "레트로핏 안된다.");
                }
            }

            @Override
            public void onFailure(Call<List<Hospital>> call, Throwable t) {
                Log.d("Network Error", "레트로핏 못 받아옴.");
            }
        });
        hospitalRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // 강동구도 뜸.
        // 가만히 냅둬도 호출되니까 이렇게 하면 여러번 호출이 안되겠지
        if (currentDistrict == null || !currentDistrict.equals((String) adapterView.getItemAtPosition(i))) {
            currentDistrict = (String) adapterView.getItemAtPosition(i);
//            Toast.makeText(getContext(), adapterView.getItemAtPosition(i) + " ", Toast.LENGTH_SHORT).show();
            // 거리순으로 보고자할때.
            getHospitalList(currentDistrict);
            checkBox.setChecked(false);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        Log.d("갱신하자", "갱신하자");
        // 거리순으로 보는상태면.
        if (checkBox.isChecked()) {
            Toasty.Config.getInstance()
                    .setInfoColor(ContextCompat.getColor(getContext(), android.R.color.black))
                    .apply();
            Toasty.info(getContext(), "현재 위치를 가져옵니다.", Toast.LENGTH_SHORT, true).show();
            Toasty.Config.reset();
            permissionCheck();
        }
        // 아니면.
        else {
            getHospitalList(currentDistrict);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        // 체크해서 현재 위치 기준으로 정렬하려 할 때.
        if (isChecked) {
            byDistance = true;
            Log.d("체크되었다.", isChecked + "");
            if (isSetLocation) {
                if(sortedHospitalList == null){
                    calcDistance(hospitalList);
                    // 이제 거리 순으로 리스트를 정렬해보자.
                    sortedHospitalList = new ArrayList<>();
                    sortedHospitalList.addAll(hospitalList);
                    Collections.sort(sortedHospitalList);
                    refreshRecyclerView(sortedHospitalList);
                }
                else{
                    refreshRecyclerView(sortedHospitalList);
                }
            } else {
                Toasty.Config.getInstance()
                        .setInfoColor(ContextCompat.getColor(getContext(), android.R.color.black))
                        .apply();
                Toasty.info(getContext(), "현재 위치를 가져옵니다.", Toast.LENGTH_SHORT, true).show();
                Toasty.Config.reset();

                if(GlobalData.getCurrentLocation() == null) {
                    permissionCheck();
                }
                else{
                    refreshRecyclerView(sortedHospitalList);
                }
            }
        }
        else {
            byDistance = false;
            Log.d("체크해제되었다.", isChecked + "");
            refreshRecyclerView(hospitalList);
        }
    }

    public void permissionCheck() {
        // 퍼미션 체크. 권한이 있는 경우.
        // 현재 안드로이드 버전이 마시멜로 이상인 경우 퍼미션 체크가 추가로 필요함.
        Log.d("내 버전 정보", Build.VERSION.SDK_INT + " ");
        Log.d("마시멜로 정보", M + " ");
        if (Build.VERSION.SDK_INT >= M) {
            // 퍼미션이 없는 경우 퍼미션을 요구해야겠지?
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {
                // 사용자가 다시 보지 않기에 체크 하지 않고, 퍼미션 체크를 거절한 이력이 있는 경우. (처음 거절한 경우에도 들어감.)
                // 최초 요청시에는 false를 리턴해서 아래 else에 들어간다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            // 퍼미션이 있는 경우.
            else {
                getCurrentLocation();
            }
        }
        // 버전 낮은거.
        else {
            getCurrentLocation();
        }
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
                Toasty.error(getContext(), "현재 위치를 받아오는 데 실패했습니다.", Toast.LENGTH_SHORT, true).show();
                checkBox.setChecked(false);
            }
        };

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    // 위치 받아왔으니 계산해주자.
    public void afterLocationUpdated(Location location) {
        Toasty.success(getContext(), "현재 위치를 받아오는데 성공했습니다.", Toast.LENGTH_SHORT, true).show();
        currentLocation = location;

        isSetLocation = true;
        // 이거 세팅이 되어 있으면 다른 액티비티에서 또 위치 받아오는 거 생략해주게 해주자.
        GlobalData.setCurrentLocation(currentLocation);

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        // 일반 쓰레드니까 따로 메소드로 빼주자.
        locationManager.removeUpdates(locationListener);

        calcDistance(hospitalList);
        // 이제 거리 순으로 리스트를 정렬해보자.
        sortedHospitalList = new ArrayList<>();
        sortedHospitalList.addAll(hospitalList);
        Collections.sort(sortedHospitalList);
        // 화면에도 뿌려주자.
        // 중간에 체크 해제하는 예외처리?

        if (checkBox.isChecked()) {
            refreshRecyclerView(sortedHospitalList);
        }
        hospitalRefreshLayout.setRefreshing(false);
    }

    public void calcDistance(List<Hospital> hospitalList) {
        for (int i = 0; i < hospitalList.size(); i++) {
            Hospital hospital = hospitalList.get(i);

            // 병원 목록들의 위치를 지정해준다.
            Location hospitalLocation = new Location(hospital.getName());
            hospitalLocation.setLatitude(hospital.getLatitude());
            hospitalLocation.setLongitude(hospital.getLongitude());

            // 이제 거리를 구해보자. 구하고 병원 목록에 저장해두고 이제 화면에 뿌려주자.
            // 단위는 m인데 Hospital의 set 메소드에서 1000으로 나누어 km단위로 바꾸어주자.
            float distance = currentLocation.distanceTo(hospitalLocation);
            hospital.setDistanceFromCurrentLocation(distance);
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // requestPermission 메소드의 requestCode와 일치하는지 확인.
        if (requestCode == REQUEST_CODE) {
            Log.d("퍼미션 요구", "퍼미션 요구");
            // 요구하는 퍼미션이 한개이기 때문에 하나만 확인한다.
            // 해당 퍼미션이 승낙된 경우.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                Log.d("퍼미션 승인", "퍼미션 승인");
                getCurrentLocation();
            }
            // 해당 퍼미션이 거절된 경우.
            else {
                Log.d("퍼미션 거절", "퍼미션 거절");
                Toasty.error(getContext(), "퍼미션을 승인 해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT, true).show();
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
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GlobalData.getCurrentLocation() != null) {
            isSetLocation = true;
            currentLocation = GlobalData.getCurrentLocation();
        }
        if(isFirst){
            isFirst = !isFirst;
        }
        else{
            if(checkBox.isChecked()){
                refreshRecyclerView(sortedHospitalList);
            }
            else{
                getHospitalList(currentDistrict);
            }
        }
    }
}
