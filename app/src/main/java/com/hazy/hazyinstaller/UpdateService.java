package com.hazy.hazyinstaller;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public  class UpdateService extends IntentService {

    public UpdateService() {
        super("LogService");
    }

    @Override
    protected void onHandleIntent(Intent i) {

        Bitmap banner = BitmapFactory.decodeResource(getResources(), R.drawable.banner);
        while (true) {
                MainActivity mainActivity = new MainActivity();
                try { mainActivity.stuffs(); } catch (Exception e) {}

                if (mainActivity.newUpdate == true && mainActivity.hasHazyROM == true) {
                            Intent intent = new Intent(getApplication(), updateActivity.class);
                            PendingIntent pIntent = PendingIntent.getActivity(getApplication(), 0, intent, 0);

                            Notification n = new Notification.Builder(getApplication())
                                    .setContentTitle("HazyInstaller")
                                    .setContentText("New Update Available")
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.refresh)
                                    .setStyle(new Notification.BigPictureStyle()
                                            .bigPicture(banner))
                                    .setContentIntent(pIntent).build();


                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                            notificationManager.notify(0, n);
                        }
                        Log.d("CHECK FOR UPDATES:", "...");
                        try {
                            Thread.sleep(2880000); //8 hours = 28800000ms
                        } catch (InterruptedException e) {}
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SERVICE", "Destroying Service");
    }
}
