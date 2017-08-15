package com.example.kwy2868.finalproject.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-12.
 */

public class PetActivity extends AppCompatActivity {
    @BindView(R.id.inputPetName)
    EditText inputPetName;
    @BindView(R.id.inputPetAge)
    EditText inputPetAge;
    @BindView(R.id.inputPetSpecies)
    EditText inputPetSpecies;
    @BindView(R.id.enrollPetButton)
    Button enrollPetButton;
    @BindView(R.id.petImage)
    CircleImageView petImage;
    @BindView(R.id.imageNavigation)
    TextView imageNavigation;

    private Unbinder unbinder;
    private static final int PHOTO_REQUEST = 1;
    private static final int READ_REQUEST_CODE = 1;
    private Uri selectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        unbinder = ButterKnife.bind(this);
        setTitle("My Pet 등록");
    }

    @OnClick(R.id.petImage)
    public void addPetImage(){
        Intent photoPickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickIntent.setType("image/*");
        startActivityForResult(photoPickIntent, PHOTO_REQUEST);
    }

    public void checkPermission(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            Log.i("Permission Denied", "Denied");
            requestPermission();
        }
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_REQUEST_CODE){
            if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Log.i("Permission Denied", "Permission Denied by user");
            }
            else {
                Log.i("Permission Granted", "Permission Granted by user");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            checkPermission();

            imageNavigation.setVisibility(View.GONE);

            selectedImage = data.getData();
            Glide.with(this).load(selectedImage)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(this))
                    .into(petImage);
            Log.d("selected Image", selectedImage + " ");

            String imagePath = getPath(selectedImage);
            Log.d("Image Path", imagePath);
            File file = new File(imagePath);

            // 이미지를 보내는 거다.
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            Log.d("File name", file.getName());
            Log.d("reqFile", reqFile.toString() + " ");
            Log.d("Body", body + " ");

            // 텍스트를 보내는거다.
            RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), "name");
            Log.d("Name", name + " ");

            NetworkService networkService = NetworkManager.getNetworkService();

            Call<ResponseBody> call = networkService.addPetImage(body, name);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("레트로핏 옴", "옴");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("레트로핏 안옴", "안옴");
                }
            });
        }
    }

    public String getPath(Uri uri){
        String filePath = "";
        if (uri.getHost().contains("com.android.providers.media")) {
            // Image pick from recent
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        }
        else
            return "";
    }

    @OnClick(R.id.enrollPetButton)
    public void enrollPet(){
        String name = inputPetName.getText().toString();
        int age = Integer.parseInt(inputPetAge.getText().toString());
        String species = inputPetSpecies.getText().toString();
        long userId = GlobalData.getUser().getUserId();

        Pet pet = new Pet(name, age, species, userId);
        GlobalData.getPetDBHelper().addPet(pet);
        Toast.makeText(this, "펫 등록 성공", Toast.LENGTH_SHORT).show();

        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.enrollPet(pet);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    if(response.body().getResultCode() == 200)
                        Toast.makeText(PetActivity.this, "펫 등록 성공!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
