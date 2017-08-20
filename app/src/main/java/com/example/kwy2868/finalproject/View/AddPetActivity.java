package com.example.kwy2868.finalproject.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.google.gson.Gson;

import org.parceler.Parcels;

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

import static android.os.Build.VERSION_CODES.M;
import static com.example.kwy2868.finalproject.Model.GlobalData.getContext;

/**
 * Created by kwy2868 on 2017-08-12.
 */

public class AddPetActivity extends AppCompatActivity {
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

    private static final int REQUEST_CODE = 0;

    private Uri selectedImage;

    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet);
        unbinder = ButterKnife.bind(this);
        setTitle("My Pet 등록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.petImage)
    public void addPetImage(){
        imagePermissionCheck();
    }

    public void imagePermissionCheck() {
        // 현재 안드로이드 버전이 마시멜로 이상인 경우 퍼미션 체크가 추가로 필요함.
        if (Build.VERSION.SDK_INT >= M) {
            // 퍼미션이 없는 경우 퍼미션을 요구해야겠지?
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                // 사용자가 다시 보지 않기에 체크 하지 않고, 퍼미션 체크를 거절한 이력이 있는 경우. (처음 거절한 경우에도 들어감.)
                // 최초 요청시에는 false를 리턴해서 아래 else에 들어간다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
            // 퍼미션이 있는 경우.
            else {
                selectImage();
            }
        }
        // 버전 낮은거.
        else {
            selectImage();
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
                selectImage();
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

    public void selectImage(){
        Intent photoPickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickIntent.setType("image/*");
        startActivityForResult(photoPickIntent, PHOTO_REQUEST);
    }

    public void requestPermissionInSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
//            checkPermission();
            imageNavigation.setVisibility(View.GONE);

            selectedImage = data.getData();
            Glide.with(this).load(selectedImage)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(this))
                    .into(petImage);
            Log.d("selected Image", selectedImage + " ");

            imagePath = getPath(selectedImage);
            Log.d("Image Path", imagePath);
        }
    }

    @OnClick(R.id.enrollPetButton)
    public void enrollPet(){
        String name = inputPetName.getText().toString();
        int age = Integer.parseInt(inputPetAge.getText().toString());
        String species = inputPetSpecies.getText().toString();
        long userId = GlobalData.getUser().getUserId();
        Log.d("Path", imagePath+"");
        // 이미지 선택 안하면.
        if(imagePath == null || imagePath.trim().equals("")){
            enrollPetWithoutImage(name, age, species, userId);
        }
        // 이미지와 함께 등록하면.
        else{
            enrollPetWithImage(name, age, species, userId);
        }
    }

    public void enrollPetWithoutImage(String name, int age, String species, long userId){
        Pet pet = new Pet(name, age, species, userId, GlobalData.getUser().getFlag());
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.enrollPet(pet);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    if(response.body().getResultCode() == 200) {
                        Toast.makeText(AddPetActivity.this, "펫 등록 성공!!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
    }

    public void enrollPetWithImage(String name, int age, String species, long userId){
        File file = new File(imagePath);

        // 이미지를 보내는 거다.
        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        Log.d("File name", file.getName());
        Log.d("reqFile", reqFile.toString() + " ");
        Log.d("Body", body + " ");

        String json = new Gson().toJson(Parcels.wrap(new Pet(name, age, species, userId, GlobalData.getUser().getFlag())));

        // 텍스트를 보내는거다.
        RequestBody pet = RequestBody.create(MediaType.parse("text/plain"), json);

        NetworkService networkService = NetworkManager.getNetworkService();

        Call<ResponseBody> call = networkService.addPetImage(body, pet);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("레트로핏 옴", "옴");
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.d("레트로핏 안옴", "안옴");
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}