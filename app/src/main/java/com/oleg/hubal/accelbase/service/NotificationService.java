package com.oleg.hubal.accelbase.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

/**
 * Created by User on 16.01.2017.
 */

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";

    public static final String KEY_NOTIF_ANOTHER_ACTIVITY = "another_activity";
    public static final String KEY_NOTIF_TITLE = "title";
    public static final String KEY_NOTIF_IMAGE = "imageUrl";
    public static final String KEY_NOTIF_BODY = "body";
    public static final String VALUE_TRUE = "true";

    private Intent mIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        mIntent = intent;
        Set<String> keySet = intent.getExtras().keySet();
        for (String s : keySet) {
            Log.d(TAG, "onStartCommand: " + s + " " + intent.getExtras().get(s) + "\n");
        }
        LoadImageTask loadImageTask = new LoadImageTask(intent.getExtras().getString(KEY_NOTIF_IMAGE));
        loadImageTask.execute();
        return(START_STICKY);
    }

    private void sendNotification(Context context, Intent intent, Bitmap image) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(intent.getExtras().getString(KEY_NOTIF_TITLE))
                .setContentText(intent.getExtras().getString(KEY_NOTIF_BODY))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(0, notificationBuilder.build());
    }

    public static Bitmap getBitmapFromUrl(String imageUrl) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {

        String mImageUrl;

        private LoadImageTask(String imageUrl) {
            mImageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL(mImageUrl);
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

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            sendNotification(getApplicationContext(), mIntent, bitmap);
            stopSelf();
        }
    }
}
