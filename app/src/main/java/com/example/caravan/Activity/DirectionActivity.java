package com.example.caravan.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.caravan.Adapter.DirectionStepAdapter;
import com.example.caravan.Constant.AllConstant;
import com.example.caravan.Constant.Constants;
import com.example.caravan.Database;
import com.example.caravan.DestinationInfo;
import com.example.caravan.DeviceInfo;
import com.example.caravan.Model.DirectionPlaceModel.DirectionLegModel;
import com.example.caravan.Model.DirectionPlaceModel.DirectionResponseModel;
import com.example.caravan.Model.DirectionPlaceModel.DirectionRouteModel;
import com.example.caravan.Model.DirectionPlaceModel.DirectionStepModel;
import com.example.caravan.Permissions.AppPermissions;
import com.example.caravan.R;
import com.example.caravan.StopInfo;
import com.example.caravan.Utility.LoadingDialog;
import com.example.caravan.WebServices.RetrofitAPI;
import com.example.caravan.WebServices.RetrofitClient;
import com.example.caravan.databinding.ActivityDirectionBinding;
import com.example.caravan.databinding.BottomSheetLayoutBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = DirectionActivity.class.getSimpleName();

    private ActivityDirectionBinding binding;
    private static GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private static boolean isLocationPermissionOk;
    private boolean isTrafficEnable;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private BottomSheetLayoutBinding bottomSheetLayoutBinding;
    private static RetrofitAPI retrofitAPI;
    private LoadingDialog loadingDialog;
    private static DirectionStepAdapter adapter;
    private ArrayList<DestinationInfo> m_destinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        get_destinations();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appPermissions = new AppPermissions();
        loadingDialog = new LoadingDialog(this);

        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);

        bottomSheetLayoutBinding = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        adapter = new DirectionStepAdapter();

        bottomSheetLayoutBinding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomSheetLayoutBinding.stepRecyclerView.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.directionMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        binding.route.setOnClickListener(view -> {
            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }
        });

        binding.travelMode.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    if(checkedId == R.id.btnChipDriving) {
                        try {
                            getDirection("driving");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void getDirection(String mode) throws InterruptedException {
        if (isLocationPermissionOk) {
            loadingDialog.startLoading();
            double startingLatitude = DeviceInfo.get_location().getLatitude();
            double startingLongitude = DeviceInfo.get_location().getLongitude();
            String destinationID = m_destinations.get(m_destinations.size() - 1).placeID();

            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + startingLatitude + "," + startingLongitude +
                    "&destination=place_id:" + destinationID +
                    "&mode=" + mode +
                    "&key=" + getResources().getString(R.string.MAPS_API_KEY) +
                    "&waypoints=";
            StringBuilder chain = new StringBuilder();
            for(int i = 0; i < m_destinations.size(); i += 1){
                chain.append(i == 0 ? "" : "|")
                        .append("place_id:")
                        .append(m_destinations.get(i).placeID());

                double latitude = m_destinations.get(i).latitude();
                double longitude = m_destinations.get(i).longitude();
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("End Location"));
            }
            url += chain;

            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(@NonNull Call<DirectionResponseModel> call, @NonNull Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());
                    Log.d(TAG, "onResponse: " + res);

                    if (response.errorBody() == null) {
                        if (response.body() != null) {
                            if (response.body().getDirectionRouteModels().size() > 0) {
                                PolylineOptions options = new PolylineOptions()
                                        .width(25)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .clickable(true)
                                        .visible(true);

                                List<DirectionRouteModel> routeModels = response.body().getDirectionRouteModels();
                                assert getSupportActionBar() != null;
                                for(DirectionRouteModel routeModel : routeModels){
                                    getSupportActionBar().setTitle(routeModel.getSummary());

                                    List<DirectionLegModel> legModels = routeModel.getLegs();
                                    binding.txtStartLocation.setText(legModels.get(0).getStartAddress());
                                    binding.txtEndLocation.setText(legModels.get(legModels.size() - 1).getEndAddress());
                                    bottomSheetLayoutBinding.txtSheetTime.setText(legModels.get(0).getDuration().getText());
                                    bottomSheetLayoutBinding.txtSheetDistance.setText(legModels.get(0).getDistance().getText());
                                    for(DirectionLegModel legModel : legModels) {
                                        adapter.setDirectionStepModels(legModel.getSteps());

                                        List<PatternItem> pattern;
                                        if (mode.equals("walking")) {
                                            pattern = Arrays.asList(
                                                    new Dot(), new Gap(10));

                                            options.jointType(JointType.ROUND);
                                        } else {
                                            pattern = Arrays.asList(
                                                    new Dash(30));
                                        }

                                        options.pattern(pattern);

                                        List<LatLng> stepList = new ArrayList<>();
                                        for (DirectionStepModel stepModel : legModel.getSteps()) {
                                            List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                            for (com.google.maps.model.LatLng latLng : decodedLatLng) {
                                                stepList.add(new LatLng(latLng.lat, latLng.lng));
                                            }
                                        }

                                        options.addAll(stepList);

                                        Polyline polyline = mGoogleMap.addPolyline(options);

                                        LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                        LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
                                    }
                                }
                            } else {
                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + response);
                    }

                    loadingDialog.stopLoading();
                }

                @Override
                public void onFailure(@NonNull Call<DirectionResponseModel> call, @NonNull Throwable t) {

                }
            });
        }
    }

