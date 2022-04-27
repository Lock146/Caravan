package com.example.caravan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.TimerTask;

import com.example.caravan.Database;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class CurrentLocationUpdateTask extends TimerTask {
    private Database m_database;
    private Context m_context;
    private Location m_currentLocation;
    private FusedLocationProviderClient m_locationClient;
    private LocationRequest m_locationRequest;
    private LocationCallback m_locationCallback;

    public Location get_current_location(){
        return m_currentLocation;
    }

    public CurrentLocationUpdateTask(Context context, long period){
        m_context = context;
        m_database = Database.get_instance();

        m_locationClient = LocationServices.getFusedLocationProviderClient(m_context);
        if (ActivityCompat.checkSelfPermission(m_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(m_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        m_locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                m_currentLocation = locationResult.getLastLocation();
                DeviceInfo.set_location(m_currentLocation);
            }
        };

        m_locationRequest = new LocationRequest();
        m_locationRequest.setInterval(period);
        m_locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Task<Void> task = m_locationClient.requestLocationUpdates(m_locationRequest, m_locationCallback, Looper.getMainLooper());
        task
                .addOnSuccessListener(unused -> {
                    Log.d("Device Info", "Location updates granted.");
                })
                .addOnFailureListener(e -> {
                    Log.d("DeviceInfo", "Location updates denied: " + e.toString());
                });
    }
    @Override
    public void run() {
        m_database.current_location(m_currentLocation);
    }
}
