package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.example.kwy2868.finalproject.Adapter.PetAdapter;
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
 * Created by kwy2868 on 2017-08-16.
 */

public class MyPetActivity extends AppCompatActivity {

    @BindView(R.id.myPetRecyclerView)
    RecyclerView myPetRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PetAdapter petAdapter;
    private List<Pet> petList;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypet);
        unbinder = ButterKnife.bind(this);
        setTitle("My Pet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    recyclerViewSetting();
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

    public void recyclerViewSetting(){
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        myPetRecyclerView.setLayoutManager(layoutManager);
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
