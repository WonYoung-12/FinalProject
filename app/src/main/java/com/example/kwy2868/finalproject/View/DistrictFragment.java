package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.baoyz.widget.PullRefreshLayout;
import com.example.kwy2868.finalproject.Adapter.HospitalAdapter;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;

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

public class DistrictFragment extends Fragment implements AdapterView.OnItemSelectedListener, PullRefreshLayout.OnRefreshListener {
    @BindView(R.id.districtSpinner)
    Spinner districtSpinner;

    @BindView(R.id.districtRefreshLayout)
    PullRefreshLayout districtRefreshLayout;
    @BindView(R.id.districtRecyclerView)
    RecyclerView districtRecyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private HospitalAdapter hospitalAdapter;

    private Unbinder unbinder;

    private String currentDistrict;
    private List<Hospital> hospitalList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_district, container, false);
        unbinder = ButterKnife.bind(this, view);
        districtSpinner.setOnItemSelectedListener(this);
        districtRefreshLayout.setOnRefreshListener(this);
//        enrollLatLngToDB();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerViewSetting();
    }

    public void recyclerViewSetting() {
        layoutManager = new LinearLayoutManager(getContext());
        districtRecyclerView.setLayoutManager(layoutManager);
        districtRecyclerView.setHasFixedSize(true);
        hospitalAdapter = new HospitalAdapter(hospitalList);
        districtRecyclerView.setAdapter(hospitalAdapter);
    }

    // 거리 구할때 직선거리 말고는 없나..?
    public void getHospitalList(String district) {
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Hospital>> call = networkService.getHospitalList(district);
        call.enqueue(new Callback<List<Hospital>>() {
            @Override
            public void onResponse(Call<List<Hospital>> call, Response<List<Hospital>> response) {
                if (response.isSuccessful()) {
                    hospitalList = response.body();
                    Log.d("사이즈", hospitalList.size() + " ");
                    // notify 해주자.
                    refreshRecyclerView();
                    Log.d("HospitalList", hospitalList.get(0).getName() + " ");
                } else {
//                    Log.d("씨발", "레트로핏 안된다.");
                }
            }

            @Override
            public void onFailure(Call<List<Hospital>> call, Throwable t) {
                Log.d("Network Error", "레트로핏 못 받아옴.");
            }
        });
        districtRefreshLayout.setRefreshing(false);
    }

    public void refreshRecyclerView() {
        hospitalAdapter.setHospitalList(hospitalList);
        districtRecyclerView.setAdapter(hospitalAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // 강동구도 뜸.
        // 가만히 냅둬도 호출되니까 이렇게 하면 여러번 호출이 안되겠지
        if(currentDistrict == null || !currentDistrict.equals((String) adapterView.getItemAtPosition(i))){
            currentDistrict = (String) adapterView.getItemAtPosition(i);
//            Toast.makeText(getContext(), adapterView.getItemAtPosition(i) + " ", Toast.LENGTH_SHORT).show();
            getHospitalList(currentDistrict);
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
        getHospitalList(currentDistrict);
    }
}
