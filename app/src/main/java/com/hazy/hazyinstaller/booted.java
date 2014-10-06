package com.hazy.hazyinstaller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.util.Log;
import android.widget.Toast;

public class booted extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.w(TAG, "Starting service...");
        context.startService(new Intent(context, UpdateService.class));
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 1, intent, 0);

        Bitmap banner = BitmapFactory.decodeResource(context.getResources(), R.drawable.wallsette_);

        Notification n = new Notification.Builder(context)
                        .setContentTitle("Service started.")
                        .setContentText("HazyInstaller will check updates every 8 hours.")
                        .setSmallIcon(R.drawable.ic_action_accept)
                        .setStyle(new Notification.BigPictureStyle()
                                .bigPicture(banner))
                        .setContentIntent(pIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, n);
    }
}
