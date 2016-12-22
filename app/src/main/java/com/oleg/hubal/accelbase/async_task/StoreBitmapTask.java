package com.oleg.hubal.accelbase.async_task;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oleg.hubal.accelbase.model.Coordinates;
import com.oleg.hubal.accelbase.utility.Constants;
import com.oleg.hubal.accelbase.utility.Utility;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 21.12.2016.
 */

public class StoreBitmapTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "StoreBitmapTask";

    private final List<Coordinates> mCoordinatesList;
    private final long mCurrTime;

    private ArrayList<Double> xCoord, yCoord, zCoord;

    private int mWidth = 1000;
    private int mHeight = 1000;

    private int mMinCoordinate;
    private int mMaxCoordinate;
    private int mCoorDifference;

    private int mVerticalUnitSize;
    private int mHorizontalUnitSize;

    private Canvas mCanvas;

    public StoreBitmapTask(List<Coordinates> coordinates, long currTime) {
        mCoordinatesList = coordinates;
        mCurrTime = currTime;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Bitmap bitmap = createBitmapDiagram();
        saveBitmapToStorage(bitmap);

        return null;
    }

    private Bitmap createBitmapDiagram() {
        getCoordinatesData();
        getMinAndMaxCoordinate();
        getUnitSizes();

        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
        bitmap.eraseColor(Color.WHITE);
        mCanvas = new Canvas(bitmap);

        drawGrid();
        drawText();

        drawGraphic(xCoord, Color.GREEN, 0);
        drawGraphic(yCoord, Color.BLUE, 1);
        drawGraphic(zCoord, Color.YELLOW, 2);

        return bitmap;
    }

    private void saveBitmapToStorage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        bitmap.recycle();
        byte[] byteArray = outputStream.toByteArray();

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("images").child(uId).child(String.valueOf(mCurrTime) + ".jpg");

        UploadTask uploadTask = imageRef.putBytes(byteArray);
        uploadTask.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: ");
                    }
                });
    }

    private void getCoordinatesData() {
        ArrayList<String> date = new ArrayList<>();

        xCoord = new ArrayList<>();
        yCoord = new ArrayList<>();
        zCoord = new ArrayList<>();

        for (Coordinates coordinateItem : mCoordinatesList) {
            date.add(Utility.formatDate(coordinateItem.getDate(), Constants.DATE_MINUT_FORMAT));
            xCoord.add(coordinateItem.getCoordinateX());
            yCoord.add(coordinateItem.getCoordinateY());
            zCoord.add(coordinateItem.getCoordinateZ());
        }
    }

    private void getMinAndMaxCoordinate() {
        double min = 0;
        double max = 0;

        for (double value : xCoord) {
            if (max < value)
                max = value;
            if (min > value)
                min = value;
        }


        for (double value : yCoord) {
            if (max < value)
                max = value;
            if (min > value)
                min = value;
        }

        for (double value : zCoord) {
            if (max < value)
                max = value;
            if (min > value)
                min = value;
        }

        mMinCoordinate = (int) min - 1;
        mMaxCoordinate = (int) max + 1;
        mCoorDifference = mMaxCoordinate - mMinCoordinate;
    }

    private void getUnitSizes() {
        mHorizontalUnitSize = mWidth / (mCoordinatesList.size() + 1);
        mVerticalUnitSize = mHeight / (mCoorDifference);
    }

    private void drawGrid() {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        paint.setAlpha(40);
        for (int i = 1; i <= mCoordinatesList.size(); i++) {
            int coorX = mHorizontalUnitSize * i;
            mCanvas.drawLine(coorX, 0, coorX, mHeight, paint);
        }
        for (int i = 1; i < mCoorDifference; i++) {
            int coorY = mVerticalUnitSize * i;
            mCanvas.drawLine(0, coorY, mWidth, coorY, paint);
        }
    }

    private void drawText() {
        int coordinate = mMinCoordinate + 1;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(mVerticalUnitSize / 2);
        paint.setAlpha(150);
        for (int i = 1; i < mCoorDifference; i++, coordinate++) {
            mCanvas.drawText(String.valueOf(coordinate), 10, i * mVerticalUnitSize, paint);
        }
    }

    private void drawGraphic(List<Double> coordinates, int color, int id) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(color);

        Path path = new Path();
        float startHorizontal = mHorizontalUnitSize;
        float startVertical = (float) ((coordinates.get(0) + Math.abs(mMinCoordinate)) * mVerticalUnitSize);

        path.moveTo(startHorizontal, startVertical);
        mCanvas.drawCircle(startHorizontal, startVertical, 8, paint);

        for (int i = 1; i < coordinates.size(); i++) {
            float x = mHorizontalUnitSize * (i + 1);
            float y = (float) (mVerticalUnitSize * (coordinates.get(i) + Math.abs(mMinCoordinate)));
            path.lineTo(x, y);
            mCanvas.drawCircle(x, y, 8, paint);
        }

        mCanvas.drawPath(path, paint);
    }
}
