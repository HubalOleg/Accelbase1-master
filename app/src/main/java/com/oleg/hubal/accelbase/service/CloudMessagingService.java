package com.oleg.hubal.accelbase.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oleg.hubal.accelbase.R;
import com.oleg.hubal.accelbase.activity.HistoryActivity;
import com.oleg.hubal.accelbase.activity.MainActivity;
import com.oleg.hubal.accelbase.utility.Utility;

import java.util.Map;
import java.util.Set;

/**
 * Created by User on 11.01.2017.
 */

public class CloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "CloudMessagingService";
    public static final String KEY_NOTIF_ANOTHER_ACTIVITY = "another_activity";
    public static final String KEY_NOTIF_TITLE = "title";
    public static final String KEY_NOTIF_IMAGE = "imageUrl";
    public static final String KEY_NOTIF_BODY = "body";
    public static final String VALUE_TRUE = "true";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> map = remoteMessage.getData();
        Set<String> set = map.keySet();
        for (String s : set) {
            Log.d(TAG, "onMessageReceived: " + map.get(s));
        }

        Bitmap image = Utility.getBitmapFromUrl(remoteMessage.getData().get(KEY_NOTIF_IMAGE));
//        sendNotification(remoteMessage, image);
    }

    private void sendNotification(RemoteMessage remoteMessage, Bitmap image) {
        Intent intent;

        if (remoteMessage.getData().get(KEY_NOTIF_ANOTHER_ACTIVITY).equals(VALUE_TRUE)) {
            intent = new Intent(CloudMessagingService.this, HistoryActivity.class);
        } else {
            intent = new Intent(CloudMessagingService.this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(CloudMessagingService.this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(CloudMessagingService.this)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(remoteMessage.getData().get(KEY_NOTIF_TITLE))
                .setContentText(remoteMessage.getData().get(KEY_NOTIF_BODY))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image))
                .setColor(getColor(R.color.colorAccent))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}
