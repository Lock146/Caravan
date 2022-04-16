package com.example.caravan;

import android.os.Parcel;
import android.os.Parcelable;

public class DestinationInfo implements Parcelable {
    public String placeID(){
        return m_placeID;
    }

    public void placeID(String placeID){
        m_placeID = placeID;
    }

    public Double latitude(){
        return m_latitude;
    }

    public void latitude(Double latitude){
        m_latitude = latitude;
    }

    public Double longitude(){
        return m_longitude;
    }

    public void longitude(Double longitude){
        m_longitude = longitude;
    }

    public DestinationInfo(String placeID, Double latitude, Double longitude){
        m_placeID = placeID;
        m_latitude = latitude;
        m_longitude = longitude;
    }

    protected DestinationInfo(Parcel in) {
        m_placeID = in.readString();
        m_latitude = in.readDouble();
        m_longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_placeID);
        dest.writeDouble(m_latitude);
        dest.writeDouble(m_longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DestinationInfo> CREATOR = new Creator<DestinationInfo>() {
        @Override
        public DestinationInfo createFromParcel(Parcel in) {
            return new DestinationInfo(in);
        }

        @Override
        public DestinationInfo[] newArray(int size) {
            return new DestinationInfo[size];
        }
    };

    private String m_placeID;
    private Double m_latitude;
    private Double m_longitude;
}
