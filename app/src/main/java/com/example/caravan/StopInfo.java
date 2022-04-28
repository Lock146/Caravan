package com.example.caravan;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.ObjectStreamException;

public class StopInfo implements Parcelable
{
    private static final String TAG = StopInfo.class.getSimpleName();
    @SerializedName("placeID")
    @Expose
    private String m_placeID;
    @SerializedName("name")
    @Expose
    private String m_name;
    @SerializedName("distance")
    @Expose
    private double m_distance;

    public StopInfo() {
        m_placeID = "";
        m_name = "";
        m_distance = 0.0;
    }

    public StopInfo(GooglePlaceModel stop, double distance) {
        Log.d(TAG, "StopInfo constructed: " + stop);
        m_placeID = stop.placeID();
        m_name = stop.getName();
        m_distance = distance;
    }

    public StopInfo(String placeID, String name, double distance){
        m_placeID = placeID;
        m_name = name;
        m_distance = distance;
    }

    public String getName()
    {
        return m_name;
    }

    public String getPlaceID(){
        return m_placeID;
    }

    public double getDistance()
    {
        return m_distance;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public void setPlaceID(String placeID){
        m_placeID = placeID;
    }

    public void setDistance(double distance)
    {
        m_distance = distance;
    }

    protected StopInfo(Parcel in) {
        m_placeID = in.readString();
        m_name = in.readString();
        m_distance = in.readDouble();
    }

    public static final Creator<StopInfo> CREATOR = new Creator<StopInfo>() {
        @Override
        public StopInfo createFromParcel(Parcel in) {
            return new StopInfo(in);
        }

        @Override
        public StopInfo[] newArray(int size) {
            return new StopInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(m_placeID);
        parcel.writeString(m_name);
        parcel.writeDouble(m_distance);
    }
}