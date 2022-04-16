package com.example.caravan;

import android.location.Location;

public class DeviceInfo {
    private static Location m_currentLocation;
    public static void set_location(Location location){
        m_currentLocation = location;
    }
    public static Location get_location(){
        return m_currentLocation;
    }
}
