package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kwy2868.finalproject.Adapter.ChartAdapter;
import com.example.kwy2868.finalproject.Adapter.PetAdapter;
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-17.
 */

public class MyInfoFragment extends Fragment {
    @BindView(R.id.myPetRecyclerView)
    RecyclerView myPetRecyclerView;
    @BindView(R.id.myChartRecyclerView)
    RecyclerView myChartRecyclerView;

    private RecyclerView.LayoutManager petLayoutManager;
    private PetAdapter petAdapter;
    private List<Pet> petList;

    private RecyclerView.LayoutManager chartLayoutManager;
    private ChartAdapter chartAdapter;
    private List<Chart> chartList;

    private static final int COLUMN_SPAN = 2;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myinfo, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPetListFromServer();
    }

    public void getPetListFromServer(){
        Log.d("펫 리스트 가져온다", "가져온다");
        final NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Pet>> call = networkService.getPetList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if(response.isSuccessful()){
                    petList = response.body();
                    GlobalData.setPetList(petList);
                    System.out.println("이미지들");
                    petRecyclerViewSetting();
                    for(int i=0; i<petList.size(); i++){
                        final Pet pet = petList.get(i);
                        // 등록된 경로가 있으면 이미지 받아오자.
                        if(!(pet.getImagePath() == null || pet.getImagePath().trim().equals(""))){
                            Call<ResponseBody> imageCall = networkService.getPetImage(pet.getImagePath());
                            Log.i("이미지 세팅", "세팅 해주자");
                            final int position = i;

                            imageCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()){
                                        File imgFile = convertToFile(response.body(), pet.getImagePath());
                                        pet.setImgFile(imgFile);
                                        petAdapter.notifyDataSetChanged();
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

    public void petRecyclerViewSetting(){
        petLayoutManager= new StaggeredGridLayoutManager(COLUMN_SPAN, StaggeredGridLayoutManager.VERTICAL);
        myPetRecyclerView.setLayoutManager(petLayoutManager);
        myPetRecyclerView.setItemAnimator(null);
        petAdapter = new PetAdapter(petList);
        myPetRecyclerView.setAdapter(petAdapter);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
