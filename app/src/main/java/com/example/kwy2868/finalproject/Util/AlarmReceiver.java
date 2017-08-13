package com.example.kwy2868.finalproject.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_favorite);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);

//        Log.d("Alerter", "Alerter");
//        Alerter.create((Activity)GlobalApplication.getAppContext())
//                .setTitle(title)
//                .setText(description)
//                .show();

        notificationManager.notify(111, builder.build());
    }
}
