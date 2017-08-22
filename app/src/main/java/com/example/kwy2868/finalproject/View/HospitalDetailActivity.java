package com.example.kwy2868.finalproject.View;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.kwy2868.finalproject.Adapter.ReviewAdapter;
import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.Black;
import com.example.kwy2868.finalproject.Model.Favorite;
import com.example.kwy2868.finalproject.Model.GetReviewResult;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.Model.Review;
import com.example.kwy2868.finalproject.Model.UserInfo;
import com.example.kwy2868.finalproject.Model.WriteResult;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.LocationHelper;
import com.example.kwy2868.finalproject.Util.NavigationDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION_CODES.M;
import static com.example.kwy2868.finalproject.Model.GlobalData.getContext;

/**
 * Created by kwy2868 on 2017-08-08.
 */

public class HospitalDetailActivity extends AppCompatActivity
        implements MapView.POIItemEventListener, MapView.MapViewEventListener, TextWatcher
        , RatingBar.OnRatingBarChangeListener, LocationHelper {
    @BindView(R.id.detailHospitalImage)
    ImageView hospitalImage;
    @BindView(R.id.hospitalName)
    TextView hospitalName;
    @BindView(R.id.hospitalAddress)
    TextView hospitalAddress;
    @BindView(R.id.hospitalTel)
    TextView hospitalTel;
    @BindView(R.id.hospitalSpecies)
    TextView hospitalSpecies;

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.reviewTitle)
    EditText reviewTitle;
    @BindView(R.id.reviewCost)
    EditText reviewCost;
    @BindView(R.id.textCount)
    TextView textCount;
    @BindView(R.id.reviewContent)
    EditText reviewContent;
    @BindView(R.id.writeReviewButton)
    Button writeReviewButton;

    @BindView(R.id.reviewRecyclerView)
    ShimmerRecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ReviewAdapter reviewAdapter;
    private static final int COLUMN_SPAN = 2;
    private static final int REQUEST_CODE = 0;

    @BindView(R.id.noReview)
    TextView noReview;

    @BindView(R.id.expandableLayout)
    ExpandableLayout expandableLayout;
    @BindView(R.id.expandableHeader)
    RelativeLayout expandableHeader;
    @BindView(R.id.headerText)
    TextView headerText;

    ////////////Floating Action Button//////////
    @BindView(R.id.floatingActionsMenu)
    FloatingActionsMenu floatingActionsMenu;
    @BindView(R.id.favoriteButton)
    FloatingActionButton favoriteButton;
    @BindView(R.id.blackButton)
    FloatingActionButton blackButton;
    @BindView(R.id.ratingButton)
    FloatingActionButton ratingButton;

    private static final String FAVORITE_ADD = "즐겨찾기 등록";
    private static final String FAVORITE_DELETE = "즐겨찾기 해제";
    private static final String BLACK_ADD = "블랙 리스트 등록";
    private static final String BLACK_DELETE = "블랙 리스트 해제";

    private NetworkService networkService = NetworkManager.getNetworkService();

    private Location currentLocation;
    private Double currentLatitude;
    private Double currentLongitude;

    private static final String HOSPITAL_TAG = "Hospital";

    // 현재 위치 값을 이미 갖고 있는지.
    private boolean isSetLocation = false;

    // 현재 보고 있는 화면에 해당하는 병원.
    private Hospital hospital;
    // 현재 사용중인 유저.
    private UserInfo user;
    private MapPoint hospitalMapPoint;

    private LocationManager locationManager;
    private LocationListener locationListener;

    // 서버에서 받아온 리뷰 리스트들.
    private List<GetReviewResult> reviewList;

    // 진료 동물들.
    String species;

    // 사용자가 평가한 점수.
    private float score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitaldetail);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkLocation();
        dataInit();
        recyclerViewSetting();
        getReviewList();
        // 보고 있는 병원이 즐겨찾기 또는 블랙리스트에 추가 되어 있는지 확인.
        checkAdded();
    }

    public void checkLocation() {
        Location location = GlobalData.getCurrentLocation();
        // 이전 액티비티에서 현재 위치가 설정 되어 있는 경우.
        if (location != null) {
            isSetLocation = true;
            currentLocation = location;
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }
    }

    public void dataInit() {
        Intent intent = getIntent();
        hospital = Parcels.unwrap(intent.getParcelableExtra(HOSPITAL_TAG));
        setTitle(hospital.getName());
        user = GlobalData.getUser();
        Log.d("유저", user + " ");
        species = hospital.getSpecies();

        setHospitalDataOnView();
        mapSetting();
    }

    public void setHospitalDataOnView() {
        Log.d("이미지 패스", hospital.getImgPath() + "");
        if (hospital.getImgPath() == null || hospital.getImgPath().trim().equals("")) {
            Glide.with(this).load(R.drawable.imgready)
                    .centerCrop().bitmapTransform(new CenterCrop(this))
                    .into(hospitalImage);
        } else {
            Glide.with(this).load(hospital.getImgPath())
                    .centerCrop().bitmapTransform(new CenterCrop(this))
                    .into(hospitalImage);
        }

        hospitalName.setText(hospital.getName());
        hospitalAddress.setText(hospital.getAddress());
        hospitalTel.setText(hospital.getTel());

        if (species == null || species.trim().equals("")) {
            hospitalSpecies.setVisibility(View.GONE);
        } else {
            hospitalSpecies.setVisibility(View.VISIBLE);
            hospitalSpecies.setText(species);
//            for (int i = 0; i < speciesList.size(); i++) {
//                if (i == 0)
//                    hospitalSpecies.setText(speciesList.get(i));
//                else {
//                    hospitalSpecies.append(", " + speciesList.get(i));
//                }
//            }
        }

        reviewContent.addTextChangedListener(this);
    }

    public void mapSetting() {
        mapView.setPOIItemEventListener(this);
        // 사용자 위치 받아올 수 있지만 맵 이동은 안됨.
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mapView.setMapViewEventListener(this);

        hospitalMapPoint = MapPoint.mapPointWithGeoCoord(hospital.getLatitude(), hospital.getLongitude());

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(hospital.getName());
        marker.setMapPoint(hospitalMapPoint);

        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(marker);
        mapView.selectPOIItem(marker, true);
        // 숫자가 낮을수록 좁게, 숫자가 높을수록 넓은 지역까지 보인다.
        // 이렇게 하면 드래그 막히는거 아닌가..?
        mapView.setMapCenterPoint(hospitalMapPoint, true);
    }

    public void recyclerViewSetting() {
        layoutManager = new StaggeredGridLayoutManager(COLUMN_SPAN, StaggeredGridLayoutManager.VERTICAL);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewAdapter = new ReviewAdapter();
        reviewRecyclerView.setAdapter(reviewAdapter);
    }

    // 서버에서 이 병원에 해당하는 리뷰 목록들 가져오자.
    public void getReviewList() {
        Call<List<GetReviewResult>> call = networkService.getReviewList(hospital.getNum());
        call.enqueue(new Callback<List<GetReviewResult>>() {
            @Override
            public void onResponse(Call<List<GetReviewResult>> call, Response<List<GetReviewResult>> response) {
                if (response.isSuccessful()) {
                    reviewList = response.body();
                    // 받아온 리스트가 있으면.
                    if (reviewList.size() > 0) {
                        refreshRecyclerView();
                    } else {
                        reviewRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reviewRecyclerView.hideShimmerAdapter();
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GetReviewResult>> call, Throwable t) {
//                Toast.makeText(HospitalDetailActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkAdded() {
        Call<BaseResult> favoriteCheck = networkService.checkAddedFavorite(hospital.getNum(), user.getUserId(), user.getFlag());
        favoriteCheck.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    // 이미 추가 되어 있는 병원.
                    if (response.body().getResultCode() == 200) {
                        favoriteButton.setTitle(FAVORITE_DELETE);
                    }
                    // 추가 되어 있지 않은 병원.
                    else if (response.body().getResultCode() == 300) {
                        favoriteButton.setTitle(FAVORITE_ADD);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
            }
        });

        Call<BaseResult> blackCheck = networkService.checkAddedBlack(hospital.getNum(), user.getUserId(), user.getFlag());
        blackCheck.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200) {
                        blackButton.setTitle(BLACK_DELETE);
                    }
                    // 추가 되어 있지 않은 병원.
                    else if (response.body().getResultCode() == 300) {
                        blackButton.setTitle(BLACK_ADD);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void refreshRecyclerView() {
        // 로딩중인인 카드 보이게 세팅.
//        reviewAdapter = new ReviewAdapter(reviewList);
        reviewAdapter.notifyDataSetChanged();
        reviewRecyclerView.showShimmerAdapter();

        // 1초 후에.
        reviewRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                reviewAdapter = new ReviewAdapter(reviewList);
                reviewRecyclerView.setAdapter(reviewAdapter);
                reviewRecyclerView.hideShimmerAdapter();
                noReview.setVisibility(View.GONE);
            }
        }, 1000);
    }

    @OnClick(R.id.writeReviewButton)
    public void writeReview() {
        // 포커스 없애주자.
        reviewTitle.clearFocus();
        reviewCost.clearFocus();
        reviewContent.clearFocus();

        int hospitalNum = hospital.getNum();
        long userId = user.getUserId();
        String title = reviewTitle.getText().toString();

        if (title == null || title.trim().equals("")) {
            Toasty.error(HospitalDetailActivity.this, "제목을 바르게 입력하여 주세요.", Toast.LENGTH_SHORT, true).show();
            return;
        }

        if (reviewCost.getText() == null || reviewCost.getText().toString().trim().equals("")) {
            Toasty.error(HospitalDetailActivity.this, "금액을 바르게 입력하여 주세요.", Toast.LENGTH_SHORT, true).show();
            return;
        }
        int cost = Integer.parseInt(reviewCost.getText().toString());

        String content = reviewContent.getText().toString();
        if (content == null || content.trim().equals("")) {
            Toasty.error(HospitalDetailActivity.this, "내용을 바르게 입력하여 주세요.", Toast.LENGTH_SHORT, true).show();
            return;
        }

        long time = System.currentTimeMillis();
        Date tempDate = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(tempDate);

        Review review = new Review(hospitalNum, userId, title, cost, content, date);

        sendReviewToServer(review);

        // EditText들 다 지워주자.
        reviewTitle.setText(null);
        reviewCost.setText(null);
        reviewContent.setText(null);
        // 등록하면 후기 등록창 닫아주자.
        expandableLayout.hide();
    }

    public void sendReviewToServer(final Review review) {
        Call<WriteResult> call = networkService.writeReview(review);
        call.enqueue(new Callback<WriteResult>() {
            @Override
            public void onResponse(Call<WriteResult> call, Response<WriteResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200) {
                        Log.d("게시글 번호", response.body().getReviewNum() + "");
                        review.setNum(response.body().getReviewNum());
                        Toasty.success(HospitalDetailActivity.this, "후기를 정상적으로 등록하였습니다.", Toast.LENGTH_SHORT, true).show();
                        // 작성했으면 새로 리스트 받아와야지!
                        // 작성한 리뷰도 화면에 나오게 리사이클러뷰 갱신해줘야겠지.
                        getReviewList();
                        if (noReview.getVisibility() == View.VISIBLE)
                            noReview.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<WriteResult> call, Throwable t) {
                Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    // 마커의 병원 이름 클릭 했을 때 호출.
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        if (isSetLocation) {
            NavigationDialog navigationDialog = new NavigationDialog(this, currentLatitude, currentLongitude, hospital.getLatitude(), hospital.getLongitude());
            navigationDialog.show();
        } else {
            permissionCheck();
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                    "현재 위치를 받아옵니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 다른 액티비티 갔다가 오면 (다음 지도 앱 갔다가 오면) 현재 위치 세팅해주도록 하자.
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    // 드래그 끝났을 때. 다시 원래 위치로 가게 해주자.
    // 근데 다음 맵 드레그 안되게 하고 싶은데 소개가 잘 안되어있음..! 드래그 끝나도 이상하고.. 흠...
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        mapView.setMapCenterPoint(hospitalMapPoint, false);
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        textCount.setText("글자수 : " + String.valueOf(charSequence.length()) + "/300");
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @OnClick(R.id.expandableHeader)
    public void headerClick() {
        // 레이아웃이 펼쳐져 있으면 헤더의 텍스트 바꿔주자.
        if (expandableLayout.isOpened()) {
            headerText.setText(getString(R.string.header_open));
            expandableLayout.hide();
        } else {
            headerText.setText(getString(R.string.header_close));
            expandableLayout.show();
        }
    }

    @OnClick(R.id.favoriteButton)
    public void enrollFavorite() {
        Favorite favorite = new Favorite(hospital.getNum(), user.getUserId(), user.getFlag());
        // 즐겨찾기가 등록이 되어 있지 않은 상태면 정상적으로 추가해 주자.
        if (favoriteButton.getTitle().equals(FAVORITE_ADD)) {
            Call<BaseResult> call = networkService.enrollFavorite(favorite);
            call.enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                    if (response.isSuccessful()) {
                        BaseResult baseResult = response.body();
                        if (baseResult.getResultCode() == 200) {
                            Toasty.success(HospitalDetailActivity.this, "즐겨찾기 추가 성공.", Toast.LENGTH_SHORT, true).show();
                            favoriteButton.setTitle(FAVORITE_DELETE);
                            Log.d("즐겨찾기", "즐겨찾기 등록 성공");
                        }
                        //
                        else if (baseResult.getResultCode() == 300) {
                            Toasty.error(HospitalDetailActivity.this, "블랙 리스트에 추가된 병원입니다. 블랙리스트에서 해제 후에 추가 해주세요.", Toast.LENGTH_SHORT, true).show();
                        }
                        // 여기 이제 안들어갈듯.
                        else if (response.body().getResultCode() == 2000) {
                            Toast.makeText(HospitalDetailActivity.this, "즐겨찾기에 이미 추가된 병원입니다.", Toast.LENGTH_SHORT).show();
                            Log.d("Result Code", baseResult.getResultCode() + " ");
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {
                    Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
                }
            });
        }
        // TODO 즐겨찾기 해제해주자.
        else {
            showDeleteFavoriteDialog(favorite);
        }
    }

    public void showDeleteFavoriteDialog(final Favorite favorite) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("즐겨찾기 해제")
                .setMessage("즐겨찾기에서 삭제하시겠습니까?")
                .setCancelable(true)
                // 여기서 서버에 보내서 삭제해주자.
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFavorite(favorite);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteFavorite(Favorite favorite) {
        Call<BaseResult> call = networkService.deleteFavorite(favorite.getNum(), favorite.getUserId(), favorite.getFlag());
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200) {
                        Toasty.success(HospitalDetailActivity.this, "정상적으로 삭제되었습니다", Toast.LENGTH_SHORT, true).show();
                        favoriteButton.setTitle(FAVORITE_ADD);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @OnClick(R.id.blackButton)
    public void enrollBlack() {
        Black black = new Black(hospital.getNum(), user.getUserId(), user.getFlag());
        // 블랙 리스트 추가 되어 있지 않은 상태이면 눌렀을 때 추가해주자.
        if (blackButton.getTitle().equals(BLACK_ADD)) {
            Call<BaseResult> call = networkService.enrollBlack(black);
            call.enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                    if (response.isSuccessful()) {
                        BaseResult baseResult = response.body();
                        if (baseResult.getResultCode() == 200) {
                            Toasty.success(HospitalDetailActivity.this, "블랙리스트 추가 성공.", Toast.LENGTH_SHORT, true).show();
                            blackButton.setTitle("블랙리스트 해제");
                            Log.d("블랙리스트", "블랙리스트 추가");
                        }
                        // 여기 이제 안들어가겠지.
                        else if (baseResult.getResultCode() == 300) {
                            Toasty.error(HospitalDetailActivity.this, "즐겨 찾기에 추가된 병원입니다. 즐겨 찾기에서 해제 후에 추가 해주세요.", Toast.LENGTH_SHORT, true).show();
                        }
                        // 여기도 이제 안들어가겠지.
                        else if (baseResult.getResultCode() == 2000) {
                            Toast.makeText(HospitalDetailActivity.this, "이미 블랙 리스트에 추가된 병원입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {
                    Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
                }
            });
        }
        // 블랙리스트 해제해주자.
        else {
            showDeleteBlackDialog(black);
        }
    }

    public void showDeleteBlackDialog(final Black black) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블랙리스트 해제")
                .setMessage("블랙리스트에서 해제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteBlack(black);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteBlack(final Black black) {
        Call<BaseResult> call = networkService.deleteBlack(black.getNum(), black.getUserId(), black.getFlag());
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResultCode() == 200) {
                        Toasty.success(HospitalDetailActivity.this, "정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT, true).show();
                        blackButton.setTitle(BLACK_ADD);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @OnClick(R.id.ratingButton)
    public void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("별점 평가");
        View mView = getLayoutInflater().inflate(R.layout.rating_bar, null);
        builder.setView(mView);

        RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(this);

        // 평가 완료 했을 때.
        // 서버에 전송해주자.
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call<BaseResult> call = networkService.ratingHospital(hospital.getNum(), score);
                call.enqueue(new Callback<BaseResult>() {
                    @Override
                    public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getResultCode() == 200) {
                                Log.d("평가 완료", "평가 완료");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResult> call, Throwable t) {
                        Toasty.error(HospitalDetailActivity.this, "네트워크 오류입니다.", Toast.LENGTH_SHORT, true).show();
                    }
                });
                Log.d("별점 평가", "서버에 전송해주자" + score);
            }
        });

        // 취소 했을 때.
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        score = rating;
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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
                Toasty.error(getContext(), "현재 위치를 받아오는데 실패 했습니다.", Toast.LENGTH_SHORT, true).show();
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentLocation = location;
        GlobalData.setCurrentLocation(currentLocation);
        isSetLocation = true;
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        makeDialog();
    }

    public void makeDialog() {
//        DialogPlus dialogPlus = DialogPlus.newDialog(this)
//                .setAdapter(new DialogAdapter(this))
//                .setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//
//                    }
//                })
//                .setExpanded(true)
//                .setGravity(Gravity.CENTER)
//                .setCancelable(true)
//                .create();
//        dialogPlus.show();
        NavigationDialog navigationDialog = new NavigationDialog(this, currentLatitude, currentLongitude, hospital.getLatitude(), hospital.getLongitude());
        if (!this.isFinishing())
            navigationDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestPermission 메소드의 requestCode와 일치하는지 확인.
        if (requestCode == REQUEST_CODE) {
            Log.d("퍼미션 요구", "퍼미션 요구");
            // 요구하는 퍼미션이 한개이기 때문에 하나만 확인한다.
            // 해당 퍼미션이 승낙된 경우.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
