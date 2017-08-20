package com.example.kwy2868.finalproject.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.kakao.GlobalApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by kwy2868 on 2017-08-13.
 */

public class MyAlarmManager {
    private static Context context = GlobalApplication.getAppContext();

    // 알람매니저 통해서 세팅하자.
    public static void setAlarm() {
        Chart chart;
        List<Chart> chartList = GlobalData.getChartList();

        for (int i = 0; i < chartList.size(); i++) {
            // 여기서 이제 알람을 해주자.
            if (!chartList.get(i).getReTreatmentDate().trim().equals("")) {
                chart = chartList.get(i);
                sendAlarmToReceiver(chart);
                Log.d("재방문 날짜", chart.getReTreatmentDate());
            }
        }
    }

    public static void cancelAlarm(){
        Log.d("알람 취소 호출", "취소해주자");
        Chart chart;
        List<Chart> chartList = GlobalData.getChartList();

        for (int i = 0; i < chartList.size(); i++) {
            // 여기서 이제 알람을 해주자.
            if (!chartList.get(i).getReTreatmentDate().trim().equals("")) {
                chart = chartList.get(i);
                cancelAlarmToReceiver(chart);
                Log.d("재방문 날짜", chart.getReTreatmentDate());
            }
        }
    }

    public static void sendAlarmToReceiver(Chart chart) {
        Log.d("리시버에 보내자", "리시버에 보낸다.");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(GlobalApplication.getAppContext(), AlarmReceiver.class);
        intent.putExtra("Title", chart.getTitle());
        intent.putExtra("Description", chart.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, chart.getNum(), intent, 0);
        Log.d("Chart Number", chart.getNum() + " ");

        Calendar calendar = Calendar.getInstance();
        Log.d("현재 시간", calendar.getTime()+"");
        Log.d("가져온 시간", chart.getReTreatmentDate());
        try {
            if (calendar.getTime().compareTo(sdf.parse(chart.getReTreatmentDate())) < 0) {
                calendar.setTime(sdf.parse(chart.getReTreatmentDate()));
                Log.d("날짜 테스트", "현재보다 알람 울리는게 이후니까 세팅해주자");
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (ParseException e) {
            Log.e("Error", "날짜 변환 오류");
        }
    }

    public static void cancelAlarmToReceiver(Chart chart){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(GlobalApplication.getAppContext(), AlarmReceiver.class);
        intent.putExtra("Title", chart.getTitle());
        intent.putExtra("Description", chart.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, chart.getNum(), intent, 0);
        Log.d("Chart Number", chart.getNum() + " ");

        if(pendingIntent != null){
            Log.d("등록된 알람 있다.", "있으니까 취소하자");
            alarmManager.cancel(pendingIntent);
        }
    }
}
