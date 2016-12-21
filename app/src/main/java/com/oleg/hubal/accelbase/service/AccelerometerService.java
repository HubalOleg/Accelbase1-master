package com.oleg.hubal.accelbase.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oleg.hubal.accelbase.async_task.StoreBitmapTask;
import com.oleg.hubal.accelbase.utility.Constants;
import com.oleg.hubal.accelbase.model.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 01.11.2016.
 */

public class AccelerometerService extends Service implements SensorEventListener {
    private static final String TAG = "AccelerometerService";

    private SensorManager mSensorManager = null;
    private Sensor mSensor = null;
    private long accelerometerDelay;

    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private List<Coordinates> mCoordinatesList;
    private long currentTime;

    final Handler loopHandler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerDelay = intent.getLongExtra(Constants.BUNDLE_EDIT_TEXT_DELAY, 1000);

        currentTime = System.currentTimeMillis();
        mCoordinatesList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        startAccelerometerLoop();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loopHandler.removeCallbacksAndMessages(null);
        pushDataToFirebase();
    }

    private void pushDataToFirebase() {
        StoreBitmapTask storeBitmapTask = new StoreBitmapTask(mCoordinatesList, currentTime);
        storeBitmapTask.execute();

        mDatabase.child(user.getUid())
                .child(String.valueOf(currentTime))
                .setValue(mCoordinatesList);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        saveAccelerometerCoorUnit(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startAccelerometerLoop() {
        loopHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                registerListener();
                loopHandler.postDelayed(this, accelerometerDelay);
            }
        }, accelerometerDelay);
    }

    private void saveAccelerometerCoorUnit(SensorEvent event) {
        Long date = System.currentTimeMillis();
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        Log.d(TAG, "saveAccelerometerCoorUnit: " + x + " " + y + " " + z);

        Coordinates coordinates = new Coordinates(date, x, y, z);
        mCoordinatesList.add(coordinates);

        mSensorManager.unregisterListener(this);
    }

    private void registerListener() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
