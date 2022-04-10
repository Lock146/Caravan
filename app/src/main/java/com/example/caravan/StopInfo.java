package com.example.caravan;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class StopInfo implements Parcelable
{
    private static final String TAG = StopInfo.class.getSimpleName();
    private String m_placeID;
    private String m_name;
    private double m_distance;

    public StopInfo(GooglePlaceModel stop, double distance) {
        Log.d(TAG, "StopInfo constructed: " + stop);
        m_placeID = stop.placeID();
        m_name = stop.getName();
        m_distance = distance;
    }

    public String name()
    {
        return m_name;
    }

    public double distance()
    {
        return m_distance;
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