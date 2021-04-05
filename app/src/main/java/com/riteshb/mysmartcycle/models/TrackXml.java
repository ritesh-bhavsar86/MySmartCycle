package com.riteshb.mysmartcycle.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TrackXml implements Parcelable {
    String CheckValue;
    String Name;
    String Description;
    String Date;
    String Location;
    String Action;
    String Difficulty;
    String Video;
    String CountryCode;
    String GID;
    String UserOwnerGID;
    String OriginalVideoSize;
    String ErgoTrack;
    String Mode;
    String VerzeEditoru;
    String Verze;
    String StreamOnly;

    public TrackXml() {
    }

    List<AltitudePoints> altitudePoints;
    List<VideoPoints> videoPoints;

    protected TrackXml(Parcel in) {
        CheckValue = in.readString();
        Name = in.readString();
        Description = in.readString();
        Date = in.readString();
        Location = in.readString();
        Action = in.readString();
        Difficulty = in.readString();
        Video = in.readString();
        CountryCode = in.readString();
        GID = in.readString();
        UserOwnerGID = in.readString();
        OriginalVideoSize = in.readString();
        ErgoTrack = in.readString();
        Mode = in.readString();
        VerzeEditoru = in.readString();
        Verze = in.readString();
        StreamOnly = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CheckValue);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeString(Date);
        dest.writeString(Location);
        dest.writeString(Action);
        dest.writeString(Difficulty);
        dest.writeString(Video);
        dest.writeString(CountryCode);
        dest.writeString(GID);
        dest.writeString(UserOwnerGID);
        dest.writeString(OriginalVideoSize);
        dest.writeString(ErgoTrack);
        dest.writeString(Mode);
        dest.writeString(VerzeEditoru);
        dest.writeString(Verze);
        dest.writeString(StreamOnly);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrackXml> CREATOR = new Creator<TrackXml>() {
        @Override
        public TrackXml createFromParcel(Parcel in) {
            return new TrackXml(in);
        }

        @Override
        public TrackXml[] newArray(int size) {
            return new TrackXml[size];
        }
    };

    public String getCheckValue() {
        return CheckValue;
    }

    public void setCheckValue(String checkValue) {
        CheckValue = checkValue;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getGID() {
        return GID;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }

    public String getUserOwnerGID() {
        return UserOwnerGID;
    }

    public void setUserOwnerGID(String userOwnerGID) {
        UserOwnerGID = userOwnerGID;
    }

    public String getOriginalVideoSize() {
        return OriginalVideoSize;
    }

    public void setOriginalVideoSize(String originalVideoSize) {
        OriginalVideoSize = originalVideoSize;
    }

    public String getErgoTrack() {
        return ErgoTrack;
    }

    public void setErgoTrack(String ergoTrack) {
        ErgoTrack = ergoTrack;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public String getVerzeEditoru() {
        return VerzeEditoru;
    }

    public void setVerzeEditoru(String verzeEditoru) {
        VerzeEditoru = verzeEditoru;
    }

    public String getVerze() {
        return Verze;
    }

    public void setVerze(String verze) {
        Verze = verze;
    }

    public String getStreamOnly() {
        return StreamOnly;
    }

    public void setStreamOnly(String streamOnly) {
        StreamOnly = streamOnly;
    }

    public List<AltitudePoints> getAltitudePoints() {
        return altitudePoints;
    }

    public void setAltitudePoints(List<AltitudePoints> altitudePoints) {
        this.altitudePoints = altitudePoints;
    }

    public List<VideoPoints> getVideoPoints() {
        return videoPoints;
    }

    public void setVideoPoints(List<VideoPoints> videoPoints) {
        this.videoPoints = videoPoints;
    }


}
