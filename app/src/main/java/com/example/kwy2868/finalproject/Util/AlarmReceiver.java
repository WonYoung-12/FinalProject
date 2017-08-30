package com.example.kwy2868.finalproject.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.kwy2868.finalproject.R;

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.noti_icon);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setVibrate(new long[]{1000, 1000, 200, 200})
                .setLights(0xff00ff00, 500, 500);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setAutoCancel(true);

//        Intent alertIntent = new Intent(context, AlertActivity.class);
//        alertIntent.putExtra("Title", title);
//        alertIntent.putExtra("Description", description);
//        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(alertIntent);

        // 전달. 그런데 EventBus는 앱 꺼지면 유지가 안됨..!
//        EventBus.getDefault().post(new AlarmEvent(title, description));

        notificationManager.notify(111, builder.build());
    }
}
