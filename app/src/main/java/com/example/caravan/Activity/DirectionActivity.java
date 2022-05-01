package com.example.caravan.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.caravan.DestinationModel;
import com.example.caravan.DeviceInfo;
import com.example.caravan.Model.DirectionPlaceModel.DirectionLegModel;
import com.example.caravan.Model.DirectionPlaceModel.DirectionResponseModel;
import com.example.caravan.Model.DirectionPlaceModel.DirectionRouteModel;
import com.example.caravan.Model.DirectionPlaceModel.DirectionStepModel;
import com.example.caravan.Permissions.AppPermissions;
import com.example.caravan.R;
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

    private static String TAG = "help";

    private static ActivityDirectionBinding binding;
    private static GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private static boolean isLocationPermissionOk;
    private boolean isTrafficEnable;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private static BottomSheetLayoutBinding bottomSheetLayoutBinding;
    private static RetrofitAPI retrofitAPI;
    private LoadingDialog loadingDialog;
    private static Location currentLocation;
    private Double endLat;
    private Double endLng;
    private Double endLat2;
    private Double endLng2;
    private Double endLat3;
    private Double endLng3;
    private String placeId;
    private String placeId2;
    private String placeId3;
    private int currentMode;
    private boolean moreStops;
    private static DirectionStepAdapter adapter;
    private ArrayList<DestinationModel> destinationModelArrayList;
    private ArrayList<DestinationInfo> m_destinations;

    public static void getList(ArrayList<DestinationModel> destinationModelArrayList) {

        while (!destinationModelArrayList.isEmpty()) {
            if (destinationModelArrayList.get(0).getLat() != null && destinationModelArrayList.get(0).getLng() != null) {
                //Log.e(TAG, "onStartClick: " + destinationModelArrayList.get(0).getPlaceId());
                //placeId2 = destinationModelArrayList.get(0).getPlaceId();
                //endLat = destinationModelArrayList.get(0).getLat();
                //endLng = destinationModelArrayList.get(0).getLng();
               // getDirection("driving");

            } else {

            }

            destinationModelArrayList.remove(0);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        moreStops = true;
        placeId2 = null;
        endLat2 = 0.0;

        m_destinations = getIntent().getParcelableArrayListExtra(Constants.KEY_DESTINATIONS);
//        endLat = getIntent().getDoubleExtra("lat", 0.0);
//        endLng = getIntent().getDoubleExtra("lng", 0.0);
//        placeId = getIntent().getStringExtra("placeId");
//        //Log.e(TAG, "onStartClick: " + destinationModelArrayList.get(0).getPlaceId());
//
//        endLat2 = getIntent().getDoubleExtra("lat2", 0.0);
//        endLng2 = getIntent().getDoubleExtra("lng2", 0.0);
//        placeId2 = getIntent().getStringExtra("placeId2");
//        Log.e(TAG, "onStartClick: " + endLat2.toString());
//
//        endLat3 = getIntent().getDoubleExtra("lat3", 0.0);
//        endLng3 = getIntent().getDoubleExtra("lng3", 0.0);
//        placeId3 = getIntent().getStringExtra("placeId3");
//
//        if (endLat2 == 0.0) {
//            moreStops = false;
//        }
//        Log.e(TAG, "onStartClick: " + moreStops);



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
                    switch (checkedId) {
                        case R.id.btnChipDriving:
                            try {
                                getDirection("driving");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
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
            for(DestinationInfo destination : m_destinations){
                double endingLatitude = destination.latitude();
                double endingLongitude = destination.longitude();
                String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + startingLatitude + "," + startingLongitude +
                        "&destination=" + endingLatitude + "," + endingLongitude +
                        "&mode=" + mode +
                        "&key=" + getResources().getString(R.string.MAPS_API_KEY);

                retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                    @Override
                    public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                        Gson gson = new Gson();
                        String res = gson.toJson(response.body());
                        Log.d("TAG", "onResponse: " + res);

                        if (response.errorBody() == null) {
                            if (response.body() != null) {


                                if (response.body().getDirectionRouteModels().size() > 0) {
                                    DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);

                                    getSupportActionBar().setTitle(routeModel.getSummary());

                                    DirectionLegModel legModel = routeModel.getLegs().get(0);
                                    binding.txtStartLocation.setText(legModel.getStartAddress());
                                    binding.txtEndLocation.setText(legModel.getEndAddress());

                                    bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
                                    bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());


                                    mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(endingLatitude, endingLongitude))
                                            .title("End Location"));

                                    adapter.setDirectionStepModels(legModel.getSteps());

                                    List<LatLng> stepList = new ArrayList<>();

                                    PolylineOptions options = new PolylineOptions()
                                            .width(25)
                                            .color(Color.BLUE)
                                            .geodesic(true)
                                            .clickable(true)
                                            .visible(true);

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

                                } else {
                                    Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("TAG", "onResponse: " + response);
                        }

                        loadingDialog.stopLoading();
                    }

                    @Override
                    public void onFailure(Call<DirectionResponseModel> call, Throwable t) {

                    }
                });

                startingLatitude = endingLatitude;
                startingLongitude = endingLongitude;
                Thread.sleep(1000);
            }
