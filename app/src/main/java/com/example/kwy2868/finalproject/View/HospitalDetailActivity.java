package com.example.kwy2868.finalproject.View;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayout;
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
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;
import com.example.kwy2868.finalproject.Util.CustomDialog;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-08.
 */

public class HospitalDetailActivity extends AppCompatActivity
        implements MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener, TextWatcher{
    @BindView(R.id.hospitalName)
    TextView hospitalName;
    @BindView(R.id.hospitalAddress)
    TextView hospitalAddress;
    @BindView(R.id.hospitalTel)
    TextView hospitalTel;

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

    NetworkService networkService = NetworkManager.getNetworkService();

    private Location currentLocation;
    private Double currentLatitude;
    private Double currentLongitude;

    private static final String HOSPITAL_TAG = "Hospital";
    private static final String USER_TAG = "User";
    private static final String LOCATION_TAG = "Location";

    // 현재 보고 있는 화면에 해당하는 병원.
    private Hospital hospital;
    // 현재 사용중인 유저.
    private UserInfo user;
    private MapPoint hospitalMapPoint;

    private boolean isSetLocation = false;

    // 서버에서 받아온 리뷰 리스트들.
    private List<GetReviewResult> reviewList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitaldetail);
        ButterKnife.bind(this);

        dataInit();
        recyclerViewSetting();
        getReviewList();
    }

    public void dataInit(){
        Intent intent = getIntent();
        hospital = Parcels.unwrap(intent.getParcelableExtra(HOSPITAL_TAG));
        user = GlobalData.getUser();
        Log.d("유저", user + " ");
        currentLocation = GlobalData.getCurrentLocation();
        currentLatitude = currentLocation.getLatitude();
        currentLongitude = currentLocation.getLongitude();
//        user = Parcels.unwrap(intent.getParcelableExtra(USER_TAG));
//        currentLocation = intent.getParcelableExtra(LOCATION_TAG);
//
//        currentLatitude = currentLocation.getLatitude();
//        currentLongitude = currentLocation.getLongitude();

        setHospitalDataOnView();
        mapSetting();
    }

    public void setHospitalDataOnView(){
        hospitalName.setText(hospital.getName());
        hospitalAddress.setText(hospital.getAddress());
        hospitalTel.setText(hospital.getTel());

        reviewContent.addTextChangedListener(this);
    }

    public void mapSetting(){
        mapView.setPOIItemEventListener(this);
        // 사용자 위치 받아올 수 있지만 맵 이동은 안됨.
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mapView.setCurrentLocationEventListener(this);
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
        mapView.setMapCenterPoint(hospitalMapPoint, false);
    }

    public void recyclerViewSetting(){
        layoutManager = new StaggeredGridLayoutManager(COLUMN_SPAN, StaggeredGridLayoutManager.VERTICAL);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setHasFixedSize(true);
    }

    // 서버에서 이 병원에 해당하는 리뷰 목록들 가져오자.
    public void getReviewList(){
        Call<List<GetReviewResult>> call = networkService.getReviewList(hospital.getNum());
        call.enqueue(new Callback<List<GetReviewResult>>() {
            @Override
            public void onResponse(Call<List<GetReviewResult>> call, Response<List<GetReviewResult>> response) {
                if(response.isSuccessful()){
                    reviewList = response.body();
                    if(reviewList != null)
                        noReview.setVisibility(View.GONE);
                    refreshRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<List<GetReviewResult>> call, Throwable t) {
//                Toast.makeText(HospitalDetailActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshRecyclerView(){
        // 로딩중인인 카드 보이게 세팅.
        reviewAdapter = new ReviewAdapter();
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.showShimmerAdapter();

        // 1초 후에.
        reviewRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                reviewAdapter = new ReviewAdapter(reviewList);
                reviewRecyclerView.setAdapter(reviewAdapter);
                reviewRecyclerView.hideShimmerAdapter();
            }
        }, 1500);
    }

    @OnClick(R.id.writeReviewButton)
    public void writeReview(){
        // 포커스 없애주자.
        reviewTitle.clearFocus();
        reviewCost.clearFocus();
        reviewContent.clearFocus();

        int hospitalNum = hospital.getNum();
        long userId = user.getUserId();
        String title = reviewTitle.getText().toString();
        if(reviewCost.getText().toString().trim().equals("")){
            Toast.makeText(this, "모두 빠짐없이 입력하여 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        int cost = Integer.parseInt(reviewCost.getText().toString());
        String content = reviewContent.getText().toString();

        long time = System.currentTimeMillis();
        Date tempDate = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(tempDate);

        if(title.trim().equals("") || reviewCost.getText().toString().trim().equals("") || content.trim().equals("")){
            Toast.makeText(this, "모두 빠짐없이 입력하여 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Review review = new Review(hospitalNum, userId, title, cost, content, date);
        sendReviewToServer(review);

        // EditText들 다 지워주자.
        reviewTitle.setText(null);
        reviewCost.setText(null);
        reviewContent.setText(null);
        // 등록하면 후기 등록창 닫아주자.
        expandableLayout.hide();
    }

    public void sendReviewToServer(final Review review){
        Call<WriteResult> call = networkService.writeReview(review);
        call.enqueue(new Callback<WriteResult>() {
            @Override
            public void onResponse(Call<WriteResult> call, Response<WriteResult> response) {
                if(response.isSuccessful()){
                    if(response.body().getResultCode() == 200){
                        Log.d("게시글 번호", response.body().getReviewNum() + "");
                        review.setNum(response.body().getReviewNum());
                        Toast.makeText(HospitalDetailActivity.this, "후기를 정상적으로 등록하였습니다.", Toast.LENGTH_SHORT).show();
                        // 작성했으면 새로 리스트 받아와야지!
                        // 작성한 리뷰도 화면에 나오게 리사이클러뷰 갱신해줘야겠지.
                        getReviewList();
                        if(noReview.getVisibility() == View.VISIBLE)
                            noReview.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<WriteResult> call, Throwable t) {
                Log.d("Retrofit Fail", "서버와 통신 실패");
            }
        });
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    // 마커의 병원 이름 클릭 했을 때 호출.
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        if(isSetLocation){
            CustomDialog customDialog = new CustomDialog(this, currentLatitude, currentLongitude, hospital.getLatitude(), hospital.getLongitude());
            customDialog.show();
        }
        else{
            Toast.makeText(this, "현재 위치를 가져오는 중입니다. 잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        if(!isSetLocation){
            currentLatitude = mapPoint.getMapPointGeoCoord().latitude;
            currentLongitude = mapPoint.getMapPointGeoCoord().longitude;
//            Toast.makeText(this, "다음맵에서 정상적으로 현재 위치 받아옴.", Toast.LENGTH_SHORT).show();
            isSetLocation = !isSetLocation;
        }
        else
            return;
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Toast.makeText(this, "다음맵에서 현재위치 받아오지 못함.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 다른 액티비티 갔다가 오면 (다음 지도 앱 갔다가 오면) 현재 위치 세팅해주도록 하자.
        isSetLocation = false;
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
    public void headerClick(){
        // 레이아웃이 펼쳐져 있으면 헤더의 텍스트 바꿔주자.
        if(expandableLayout.isOpened()) {
            headerText.setText(getString(R.string.header_open));
            expandableLayout.hide();
        }
        else {
            headerText.setText(getString(R.string.header_close));
            expandableLayout.show();
        }
    }
    
    @OnClick(R.id.favoriteButton)
    public void enrollFavorite(){
        Favorite favorite = new Favorite(user.getUserId(), hospital.getNum());
        Call<BaseResult> call = networkService.enrollFavorite(favorite);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    BaseResult baseResult = response.body();
                    if(baseResult.getResultCode() == 200) {
                        Toast.makeText(HospitalDetailActivity.this, "즐겨찾기 추가 성공.", Toast.LENGTH_SHORT).show();
                        Log.d("즐겨찾기", "즐겨찾기 등록 성공");
                    }
                    else if(response.body().getResultCode() == 2000){
                        Toast.makeText(HospitalDetailActivity.this, "즐겨찾기에 이미 추가된 병원입니다.", Toast.LENGTH_SHORT).show();
                        Log.d("Result Code", baseResult.getResultCode() + " ");
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toast.makeText(HospitalDetailActivity.this, "네트워크 문제로 즐겨찾기 추가 실패.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @OnClick(R.id.blackButton)
    public void enrollBlack(){
        Black black = new Black(user.getUserId(), hospital.getNum());
        Call<BaseResult> call = networkService.enrollBlackList(black);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    BaseResult baseResult = response.body();
                    if(baseResult.getResultCode() == 200){
                        Toast.makeText(HospitalDetailActivity.this, "블랙리스트 추가 성공.", Toast.LENGTH_SHORT).show();
                        Log.d("블랙리스트", "블랙리스트 추가");
                    }
                    else if(baseResult.getResultCode() == 2000){
                        Toast.makeText(HospitalDetailActivity.this, "이미 블랙 리스트에 추가된 병원입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                Toast.makeText(HospitalDetailActivity.this, "네트워크 문제로 블랙리스트 추가 실패.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
