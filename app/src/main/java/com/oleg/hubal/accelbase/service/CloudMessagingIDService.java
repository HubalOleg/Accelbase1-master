package com.oleg.hubal.accelbase.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by User on 11.01.2017.
 */

public class CloudMessagingIDService extends FirebaseInstanceIdService {

    private static final String TAG = "CloudMessagingIDService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.d(TAG, "onTokenRefresh: " + FirebaseInstanceId.getInstance().getToken());
    }
}