//    private void getDirection(String mode) throws InterruptedException {
//
//        if (isLocationPermissionOk) {
//            loadingDialog.startLoading();
//            double startingLatitude = DeviceInfo.get_location().getLatitude();
//            double startingLongitude = DeviceInfo.get_location().getLongitude();
//            for (DestinationInfo destination : m_destinations) {
//                double endingLatitude = destination.latitude();
//                double endingLongitude = destination.longitude();
//                String url = "https://maps.googleapis.com/maps/api/directions/json?" +
//                        "origin=" + startingLatitude + "," + startingLongitude +
//                        "&destination=" + endingLatitude + "," + endingLongitude +
//                        "&mode=" + mode +
//                        "&key=" + getResources().getString(R.string.MAPS_API_KEY);
//
//                retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
//                    @Override
//                    public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
//                        Gson gson = new Gson();
//                        String res = gson.toJson(response.body());
//                        Log.d("TAG", "onResponse: " + res);
//
//                        if (response.errorBody() == null) {
//                            if (response.body() != null) {
//
//
//                                if (response.body().getDirectionRouteModels().size() > 0) {
//                                    DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);
//
//                                    getSupportActionBar().setTitle(routeModel.getSummary());
//
//                                    DirectionLegModel legModel = routeModel.getLegs().get(0);
//                                    binding.txtStartLocation.setText(legModel.getStartAddress());
//                                    binding.txtEndLocation.setText(legModel.getEndAddress());
//
//                                    bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
//                                    bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());
//
//
//                                    mGoogleMap.addMarker(new MarkerOptions()
//                                            .position(new LatLng(endingLatitude, endingLongitude))
//                                            .title("End Location"));
//
//                                    adapter.setDirectionStepModels(legModel.getSteps());
//
//                                    List<LatLng> stepList = new ArrayList<>();
//
//                                    PolylineOptions options = new PolylineOptions()
//                                            .width(25)
//                                            .color(Color.BLUE)
//                                            .geodesic(true)
//                                            .clickable(true)
//                                            .visible(true);
//
//                                    List<PatternItem> pattern;
//                                    if (mode.equals("walking")) {
//                                        pattern = Arrays.asList(
//                                                new Dot(), new Gap(10));
//
//                                        options.jointType(JointType.ROUND);
//                                    } else {
//                                        pattern = Arrays.asList(
//                                                new Dash(30));
//                                    }
//
//                                    options.pattern(pattern);
//
//                                    for (DirectionStepModel stepModel : legModel.getSteps()) {
//                                        List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
//                                        for (com.google.maps.model.LatLng latLng : decodedLatLng) {
//                                            stepList.add(new LatLng(latLng.lat, latLng.lng));
//                                        }
//                                    }
//
//                                    options.addAll(stepList);
//
//                                    Polyline polyline = mGoogleMap.addPolyline(options);
//
//                                    LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//                                    LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//
//
//                                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
//
//                                } else {
//                                    Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Log.d("TAG", "onResponse: " + response);
//                        }
//
//                        loadingDialog.stopLoading();
//                    }
//
//                    @Override
//                    public void onFailure(Call<DirectionResponseModel> call, Throwable t) {
//
//                    }
//                });
//
//                startingLatitude = endingLatitude;
//                startingLongitude = endingLongitude;
//                Thread.sleep(500);
//            }
//        }
//    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (appPermissions.isLocationOk(this)) {
            isLocationPermissionOk = true;
            setupGoogleMap();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Near Me required location permission to show you near by places")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appPermissions.requestLocationPermission(DirectionActivity.this);
                            }
                        })
                        .create().show();
            } else {
                appPermissions.requestLocationPermission(DirectionActivity.this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
                setupGoogleMap();
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupGoogleMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        getCurrentLocation();
    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    try {
                        getDirection("driving");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(DirectionActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }

    private List<com.google.maps.model.LatLng> decode(String points) {

        int len = points.length();

        final List<com.google.maps.model.LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;

    }

    private void get_destinations(){
        if(Database.get_instance().in_group()){
            ArrayList<StopInfo> destinations = Database.get_instance().get_caravan_stops();
            m_destinations = new ArrayList<>(destinations.size());
            for(StopInfo destination : destinations){
                m_destinations.add(new DestinationInfo(destination.getPlaceID(),
                        destination.getLatitude(),
                        destination.getLongitude()));
            }
        }
        else{
            m_destinations = getIntent().getParcelableArrayListExtra(Constants.KEY_STOPS);
        }
    }
}