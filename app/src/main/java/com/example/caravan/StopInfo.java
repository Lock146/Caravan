package com.example.caravan;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.HashMap;

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
    @SerializedName("latitude")
    @Expose
    private double m_latitude;
    @SerializedName("longitude")
    @Expose
    private double m_longitude;

    public StopInfo() {
        m_placeID = "";
        m_name = "";
        m_distance = 0.0;
        m_latitude = 0.0;
        m_longitude = 0.0;
    }

    public StopInfo(GooglePlaceModel stop) {
        Log.d(TAG, "StopInfo constructed: " + stop);
        m_placeID = stop.placeID();
        m_name = stop.getName();
        if(stop.getGeometry().getLocation() == null) {
            m_latitude = 0.0;
            m_longitude = 0.0;
        }
        else {
            m_latitude = stop.getGeometry().getLocation().getLat();
            m_longitude = stop.getGeometry().getLocation().getLng();
        }
        m_distance = 0.0;
        if(DeviceInfo.get_location() != null){
            double stopLat = stop.getGeometry().getLocation().getLat();
            double stopLng = stop.getGeometry().getLocation().getLng();
            double currentLat = DeviceInfo.get_location().getLatitude();
            double currentLng = DeviceInfo.get_location().getLongitude();
            m_distance = SphericalUtil.computeDistanceBetween(new LatLng(currentLat, currentLng),
                    new LatLng(stopLat, stopLng));
        }
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

    public double getLatitude() { return m_latitude; }

    public double getLongitude() { return m_longitude; }

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

    public void setLatitude(double latitude) { m_latitude = latitude; }

    public void setLongitude(double longitude) { m_longitude = longitude; }

    public static StopInfo get_stop_info(HashMap<String, Object> hashedStop){
        StopInfo stop = new StopInfo();
        stop.setName((String) hashedStop.get("name"));
        stop.setPlaceID((String) hashedStop.get("placeID"));
        stop.setDistance((double) hashedStop.get("distance"));
        stop.setLatitude((double) hashedStop.get("latitude"));
        stop.setLongitude((double) hashedStop.get("longitude"));
        return stop;
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

    @Override
    public boolean equals(Object obj){
        Log.d(TAG, "Equals override called");
        if(obj instanceof String){
            String placeID = (String) obj;
            return m_placeID.equals(placeID);
        }
//        Re-enable if we want comparison to GooglePlaceModel
//        else if(obj instanceof GooglePlaceModel){
//            GooglePlaceModel other = (GooglePlaceModel)obj;
//            return m_placeID.equals(other.placeID());
//        }
        else{
            return false;
        }
    }
}