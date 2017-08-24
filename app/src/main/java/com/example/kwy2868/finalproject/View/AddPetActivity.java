package com.example.kwy2868.finalproject.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.example.kwy2868.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.parceler.Parcels;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION_CODES.M;
import static com.example.kwy2868.finalproject.Model.GlobalData.getContext;

/**
 * Created by kwy2868 on 2017-08-12.
 */

public class AddPetActivity extends BaseActivity implements View.OnKeyListener, TextView.OnEditorActionListener {
    @BindView(R.id.inputPetName)
    EditText inputPetName;
    @BindView(R.id.inputPetAge)
    EditText inputPetAge;
    @BindView(R.id.inputPetSpecies)
    EditText inputPetSpecies;
    @BindView(R.id.petImage)
    CircleImageView petImage;
    @BindView(R.id.imageNavigation)
    TextView imageNavigation;

    @BindView(R.id.enrollLayout)
    LinearLayout enrollLayout;
    @BindView(R.id.modifyLayout)
    LinearLayout modifyLayout;

    @BindView(R.id.enrollPetButton)
    Button enrollPetButton;
    @BindView(R.id.deletePetButton)
    Button deletePetButton;
    @BindView(R.id.modifyPetButton)
    Button modifyPetButton;

    private Unbinder unbinder;
    private static final int PHOTO_REQUEST = 1;
    private static final int READ_REQUEST_CODE = 1;

    private static final int REQUEST_CODE = 0;

    private static final int MODE_ADD = 0;
    private static final int MODE_MODIFY = 1;
    private int mode;

    private Uri selectedImage;

    private String imagePath;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final String PET_TAG = "PET";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpet);
        unbinder = ButterKnife.bind(this);
        setTitle("My Pet 등록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // EditText 예외 처리를 위한 필터 세팅.
        filterSetting();
        storageSetting();

        Intent intent = getIntent();
        if(intent != null){
            setData(intent);
            mode = MODE_MODIFY;
        }
        else{
            mode = MODE_ADD;
        }

        if(mode == MODE_ADD) {
            enrollLayout.setVisibility(View.VISIBLE);
            modifyLayout.setVisibility(View.GONE);

        }
        else{
            enrollLayout.setVisibility(View.GONE);
            modifyLayout.setVisibility(View.VISIBLE);
        }
    }



    @OnClick(R.id.modifyPetButton)
    public void modifyPet(){
        Toast.makeText(this, "수정", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.deletePetButton)
    public void deletePet(){
        Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show();
    }

    public void setData(Intent intent){
        Pet pet = Parcels.unwrap(intent.getParcelableExtra(PET_TAG));
        if(pet != null){
            // 서버에서 받아온 이미지가 있으면.
            if(pet.getImagePath() == null || pet.getImagePath().trim().equals("")){

            }
            else{
                Glide.with(this)
                        .load(pet.getImagePath())
                        .centerCrop()
                        .bitmapTransform(new CenterCrop(this))
                        .into(petImage);
            }
            inputPetName.setText(pet.getName());
            inputPetAge.setText(pet.getAge() + "");
            inputPetSpecies.setText(pet.getSpecies());
        }
    }

    public void filterSetting() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        inputPetName.setFilters(new InputFilter[]{filter});
        inputPetSpecies.setFilters(new InputFilter[]{filter});
        inputPetSpecies.setOnEditorActionListener(this);
    }

    public void storageSetting() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @OnClick(R.id.petImage)
    public void addPetImage() {
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

    public void selectImage() {
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

        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            checkPermission();
            imageNavigation.setVisibility(View.GONE);

            selectedImage = data.getData();
            Glide.with(this).load(selectedImage)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(this))
                    .into(petImage);
            Log.d("selected Image", selectedImage + " ");

            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String path = file.getAbsolutePath();
            Log.d("절대 경로", path);
            imagePath = getPath(selectedImage);
            Log.d("Image Path", imagePath);
        }
    }

    @OnClick(R.id.enrollPetButton)
    public void enrollPet() {
        String name = inputPetName.getText().toString();
        if (name == null || name.trim().equals("")) {
            Toasty.error(this, "이름은 필수 항목입니다.", Toast.LENGTH_SHORT, true).show();
            return;
        }

        int age = 0;
        if (inputPetAge.getText().toString().trim().equals("")) {
            Toasty.error(this, "나이를 올바르게 입력하여 주세요.", Toast.LENGTH_SHORT, true).show();
            return;
        } else {
            age = Integer.parseInt(inputPetAge.getText().toString());
        }

        String species = inputPetSpecies.getText().toString();
        if (species == null || species.trim().equals("")) {
            Toasty.error(this, "종은 필수 항목입니다.", Toast.LENGTH_SHORT, true).show();
            return;
        }

        long userId = GlobalData.getUser().getUserId();

        // 이미지 선택 안하면.
        if (imagePath == null || imagePath.trim().equals("")) {
            enrollPetWithoutImage(name, age, species, userId);
        }
        // 이미지와 함께 등록하면.
        else {
            enrollPetWithImage(name, age, species, userId);
        }
    }

    public void enrollPetWithoutImage(String name, int age, String species, long userId) {
        Pet pet = new Pet(name, age, species, userId, "", GlobalData.getUser().getFlag());
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.enrollPet(pet);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200) {
                        Toasty.success(AddPetActivity.this, "펫 등록 성공", Toast.LENGTH_SHORT, true).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
    }

    public void enrollPetWithImage(final String name, final int age, final String species, final long userId) {
        Toasty.Config.getInstance()
                .setInfoColor(ContextCompat.getColor(this, android.R.color.black))
                .apply();
        Toasty.info(this, "서버에 데이터를 전송합니다.", Toast.LENGTH_SHORT, true).show();
        Toasty.Config.reset();

        Uri file = Uri.fromFile(new File(imagePath));
        StorageReference imgRef = storageReference.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = imgRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            // 이미지 정상적으로 올라갔을 때.
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagePath =  taskSnapshot.getDownloadUrl().toString();
                Pet pet = new Pet(name, age, species, userId, imagePath, GlobalData.getUser().getFlag());
                sendPetDataToServer(pet);
            }
        });
