package com.oleg.hubal.accelbase.async_task;

import android.os.AsyncTask;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oleg.hubal.accelbase.model.Coordinates;
import com.oleg.hubal.accelbase.utility.Constants;

import java.util.List;

/**
 * Created by User on 21.12.2016.
 */

public class StoreBitmapTask extends AsyncTask<Void, Void, Void> {

    private final List<Coordinates> mCoordinatesList;
    private final long mCurrTime;

    public StoreBitmapTask(List<Coordinates> coordinates, long currTime) {
        mCoordinatesList = coordinates;
        mCurrTime = currTime;
    }

    @Override
    protected Void doInBackground(Void... params) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_REFERENCE);
        StorageReference imageRef = storageReference.child("images").child(String.valueOf(mCurrTime));

        return null;
    }
}
