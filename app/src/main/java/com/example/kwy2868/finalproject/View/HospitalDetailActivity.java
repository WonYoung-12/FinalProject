package com.example.kwy2868.finalproject.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Model.Hospital;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Util.CustomDialog;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kwy2868 on 2017-08-08.
 */

public class HospitalDetailActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener{
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
    @BindView(R.id.reviewContent)
    EditText reviewContent;
    @BindView(R.id.writeReviewButton)
    Button writeReviewButton;

    private Double currentLatitude;
    private Double currentLongitude;

    private static final String HOSPITAL_TAG = "Hospital";

    private Hospital hospital;
    private MapPoint hospitalMapPoint;

    private boolean isSetLocation = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitaldetail);
        ButterKnife.bind(this);

        getHospital();
    }

    public void getHospital(){
        Intent intent = getIntent();
        hospital = Parcels.unwrap(intent.getParcelableExtra(HOSPITAL_TAG));
        setDataOnView();
        mapSetting();
    }

    public void setDataOnView(){
        hospitalName.setText(hospital.getName());
        hospitalAddress.setText(hospital.getAddress());
        hospitalTel.setText(hospital.getTel());
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

    // TODO 8월 9일은 여기부터 하자.
    @OnClick(R.id.writeReviewButton)
    public void writeReview(){
        int num = hospital.getNum();
        String title = reviewTitle.getText().toString();
        String cost = reviewCost.getText().toString();
        String content = reviewContent.getText().toString();
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
            Toast.makeText(this, "다음맵에서 정상적으로 현재 위치 받아옴.", Toast.LENGTH_SHORT).show();
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
}
