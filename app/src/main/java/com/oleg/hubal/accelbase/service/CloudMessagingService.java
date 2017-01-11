package com.oleg.hubal.accelbase.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.activity.HistoryActivity;
import com.oleg.hubal.accelbase.activity.MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 11.01.2017.
 */

public class CloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "CloudMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Bitmap image = getBitmapfromUrl(remoteMessage.getData().get("imageUrl"));

        sendNotification(remoteMessage, image);
    }

    private void sendNotification(RemoteMessage remoteMessage, Bitmap image) {
        Intent intent;

        if (remoteMessage.getData().get("another_activity").equals("true")) {
            intent = new Intent(CloudMessagingService.this, HistoryActivity.class);
        } else {
            intent = new Intent(CloudMessagingService.this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(CloudMessagingService.this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(CloudMessagingService.this)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image))
                .setColor(getColor(R.color.colorAccent))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
