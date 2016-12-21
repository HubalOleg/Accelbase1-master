package com.oleg.hubal.accelbase.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 01.11.2016.
 */

public class Coordinates implements Parcelable {

    private double coordinateX;
    private double coordinateY;
    private double coordinateZ;
    private Long date;

    public Coordinates() {

    }

    public Coordinates(Long date, double coordinateX, double coordinateY, double coordinateZ) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.coordinateZ = coordinateZ;
        this.date = date;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public double getCoordinateZ() {
        return coordinateZ;
    }

    public void setCoordinateZ(double coordinateZ) {
        this.coordinateZ = coordinateZ;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    protected Coordinates(Parcel in) {
        coordinateX = in.readDouble();
        coordinateY = in.readDouble();
        coordinateZ = in.readDouble();
        date = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(coordinateX);
        dest.writeDouble(coordinateY);
        dest.writeDouble(coordinateZ);
        if (date == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(date);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Coordinates> CREATOR = new Parcelable.Creator<Coordinates>() {
        @Override
        public Coordinates createFromParcel(Parcel in) {
            return new Coordinates(in);
        }

        @Override
        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };
}
