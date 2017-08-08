package com.example.kwy2868.finalproject.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwy2868.finalproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kwy2868 on 2017-07-26.
 */

public class CustomDialog extends Dialog {
    @BindView(R.id.dialogCar)
    TextView dialogCar;
    @BindView(R.id.dialogPublic)
    TextView dialogPublic;
    @BindView(R.id.dialogFoot)
    TextView dialogFoot;

    private Double startLatitude;
    private Double startLongitude;

    private Double endLatitude;
    private Double endLongitude;

    private static final String BASE_URI = "daummaps://route?";
    private static final String MARKET_URI = "market://details?id=net.daum.android.map";

    public CustomDialog(@NonNull Context context, Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude) {
        super(context);
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;

        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.dialogCar)
    public void routeByCar(){
        Toast.makeText(getContext(), "차로", Toast.LENGTH_SHORT).show();
        String uri = BASE_URI + "sp=" + startLatitude + "," + startLongitude + "&ep=" + endLatitude + "," + endLongitude + "&by=CAR";
        startKakaoMap(uri);
    }

    @OnClick(R.id.dialogPublic)
    public void routeByPublic(){
        Toast.makeText(getContext(), "대중교통으로", Toast.LENGTH_SHORT).show();
        String uri = BASE_URI + "sp=" + startLatitude + "," + startLongitude + "&ep=" + endLatitude + "," + endLongitude + "&by=PUBLICTRANSIT";
        startKakaoMap(uri);
    }

    @OnClick(R.id.dialogFoot)
    public void routeByFoot(){
        Toast.makeText(getContext(), "도보로", Toast.LENGTH_SHORT).show();
        String uri = BASE_URI + "sp=" + startLatitude + "," + startLongitude + "&ep=" + endLatitude + "," + endLongitude + "&by=FOOT";
        startKakaoMap(uri);
    }

    public static void showDialog(Context context, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setMessage(message);
        alert.show();
    }

    public void startKakaoMap(String uri){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            getContext().startActivity(intent);
        }
        // 지도 앱이 없는 경우.
        catch(ActivityNotFoundException e){
            Toast.makeText(getContext(), "다음 지도 앱 설치를 위해 마켓으로 이동합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI));
            getContext().startActivity(intent);
        }
        catch(Exception e){
            Toast.makeText(getContext(), "해당 목적지에 대한 길 찾기를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
