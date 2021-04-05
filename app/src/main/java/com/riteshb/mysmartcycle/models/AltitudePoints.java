package com.riteshb.mysmartcycle.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AltitudePoints implements Parcelable {

    boolean Key;
    boolean LockDest;
    boolean LockAlt;
    double Alt;
    double Lng;
    double Lat;
    double Distance;

    protected AltitudePoints(Parcel in) {
        Key = in.readByte() != 0;
        LockDest = in.readByte() != 0;
        LockAlt = in.readByte() != 0;
        Alt = in.readDouble();
        Lng = in.readDouble();
        Lat = in.readDouble();
        Distance = in.readDouble();
    }

    public AltitudePoints() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (Key ? 1 : 0));
//        dest.writeBoolean(Key);
//        dest.writeBoolean(LockDest);
        dest.writeByte((byte) (LockDest ? 1 : 0));
//        dest.writeBoolean(LockAlt);
        dest.writeByte((byte) (LockAlt ? 1 : 0));
        dest.writeDouble(Alt);
        dest.writeDouble(Lng);
        dest.writeDouble(Lat);
        dest.writeDouble(Distance);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AltitudePoints> CREATOR = new Creator<AltitudePoints>() {
        @Override
        public AltitudePoints createFromParcel(Parcel in) {
            return new AltitudePoints(in);
        }

        @Override
        public AltitudePoints[] newArray(int size) {
            return new AltitudePoints[size];
        }
    };

    public boolean getKey() {
        return Key;
    }

    public void setKey(boolean key) {
        Key = key;
    }

    public boolean getLockDest() {
        return LockDest;
    }

    public void setLockDest(boolean lockDest) {
        LockDest = lockDest;
    }

    public boolean getLockAlt() {
        return LockAlt;
    }

    public void setLockAlt(boolean lockAlt) {
        LockAlt = lockAlt;
    }

    public double getAlt() {
        return Alt;
    }

    public void setAlt(double alt) {
        Alt = alt;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }
}
