package com.example.kwy2868.finalproject.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Adapter.ChartAdapter;
import com.example.kwy2868.finalproject.Adapter.PetAdapter;
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.MyAlarmManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by kwy2868 on 2017-08-17.
 */

public class MyInfoFragment extends Fragment {
    @BindView(R.id.myPetRecyclerView)
    RecyclerView myPetRecyclerView;
    @BindView(R.id.myChartRecyclerView)
    RecyclerView myChartRecyclerView;
    @BindView(R.id.showMorePetButton)
    Button showMorePetButton;
    @BindView(R.id.showMoreChartButton)
    Button showMoreChartButton;

    @BindView(R.id.myPet)
    LinearLayout myPet;
    @BindView(R.id.myChart)
    LinearLayout myChart;

    private RecyclerView.LayoutManager petLayoutManager;
    private PetAdapter petAdapter;
    // 전체 펫 리스트.
    private List<Pet> petList;
    // 사용자에게 보여주는 펫 리스트.
    private ArrayList<Pet> showPetList = new ArrayList<Pet>();
    // 현재 보여지고 있는 펫 수. 처음은 2개만.
    private int currentShowPetCount = 0;

    // 버튼 누르면 펫과 차트를 두개씩 더 보여줄 수 있게 해주는 상수. 2개씩 더.
    private static final int SHOW_MORE_COUNT = 2;

    private RecyclerView.LayoutManager chartLayoutManager;
    private ChartAdapter chartAdapter;
    private List<Chart> chartList;
    private ArrayList<Chart> showChartList = new ArrayList<Chart>();
    // 현재 보여지고 있는 펫 수. 처음은 2개만.
    private int currentShowChartCount = 0;

    private static final int COLUMN_SPAN = 2;
    private Unbinder unbinder;

    private static final int REQUEST_CODE = 0;

    private boolean isFirst = true;