//            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
//                    "origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
//                    "&destination=" + endLat + "," + endLng +
//                    "&mode=" + mode +
//                    "&key=" + getResources().getString(R.string.MAPS_API_KEY);
//
//            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
//                @Override
//                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
//                    Gson gson = new Gson();
//                    String res = gson.toJson(response.body());
//                    Log.d("TAG", "onResponse: " + res);
//
//                    if (response.errorBody() == null) {
//                        if (response.body() != null) {
//                            clearUI();
//
//                            if (response.body().getDirectionRouteModels().size() > 0) {
//                                DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);
//
//                                getSupportActionBar().setTitle(routeModel.getSummary());
//
//                                DirectionLegModel legModel = routeModel.getLegs().get(0);
//                                binding.txtStartLocation.setText(legModel.getStartAddress());
//                                binding.txtEndLocation.setText(legModel.getEndAddress());
//
//                                bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
//                                bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());
//
//
//                                mGoogleMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng()))
//                                        .title("End Location"));
//
//                                mGoogleMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng()))
//                                        .title("Start Location"));
//
//                                adapter.setDirectionStepModels(legModel.getSteps());
//
//
//                                List<LatLng> stepList = new ArrayList<>();
//
//                                PolylineOptions options = new PolylineOptions()
//                                        .width(25)
//                                        .color(Color.BLUE)
//                                        .geodesic(true)
//                                        .clickable(true)
//                                        .visible(true);
//
//                                List<PatternItem> pattern;
//                                if (mode.equals("walking")) {
//                                    pattern = Arrays.asList(
//                                            new Dot(), new Gap(10));
//
//                                    options.jointType(JointType.ROUND);
//                                } else {
//                                    pattern = Arrays.asList(
//                                            new Dash(30));
//                                }
//
//                                options.pattern(pattern);
//
//                                for (DirectionStepModel stepModel : legModel.getSteps()) {
//                                    List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
//                                    for (com.google.maps.model.LatLng latLng : decodedLatLng) {
//                                        stepList.add(new LatLng(latLng.lat, latLng.lng));
//                                    }
//                                }
//
//                                options.addAll(stepList);
//
//                                Polyline polyline = mGoogleMap.addPolyline(options);
//
//                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//                                LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//
//
//
//                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
//
//                            } else {
//                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Log.d("TAG", "onResponse: " + response);
//                    }
//
//                    loadingDialog.stopLoading();
//                }
//
//                @Override
//                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {
//
//                }
//            });
        }
//        if(moreStops == true) {
//
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    getDirection2("driving");
//                }
//            }, 5000);   //5 seconds
//
//
//        }

    }

