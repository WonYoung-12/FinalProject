package com.example.kwy2868.finalproject.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kwy2868.finalproject.Model.AlarmEvent;
import com.example.kwy2868.finalproject.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by kwy2868 on 2017-08-13.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("리시버 받았다.", "리시버 받았다.");

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = intent.getStringExtra("Title");
        String description = intent.getStringExtra("Description");

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_favorite);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setVibrate(new long[]{1000, 1000, 200, 200})
                .setLights(0xff00ff00, 500, 500);
        builder.setAutoCancel(true);

        // TODO 여기서 액티비티에 대한 정보를 받고 싶은데 자꾸 Casting 오류난다 context는 receiverrestricted? 이상한거 나옴.
        // 전달.
        EventBus.getDefault().post(new AlarmEvent(title, description));

//        notificationManager.notify(111, builder.build());
    }
}
