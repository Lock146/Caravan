package com.example.caravan;

import android.location.Location;
import android.util.Log;

public class DeviceInfo {
    private static final String TAG = DeviceInfo.class.getSimpleName();
    private static Location m_currentLocation;
    public static void set_location(Location location){
        Log.d(TAG, "Location: " + location);
        m_currentLocation = location;
    }
    public static Location get_location(){
        return m_currentLocation;
    }
}
