package com.riteshb.mysmartcycle.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoPoints implements Parcelable {
    double Lng;
    double Lat;
    double Distance;
    double VideoTime;

    protected VideoPoints(Parcel in) {
        Lng = in.readDouble();
        Lat = in.readDouble();
        Distance = in.readDouble();
        VideoTime = in.readDouble();
    }

    public VideoPoints() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(Lng);
        dest.writeDouble(Lat);
        dest.writeDouble(Distance);
        dest.writeDouble(VideoTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoPoints> CREATOR = new Creator<VideoPoints>() {
        @Override
        public VideoPoints createFromParcel(Parcel in) {
            return new VideoPoints(in);
        }

        @Override
        public VideoPoints[] newArray(int size) {
            return new VideoPoints[size];
        }
    };

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

    public double getVideoTime() {
        return VideoTime;
    }

    public void setVideoTime(double videoTime) {
        VideoTime = videoTime;
    }
}