//        File file = new File(imagePath);
        // 이미지를 보내는 거다.
//        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
//        Log.d("File name", file.getName());
//        Log.d("reqFile", reqFile.toString() + " ");
//        Log.d("Body", body + " ");
//
//        String json = new Gson().toJson(Parcels.wrap(new Pet(name, age, species, userId, GlobalData.getUser().getFlag())));
//
//        // 텍스트를 보내는거다.
//        RequestBody pet = RequestBody.create(MediaType.parse("text/plain"), json);

//        NetworkService networkService = NetworkManager.getNetworkService();
//
//        Call<ResponseBody> call = networkService.addPetImage(body, pet);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d("레트로핏 옴", "옴");
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//                Log.d("레트로핏 안옴", "안옴");
//            }
//        });
    }

    public void sendPetDataToServer(Pet pet){
        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.enrollPet(pet);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200) {
                        Toasty.success(AddPetActivity.this, "펫 등록 성공", Toast.LENGTH_SHORT, true).show();
                        finish();
                    }
                    else if(response.body().getResultCode() == 300){
                        Toasty.error(AddPetActivity.this, "이미 등록된 펫입니다.", Toast.LENGTH_SHORT, true).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
    }

    public String getPath(Uri uri) {
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
        } else
            return "";
    }

    public String getRealPath(Uri uri) {
        Cursor cursor = null;
        try {
            Uri newUri = handleImageUri(uri);
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(newUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Uri handleImageUri(Uri uri) {
        if (uri.getPath().contains("content")) {
            Pattern pattern = Pattern.compile("(content://media/.*\\d)");
            Matcher matcher = pattern.matcher(uri.getPath());
            if (matcher.find())
                return Uri.parse(matcher.group(1));
            else
                throw new IllegalArgumentException("Cannot handle this URI");
        }
        return uri;
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

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            hideKeyBoard(view);
            return true;
        }
        return false;

    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_NEXT:
                hideKeyBoard(textView);
                break;
            case EditorInfo.IME_ACTION_DONE:
                textView.clearFocus();
                hideKeyBoard(textView);
            default:
                return false;
        }
        return true;
    }
}