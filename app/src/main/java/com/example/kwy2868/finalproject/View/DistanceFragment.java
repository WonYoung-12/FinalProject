package com.example.kwy2868.finalproject.View;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Adapter.HospitalAdapter;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NaverAPI.GeocodingService;
import com.example.kwy2868.finalproject.Retrofit.NaverAPI.NaverAPIManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;
import com.example.kwy2868.finalproject.Util.ParsingHelper;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class DistanceFragment extends Fragment {

    @BindView(R.id.distanceRecyclerView)
    RecyclerView distanceRecyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private HospitalAdapter hospitalAdapter;

    private static final int REQUEST_CODE = 0;
    private static final int CLOSEST = 0;
    private static final int FURTHERMOST = 14;

    private Location currentLocation;

    // 모든 병원 정보를 받아오자. 나중에 정렬해주자.
    private List<Hospital> hospitalList;

    // 15개만 뿌려주기 위해 필요한 리스트.
    private List<Hospital> sortedHospitalList;

    private Unbinder unbinder;

    public static DistanceFragment newInstance(Location location){
        DistanceFragment distanceFragment = new DistanceFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Location", location);
        distanceFragment.setArguments(bundle);

        return distanceFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_distance, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("아규먼트 널 테스트", getArguments() + " ");
        if(getArguments() != null)
            currentLocation = getArguments().getParcelable("Location");
        getEveryHospitalFromServer();
    }

    public void getEveryHospitalFromServer() {
        NetworkService networkService = NetworkManager.getNetworkService();

        Call<List<Hospital>> serverCall = networkService.getAllHospital();
        serverCall.enqueue(new Callback<List<Hospital>>() {
            @Override
            public void onResponse(Call<List<Hospital>> call, Response<List<Hospital>> response) {
                if (response.isSuccessful()) {
                    // 모든 병원 리스트 받아왔어.
                    hospitalList = response.body();
                    calcDistance(hospitalList);
//                    getLatLngForHospitalList();
                    Log.d("레트로핏 테스트.", "모두 정상적으로 받아왔다.");
                }
            }

            @Override
            public void onFailure(Call<List<Hospital>> call, Throwable t) {

            }
        });
    }

    public void calcDistance(List<Hospital> hospitalList){
        for(int i=0; i<hospitalList.size(); i++){
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

        // 이제 거리 순으로 리스트를 정렬해보자.
        Collections.sort(hospitalList);
        summarizeList();
        // 화면에도 뿌려주자.
        recyclerviewSetting();
    }

    public void recyclerviewSetting(){
        Log.d("거리 리사이클러뷰 세팅", "세팅하자");
        layoutManager = new LinearLayoutManager(getContext());
        distanceRecyclerView.setLayoutManager(layoutManager);
        distanceRecyclerView.setHasFixedSize(true);
        // TODO 여기서 모든 리스트를 해버리니까 너무 버벅인다..! 15개만 가져오자.
        hospitalAdapter = new HospitalAdapter(sortedHospitalList);
        distanceRecyclerView.setAdapter(hospitalAdapter);
    }

    public void summarizeList(){
        sortedHospitalList = new ArrayList<Hospital>();
        // 15개만 가져오자.
//        sortedHospitalList.subList(CLOSEST, FURTHERMOST);
        for(int i=CLOSEST; i<FURTHERMOST; i++){
            sortedHospitalList.add(hospitalList.get(i));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // 네이버 지오코딩 api 이용해서 좌표값 다 받아왔다! 한번만 쓰고 추후 테스트에서는 필요 없다.
    public void getLatLngForHospitalList() {
        GeocodingService geocodingService = NaverAPIManager.getGeocodingService();

        // 리스트에 있는 모든 거 지오코딩 해야겠지?
        for (int i = 0; i < hospitalList.size(); i++) {
            final Hospital hospital = hospitalList.get(i);
            String address = hospital.getAddress();

            Call<JsonObject> call = geocodingService.getLatLng(address);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.d("지오코딩 되는걸까", "된다");
                        JsonObject jsonObject = response.body();
                        ParsingHelper.geocodingParsing(hospital, jsonObject);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

            // 익명클래스 안하는 방법. 근데 이제 필요없다.
            // geocodingListener = new GeocodingListener(hospital);

//            call.enqueue(geocodingListener);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                }
//            });
        }
        Toast.makeText(getContext(), "Latitude : " + hospitalList.get(0).getLatitude() + "Longitude : " + hospitalList.get(0).getLongitude(), Toast.LENGTH_SHORT).show();
    }

    //    @OnClick(R.id.enrollButton)
//    public void enrollLatLngToDB() {
//        NetworkService networkService = NetworkManager.getNetworkService();
//        for (int i = 0; i < hospitalList.size(); i++) {
//            Call<JsonObject> call = networkService.enrollLatLng(hospitalList.get(i).getNum(), hospitalList.get(i).getLatitude(), hospitalList.get(i).getLongitude());
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    JsonObject jsonObject = response.body();
//                    Log.d("DB 등록 테스트", "등록 성공");
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                }
//            });
//        }
//    }
}