//    private void getDirection2(String mode) {
//
//        if (isLocationPermissionOk) {
//            loadingDialog.startLoading();
//            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
//                    "origin=" + endLat + "," + endLng +
//                    "&destination=" + endLat2 + "," + endLng2 +
//                    "&mode=" + mode +
//                    "&key=" + getResources().getString(R.string.MAPS_API_KEY);
//
//            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
//                @Override
//                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
//                    Gson gson = new Gson();
//                    String res = gson.toJson(response.body());
//                    Log.d("TAG", "onResponse: " + res);
//
//                    if (response.errorBody() == null) {
//                        if (response.body() != null) {
//
//
//                            if (response.body().getDirectionRouteModels().size() > 0) {
//                                DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);
//
//                                getSupportActionBar().setTitle(routeModel.getSummary());
//
//                                DirectionLegModel legModel = routeModel.getLegs().get(0);
//                                binding.txtStartLocation.setText(legModel.getStartAddress());
//                                binding.txtEndLocation.setText(legModel.getEndAddress());
//
//                                bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
//                                bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());
//
//
//                                mGoogleMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(endLat2, endLng2))
//                                        .title("End Location"));
//
//                                mGoogleMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(endLat, endLng))
//                                        .title("Start Location"));
//
//                                adapter.setDirectionStepModels(legModel.getSteps());
//
//
//                                List<LatLng> stepList = new ArrayList<>();
//
//                                PolylineOptions options = new PolylineOptions()
//                                        .width(25)
//                                        .color(Color.BLUE)
//                                        .geodesic(true)
//                                        .clickable(true)
//                                        .visible(true);
//
//                                List<PatternItem> pattern;
//                                if (mode.equals("walking")) {
//                                    pattern = Arrays.asList(
//                                            new Dot(), new Gap(10));
//
//                                    options.jointType(JointType.ROUND);
//                                } else {
//                                    pattern = Arrays.asList(
//                                            new Dash(30));
//                                }
//
//                                options.pattern(pattern);
//
//                                for (DirectionStepModel stepModel : legModel.getSteps()) {
//                                    List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
//                                    for (com.google.maps.model.LatLng latLng : decodedLatLng) {
//                                        stepList.add(new LatLng(latLng.lat, latLng.lng));
//                                    }
//                                }
//
//                                options.addAll(stepList);
//
//                                Polyline polyline = mGoogleMap.addPolyline(options);
//
//                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//                                LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//
//
//                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
//
//                            } else {
//                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Log.d("TAG", "onResponse: " + response);
//                    }
//
//                    loadingDialog.stopLoading();
//                }
//
//                @Override
//                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {
//
//                }
//            });
//        }
//        if (endLat3 == 0.0) {
//            moreStops = false;
//        }
//        if(moreStops == true) {
//
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    getDirection3("driving");
//                }
//            }, 5000);   //5 seconds
//
//
//        }
//
//    }

    private void getDirection3(String mode) throws InterruptedException {

        if (isLocationPermissionOk) {
            loadingDialog.startLoading();

//            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
//                    "origin=" + endLat2 + "," + endLng2 +
//                    "&destination=" + endLat3 + "," + endLng3 +
//                    "&mode=" + mode +
//                    "&key=" + getResources().getString(R.string.MAPS_API_KEY);
//
//            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
//                @Override
//                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
//                    Gson gson = new Gson();
//                    String res = gson.toJson(response.body());
//                    Log.d("TAG", "onResponse: " + res);
//
//                    if (response.errorBody() == null) {
//                        if (response.body() != null) {
//
//
//                            if (response.body().getDirectionRouteModels().size() > 0) {
//                                DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);
//
//                                getSupportActionBar().setTitle(routeModel.getSummary());
//
//                                DirectionLegModel legModel = routeModel.getLegs().get(0);
//                                binding.txtStartLocation.setText(legModel.getStartAddress());
//                                binding.txtEndLocation.setText(legModel.getEndAddress());
//
//                                bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
//                                bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());
//
//
//                                mGoogleMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(endLat3, endLng3))
//                                        .title("End Location"));
//
//
//
//                                adapter.setDirectionStepModels(legModel.getSteps());
//
//
//                                List<LatLng> stepList = new ArrayList<>();
//
//                                PolylineOptions options = new PolylineOptions()
//                                        .width(25)
//                                        .color(Color.BLUE)
//                                        .geodesic(true)
//                                        .clickable(true)
//                                        .visible(true);
//
//                                List<PatternItem> pattern;
//                                if (mode.equals("walking")) {
//                                    pattern = Arrays.asList(
//                                            new Dot(), new Gap(10));
//
//                                    options.jointType(JointType.ROUND);
//                                } else {
//                                    pattern = Arrays.asList(
//                                            new Dash(30));
//                                }
//
//                                options.pattern(pattern);
//
//                                for (DirectionStepModel stepModel : legModel.getSteps()) {
//                                    List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
//                                    for (com.google.maps.model.LatLng latLng : decodedLatLng) {
//                                        stepList.add(new LatLng(latLng.lat, latLng.lng));
//                                    }
//                                }
//
//                                options.addAll(stepList);
//
//                                Polyline polyline = mGoogleMap.addPolyline(options);
//
//                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//                                LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
//
//
//                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
//
//                            } else {
//                                Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(DirectionActivity.this, "No route find", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Log.d("TAG", "onResponse: " + response);
//                    }
//
//                    loadingDialog.stopLoading();
//                }
//
//                @Override
//                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {
//
//                }
//            });
        }

    }

    private void clearUI() {

        mGoogleMap.clear();
        binding.txtStartLocation.setText("");
        binding.txtEndLocation.setText("");
        getSupportActionBar().setTitle("");
        bottomSheetLayoutBinding.txtSheetDistance.setText("");
        bottomSheetLayoutBinding.txtSheetTime.setText("");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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
                    currentLocation = location;

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
    
    
    public ArrayList<DestinationModel> getList() {
        return destinationModelArrayList;
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
}