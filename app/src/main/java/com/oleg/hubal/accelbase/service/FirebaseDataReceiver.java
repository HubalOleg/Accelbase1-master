package com.oleg.hubal.accelbase.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by User on 16.01.2017.
 */

public class FirebaseDataReceiver extends BroadcastReceiver {

    private final String TAG = "FirebaseDataReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtras(intent);
        context.startService(serviceIntent);
        Log.d(TAG, "onRecceive: " + intent.getExtras().getString("title"));
    }


}