    // 상태 저장을 위한 태그값.
    private static final String PET_COUNT_TAG = "Pet";
    private static final String CHART_COUNT_TAG = "Chart";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myinfo, container, false);
        unbinder = ButterKnife.bind(this, view);

        if(savedInstanceState != null){
            currentShowPetCount = savedInstanceState.getInt(PET_COUNT_TAG);
            currentShowChartCount = savedInstanceState.getInt(CHART_COUNT_TAG);
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getChartListFromServer();
        getPetListFromServer();

//        imagePermissionCheck();
    }

    public void getPetListFromServer() {
        Log.d("펫 리스트 가져온다", "가져온다");
        final NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Pet>> call = networkService.getPetList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if (response.isSuccessful()) {
                    petList = response.body();
                    GlobalData.setPetList(petList);
//                    System.out.println("이미지들");
                    petRecyclerViewSetting();
//                    for (int i = 0; i < petList.size(); i++) {
//                        final Pet pet = petList.get(i);
//                        // 등록된 경로가 있으면 이미지 받아오자.
//                        if (!(pet.getImagePath() == null || pet.getImagePath().trim().equals(""))) {
//                            Call<ResponseBody> imageCall = networkService.getPetImage(pet.getImagePath());
////                            Log.i("이미지 세팅", "세팅 해주자");
//
//                            imageCall.enqueue(new Callback<ResponseBody>() {
//                                @Override
//                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                    if (response.isSuccessful()) {
//                                        File imgFile = convertToFile(response.body(), pet.getImagePath());
//                                        pet.setImgFile(imgFile);
//                                        petAdapter.notifyDataSetChanged();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                    Log.d("이미지 못 가져옴", "못 가져왔다.");
//                                    t.printStackTrace();
//                                }
//                            });
//                            Log.d("이미지 경로", pet.getImagePath());
//                        }
//                    }
                    Log.d("펫 리스트 받아옴", "펫 리스트 받아왔다");
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {

            }
        });
    }

    public void getChartListFromServer() {
        Log.d("차트 리스트 가져온다", "가져온다");
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Chart>> call = networkService.getChartList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Chart>>() {
            @Override
            public void onResponse(Call<List<Chart>> call, Response<List<Chart>> response) {
                if (response.isSuccessful()) {
                    chartList = response.body();
                    GlobalData.setChartList(response.body());
                    Log.d("차트리스트", GlobalData.getChartList() + "");
                    if (GlobalData.getUser().getNotiFlag() == 1) {
                        Log.d("노티 승인", "울려라");
                        MyAlarmManager.setAlarm();
                    } else {
                        Log.d("노티 거절", "거절했으니 울리지마라");
                        MyAlarmManager.cancelAlarm();
                    }
                    chartRecyclerViewSetting();
                }
            }

            @Override
            public void onFailure(Call<List<Chart>> call, Throwable t) {

            }
        });
    }

    public void petRecyclerViewSetting() {
        petLayoutManager = new LinearLayoutManager(getContext());
        myPetRecyclerView.setLayoutManager(petLayoutManager);
        myPetRecyclerView.setItemAnimator(null);
        myPetRecyclerView.setNestedScrollingEnabled(false);

        // 저장된 값이 있는 경우.
        if(currentShowPetCount != 0){
            showPetList = new ArrayList<Pet>();

            if(currentShowPetCount > petList.size())
                currentShowPetCount = petList.size();

            if(currentShowPetCount < 2 && petList.size() >= 2)
                currentShowPetCount = 2;

            for(int i=0; i<currentShowPetCount; i++){
                showPetList.add(petList.get(i));
            }
            petAdapter = new PetAdapter(showPetList);
            myPetRecyclerView.setAdapter(petAdapter);
            return;
        }

        // 아이템이 3개 이상인 경우는 2개만 보여주자.
        if(petList.size() > 2){
            currentShowPetCount = 2;
            showPetList.add(petList.get(0));
            showPetList.add(petList.get(1));
            petAdapter = new PetAdapter(showPetList);
        }
        else{
            currentShowPetCount = petList.size();
            petAdapter = new PetAdapter(petList);
        }

        myPetRecyclerView.setAdapter(petAdapter);
    }

    public void chartRecyclerViewSetting() {
        chartLayoutManager = new LinearLayoutManager(getContext());
        myChartRecyclerView.setLayoutManager(chartLayoutManager);
        myChartRecyclerView.setItemAnimator(null);
        myChartRecyclerView.setNestedScrollingEnabled(false);

        // 저장된 값이 있는 경우.
        if(currentShowChartCount != 0){
            showChartList = new ArrayList<Chart>();

            if(currentShowChartCount > chartList.size())
                currentShowChartCount = chartList.size();

            if(currentShowChartCount < 2 && chartList.size() >= 2)
                currentShowChartCount = 2;

            for(int i=0; i<currentShowChartCount; i++){
                showChartList.add(chartList.get(i));
            }
            chartAdapter = new ChartAdapter(showChartList);
            myChartRecyclerView.setAdapter(chartAdapter);
            return;
        }

        // 아이템이 3개 이상인 경우는 2개만 보여주자.
        if(chartList.size() > 2){
            currentShowChartCount = 2;
            showChartList.add(chartList.get(0));
            showChartList.add(chartList.get(1));
            chartAdapter = new ChartAdapter(showChartList);
        }
        else{
            currentShowChartCount = chartList.size();
            chartAdapter = new ChartAdapter(chartList);
        }

        myChartRecyclerView.setAdapter(chartAdapter);
    }

    public File convertToFile(ResponseBody responseBody, String imgPath) {
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File img = new File(path, "/" + imgPath);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            int read;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = responseBody.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = responseBody.byteStream();
                outputStream = new FileOutputStream(img);

                while ((read = inputStream.read(fileReader)) != -1) {
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();

                return img;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
        } else {
            getChartListFromServer();
            getPetListFromServer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void imagePermissionCheck() {
        // 현재 안드로이드 버전이 마시멜로 이상인 경우 퍼미션 체크가 추가로 필요함.
        if (Build.VERSION.SDK_INT >= M) {
            // 퍼미션이 없는 경우 퍼미션을 요구해야겠지?
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                // 사용자가 다시 보지 않기에 체크 하지 않고, 퍼미션 체크를 거절한 이력이 있는 경우. (처음 거절한 경우에도 들어감.)
                // 최초 요청시에는 false를 리턴해서 아래 else에 들어간다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
            // 퍼미션이 있는 경우.
            else {
                getPetListFromServer();
            }
        }
        // 버전 낮은거.
        else {
            getPetListFromServer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            Log.d("퍼미션 요구", "퍼미션 요구");
            // 요구하는 퍼미션이 한개이기 때문에 하나만 확인한다.
            // 해당 퍼미션이 승낙된 경우.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("퍼미션 승인", "퍼미션 승인");
                getPetListFromServer();
            }
            // 해당 퍼미션이 거절된 경우.
            else {
                Log.d("퍼미션 거절", "퍼미션 거절");
                Toast.makeText(getContext(), "퍼미션을 승인 해주셔야 알람 이용이 가능합니다", Toast.LENGTH_SHORT).show();
                // 앱 정보 화면을 통해 퍼미션을 다시 요구해보자.
                requestPermissionInSettings();
            }
        }
    }

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

    @OnClick(R.id.showMorePetButton)
    public void showMorePet(){
        if(showMorePetButton.getText().toString().equals("접기")){
            showPetList = new ArrayList<Pet>();
            showPetList.add(petList.get(0));
            showPetList.add(petList.get(1));

            currentShowPetCount = 2;
            showMorePetButton.setText("더 보기");

            petAdapter = new PetAdapter(showPetList);
            myPetRecyclerView.setAdapter(petAdapter);
            return;
        }

        if(currentShowPetCount == petList.size()){
            Toasty.Config.getInstance()
                    .setInfoColor(ContextCompat.getColor(getContext(), android.R.color.black))
                    .apply();
            Toasty.info(getContext(), "더 이상 등록된 반려 동물이 없습니다.", Toast.LENGTH_SHORT, true).show();
            if(petList.size() > 2)
                showMorePetButton.setText("접기");
            return;
        }

        // 현재 보여지고 있는거에서 2개 더 보여줄 수 있다. 그러면 2개 더 보여주자.
        if(petList.size() - currentShowPetCount - SHOW_MORE_COUNT >= 0){
            showPetList.add(petList.get(currentShowPetCount));
            showPetList.add(petList.get(currentShowPetCount + 1));
            currentShowPetCount = currentShowPetCount + 2;
            petAdapter = new PetAdapter(showPetList);
        }
        else{
            petAdapter = new PetAdapter(petList);
            currentShowPetCount = petList.size();
        }
        myPetRecyclerView.setAdapter(petAdapter);
    }

    @OnClick(R.id.showMoreChartButton)
    public void showMoreChart(){
        if(showMoreChartButton.getText().toString().equals("접기")){
            showChartList = new ArrayList<Chart>();
            showChartList.add(chartList.get(0));
            showChartList.add(chartList.get(1));

            currentShowChartCount = 2;
            showMoreChartButton.setText("더 보기");

            chartAdapter = new ChartAdapter(showChartList);
            myChartRecyclerView.setAdapter(chartAdapter);
            return;
        }

        if(currentShowChartCount == chartList.size()){
            Toasty.Config.getInstance()
                    .setInfoColor(ContextCompat.getColor(getContext(), android.R.color.black))
                    .apply();
            Toasty.info(getContext(), "더 이상 작성한 진료 내역이 없습니다.", Toast.LENGTH_SHORT, true).show();
            if(chartList.size() > 2)
                showMoreChartButton.setText("접기");
            return;
        }

        // 현재 보여지고 있는거에서 2개 더 보여줄 수 있다. 그러면 2개 더 보여주자.
        if(chartList.size() - currentShowChartCount - SHOW_MORE_COUNT >= 0){
            showChartList.add(chartList.get(currentShowPetCount));
            showChartList.add(chartList.get(currentShowPetCount + 1));
            currentShowChartCount = currentShowChartCount + 2;
            chartAdapter = new ChartAdapter(showChartList);
        }
        else{
            chartAdapter = new ChartAdapter(chartList);
            currentShowChartCount = chartList.size();
        }
        myChartRecyclerView.setAdapter(chartAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PET_COUNT_TAG, currentShowPetCount);
        outState.putInt(CHART_COUNT_TAG, currentShowChartCount);
    }
}
