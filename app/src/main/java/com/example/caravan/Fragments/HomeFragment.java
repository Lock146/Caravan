package com.example.caravan.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.caravan.Activity.DirectionActivity;
import com.example.caravan.Activity.GroupActivity;
import com.example.caravan.Activity.RouteTimelineActivity;
import com.example.caravan.Adapter.GooglePlaceAdapter;
import com.example.caravan.Adapter.InfoWindowAdapter;
import com.example.caravan.Constant.AllConstant;
import com.example.caravan.Constant.Constants;
import com.example.caravan.DestinationInfo;
import com.example.caravan.Database;
import com.example.caravan.GooglePlaceModel;
import com.example.caravan.Model.GooglePlaceModel.GoogleResponseModel;
import com.example.caravan.NearLocationInterface;
import com.example.caravan.Permissions.AppPermissions;
import com.example.caravan.PlaceModel;
import com.example.caravan.R;
import com.example.caravan.StopInfo;
import com.example.caravan.Utility.LoadingDialog;
import com.example.caravan.WebServices.RetrofitAPI;
import com.example.caravan.WebServices.RetrofitClient;
import com.example.caravan.databinding.FragmentHomeBinding;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import com.google.gson.Gson;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, NearLocationInterface {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding m_binding;
    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean isLocationPermissionOk;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private Marker currentMarker;
    private LoadingDialog loadingDialog;
    private int radius = 5000;
    private RetrofitAPI retrofitAPI;
    private List<GooglePlaceModel> googlePlaceModelList;
    private GooglePlaceAdapter googlePlaceAdapter;
    private InfoWindowAdapter infoWindowAdapter;
    private ArrayList<String> userSavedLocationId;
    private ArrayList<String> userCurrentLocationId;
    private ArrayList<StopInfo> m_stops;
    public LatLng testLocation;
    private ListenerRegistration m_groupChangeRegistration;
    private ListenerRegistration m_routeRegistration;
    enum GroupStatus{
        ACTIVE, INACTIVE,
    }
    private final EventListener<DocumentSnapshot> m_onGroupChange = (value, error) -> {
        if(value == null){
            Log.d(TAG, "m_onGroupChange error: " + error);
        }
        else {
                String groupID = value.get(Constants.KEY_GROUP_ID, String.class);
                if (groupID != null) {
                    add_route_listener();
                    update_icons(GroupStatus.ACTIVE);
                } else {
                    remove_route_listener();
                    update_icons(GroupStatus.INACTIVE);
                }
        }
    };

    private final EventListener<DocumentSnapshot> m_routeListener = (value, error) -> {
        if(Database.get_instance().has_routes()){
            m_binding.route.setImageTintList(getResources().getColorStateList(R.color.primaryColor, null));
        }
        else{
            m_binding.route.setImageTintList(getResources().getColorStateList(R.color.colorBackground, null));
        }
    };

    private ActivityResultLauncher<Intent> m_timelineLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent intent = result.getData();
            ArrayList<StopInfo> stops = intent.getExtras().getParcelableArrayList(Constants.KEY_STOPS);
            ArrayList<StopInfo> updatedStops = new ArrayList<>();
            for(StopInfo stop : m_stops){
                int idx = get_index_of_stop(stops, stop.getPlaceID());
                if(idx == -1){
                    mark_as_removed(stop.getPlaceID());
                }
                else{
                    updatedStops.add(m_stops.get(idx));
                }
            }
            m_stops = updatedStops;
        }
    });

    public HomeFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("HomeFragment", "onCreateView");

        m_binding = FragmentHomeBinding.inflate(inflater, container, false);
        set_listeners();

        appPermissions = new AppPermissions();
        loadingDialog = new LoadingDialog(requireActivity());
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
        googlePlaceModelList = new ArrayList<>();
        userSavedLocationId = new ArrayList<>();
        userCurrentLocationId = new ArrayList<>();

        m_stops = new ArrayList<>();

        return m_binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("HomeFragment", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.homeMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        Places.initialize(getActivity().getApplicationContext(), getString(R.string.MAPS_API_KEY));

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        autocompleteFragment.setHint("Search a location...");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());


                final LatLng location = place.getLatLng();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                testLocation = location;
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        for (PlaceModel placeModel : AllConstant.placesName) {

            Chip chip = new Chip(requireContext());
            chip.setText(placeModel.getName());
            chip.setId(placeModel.getId());
            chip.setPadding(8, 8, 8, 8);
            chip.setTextColor(getResources().getColor(R.color.white, null));
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.primaryColor, null));
            chip.setChipIcon(ResourcesCompat.getDrawable(getResources(), placeModel.getDrawableId(), null));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);

            m_binding.placesGroup.addView(chip);
        }

        setUpRecyclerView();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState){
        Log.d(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart(){
        Log.d(TAG, "onStart called");
        m_groupChangeRegistration = Database.get_instance().add_group_join_listener(m_onGroupChange);
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume called");
        if (fusedLocationProviderClient != null) {

            //startLocationUpdates();
            if (currentMarker != null) {
                currentMarker.remove();
            }
        }


        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause called");
        if (fusedLocationProviderClient != null)
            stopLocationUpdate();
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.d(TAG, "onStop called");
        if(m_groupChangeRegistration != null){
            m_groupChangeRegistration.remove();
        }
        remove_route_listener();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        Log.d(TAG, "onSaveInstanceState called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView(){
        Log.d(TAG, "onDestroyView called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (appPermissions.isLocationOk(requireContext())) {
            isLocationPermissionOk = true;

            setUpGoogleMap();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission")
                    .setMessage("Caravan required location permission to show you near by places")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLocation();
                        }
                    })
                    .create().show();
        } else {
            requestLocation();
        }
    }

    private void set_listeners(){
        m_binding.btnMapType.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.btnNormal) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                return true;
            });
            popupMenu.show();
        });

        m_binding.route.setOnClickListener(route ->
                open_directions()
        );

        m_binding.route.setOnLongClickListener(view -> {
            open_timeline();
            return true;
        });

        m_binding.currentLocation.setOnClickListener(currentLocation -> getCurrentLocation());

        m_binding.placesGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                if (checkedId != -1) {
                    PlaceModel placeModel = AllConstant.placesName.get(checkedId - 1);
                    getPlaces(placeModel.getPlaceType());
                }
            }
        });

        m_binding.group.setOnClickListener(view -> open_group_activity());
        m_binding.group.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                Database.get_instance().in_group() ? R.drawable.ic_groups : R.drawable.ic_add));
    }

    private static int get_index_of_stop(ArrayList<StopInfo> stops, String placeID){
        for(int idx = 0; idx < stops.size(); idx += 1){
            if(stops.get(idx).getPlaceID().equals(placeID)){
                return idx;
            }
        }
        return -1;
    }

    private void mark_as_removed(String placeID){
        for(int idx = 0; idx < googlePlaceModelList.size(); idx += 1){
            GooglePlaceModel place = googlePlaceModelList.get(idx);
            if(place.placeID().equals(placeID)){
                place.in_timeline(false);
                googlePlaceAdapter.notifyItemChanged(idx);
                return;
            }
        }
    }

    private void requestLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_BACKGROUND_LOCATION}, AllConstant.LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
                setUpGoogleMap();
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.setOnMarkerClickListener(this::onMarkerClick);

        //setUpLocationUpdate();
        getCurrentLocation();
    }

    private void setUpLocationUpdate() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("HomeFragment", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Location updated started", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        getCurrentLocation();
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                infoWindowAdapter = null;
                infoWindowAdapter = new InfoWindowAdapter(currentLocation, requireContext());
                mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);
                moveCameraToLocation(location);

                double currentLatitude = 0.0;
                double currentLongitude = 0.0;
                if(location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                }
                testLocation = new LatLng(currentLatitude,currentLongitude);
            }

        });
    }

    private void moveCameraToLocation(Location location) {
        double latitude = 0.0;
        double longitude = 0.0;
        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new
                LatLng(latitude, longitude), 17);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet(Database.get_instance().display_name());

        if (currentMarker != null) {
            currentMarker.remove();
        }

        currentMarker = mGoogleMap.addMarker(markerOptions);
        currentMarker.setTag(703);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void stopLocationUpdate() {
        //fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        //Log.d("HomeFragment", "stopLocationUpdate: Location Update stop");
    }

    private void getPlaces(String placeName) {
        if (isLocationPermissionOk) {
            loadingDialog.startLoading();
            String url;
            if (testLocation == null) {
                 url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                        + "&radius=" + radius + "&type=" + placeName + "&key=" +
                        getResources().getString(R.string.MAPS_API_KEY);
            }
            else {
                url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        //+ currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                        + testLocation.latitude + ',' + testLocation.longitude
                        + "&radius=" + radius + "&type=" + placeName + "&key=" +
                        getResources().getString(R.string.MAPS_API_KEY);
            }
            if (currentLocation != null) {
                retrofitAPI.getNearByPlaces(url).enqueue(new Callback<GoogleResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleResponseModel> call, @NonNull Response<GoogleResponseModel> response) {
                        Gson gson = new Gson();
                        String res = gson.toJson(response.body());
                        Log.d("HomeFragment", "onResponse: " + res);
                        if (response.errorBody() == null) {
                            if (response.body() != null) {
                                if (response.body().getGooglePlaceModelList() != null && response.body().getGooglePlaceModelList().size() > 0) {

                                    googlePlaceModelList.clear();
                                    mGoogleMap.clear();
                                    for (int i = 0; i < response.body().getGooglePlaceModelList().size(); i++) {
                                        GooglePlaceModel model = response.body().getGooglePlaceModelList().get(i);
                                        if (userSavedLocationId.contains(model.placeID())) {
                                            response.body().getGooglePlaceModelList().get(i).setSaved(true);
                                        }
                                        if (userCurrentLocationId.contains(model.placeID())) {
                                            model.setCurrentLocation(true);
                                        }
                                        model.in_timeline(m_stops.contains(model));
                                        googlePlaceModelList.add(model);
                                        addMarker(model, i);
                                    }

                                    googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);

                                } else if (response.body().getError() != null) {
                                    Snackbar.make(m_binding.getRoot(),
                                            response.body().getError(),
                                            Snackbar.LENGTH_LONG).show();
                                } else {

                                    mGoogleMap.clear();
                                    googlePlaceModelList.clear();
                                    googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);
                                    radius += 1000;
                                    Log.d("HomeFragment", "onResponse: " + radius);
                                    getPlaces(placeName);

                                }
                            }
                        } else {
                            Log.d("HomeFragment", "onResponse: " + response.errorBody());
                            Toast.makeText(requireContext(), "Error : " + response.errorBody(), Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.stopLoading();
                    }

                    @Override
                    public void onFailure(Call<GoogleResponseModel> call, Throwable t) {

                        Log.d("HomeFragment", "onFailure: " + t);
                        loadingDialog.stopLoading();

                    }
                });
            }
        }
    }

    private void addMarker(GooglePlaceModel googlePlaceModel, int position) {
        LatLng coordinates = new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                googlePlaceModel.getGeometry().getLocation().getLng());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(coordinates)
                .title(googlePlaceModel.getName())
                .snippet(googlePlaceModel.getVicinity());
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);
    }

    private BitmapDescriptor getCustomIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_gas_station);
        background.setTint(getResources().getColor(R.color.quantum_googred900, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setUpRecyclerView() {
        m_binding.placesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        m_binding.placesRecyclerView.setHasFixedSize(false);
        googlePlaceAdapter = new GooglePlaceAdapter(this);
        m_binding.placesRecyclerView.setAdapter(googlePlaceAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(m_binding.placesRecyclerView);

        m_binding.placesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position > -1) {
                    GooglePlaceModel googlePlaceModel = googlePlaceModelList.get(position);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                            googlePlaceModel.getGeometry().getLocation().getLng()), 20));
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int markerTag = (int) marker.getTag();
        Log.d("HomeFragment", "onMarkerClick: " + markerTag);

        m_binding.placesRecyclerView.scrollToPosition(markerTag);
        return false;
    }

    @Override
    public void onSaveClick(GooglePlaceModel googlePlaceModel) {
        Log.d(TAG, "onSaveClick called. GooglePlaceModel: " + googlePlaceModel.getName());
        append_stop(googlePlaceModel);
        googlePlaceModel.in_timeline(true);
        update_route_icon();

        googlePlaceAdapter.notifyDataSetChanged();

        // TODO: Add ability to remove stops
    }

    @Override
    public void onDirectionClick(GooglePlaceModel googlePlaceModel) {
        if (userSavedLocationId.contains(googlePlaceModel.placeID())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove Place")
                    .setMessage("Are you sure to remove this place?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removePlace(googlePlaceModel);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create().show();
        }
        else {
            loadingDialog.startLoading();
            locationCurrentReference.child(googlePlaceModel.placeID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {

                        DestinationModel destinationModel = new DestinationModel(googlePlaceModel.getName(), googlePlaceModel.getVicinity(),
                                googlePlaceModel.placeID(), googlePlaceModel.getRating(),
                                googlePlaceModel.getUserRatingsTotal(),
                                googlePlaceModel.getGeometry().getLocation().getLat(),
                                googlePlaceModel.getGeometry().getLocation().getLng());

                        saveCurrentLocation(destinationModel);
                    }

                    saveUserLocation(googlePlaceModel.placeID());

                    int index = googlePlaceModelList.indexOf(googlePlaceModel);
                    googlePlaceModelList.get(index).setCurrentLocation(true);
                    googlePlaceAdapter.notifyDataSetChanged();
                    loadingDialog.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void open_group_activity() {
        if (!Database.get_instance().in_group()) {
            Database.get_instance().create_group();
        }

        Intent intent = new Intent(requireContext(), GroupActivity.class);
        ArrayList<StopInfo> stops = new ArrayList<StopInfo>();
        ArrayList<String> placeIDs = new ArrayList<>();

        startActivity(intent);
    }

    private void open_directions(){
        Intent intent = new Intent(requireContext(), DirectionActivity.class);
        ArrayList<DestinationInfo> destinations = new ArrayList<DestinationInfo>();

        if(m_stops.size() != 0 || Database.get_instance().has_routes()){
            if(!Database.get_instance().in_group()){
                for (StopInfo stop : m_stops) {
                    destinations.add(new DestinationInfo(stop.getPlaceID(), stop.getLatitude(), stop.getLongitude()));
                }
                intent.putParcelableArrayListExtra(Constants.KEY_STOPS, destinations);
            }
            startActivity(intent);
        }
    }

    private void open_timeline(){
        Intent intent = new Intent(requireContext(), RouteTimelineActivity.class);

        ArrayList<DestinationInfo> destinations = new ArrayList<DestinationInfo>();
        if(m_stops.size() != 0 || Database.get_instance().has_routes()){
            if(!Database.get_instance().in_group()){
                intent.putParcelableArrayListExtra(Constants.KEY_STOPS, m_stops);
            }
            m_timelineLauncher.launch(intent);
        }
    }

    private void add_route_listener(){
        m_routeRegistration = Database.get_instance().add_group_listener(m_routeListener);
    }

    private void remove_route_listener(){
        if(m_routeRegistration != null){
            m_routeRegistration.remove();
        }
    }

    private void update_icons(GroupStatus status){
        m_binding.group.setImageDrawable(AppCompatResources.getDrawable(
                requireContext(),
                status == GroupStatus.INACTIVE ? R.drawable.ic_add : R.drawable.ic_groups));
        if(status == GroupStatus.ACTIVE){
        }
        else{

        }
    }

    private void update_route_icon(){
        if(Database.get_instance().has_routes() || m_stops.size() != 0){
            m_binding.route.setImageTintList(getResources().getColorStateList(R.color.primaryColor, null));
        }
        else{
            m_binding.route.setImageTintList(getResources().getColorStateList(R.color.colorBackground, null));
        }
    }

    private void append_stop(StopInfo stop){
        if(Database.get_instance().in_group()){
            Database.get_instance().append_to_suggestions(stop);
        }
        else{
            m_stops.add(stop);
        }
    }

    private void append_stop(GooglePlaceModel stop){
        if(Database.get_instance().in_group()){
            Database.get_instance().append_to_suggestions(stop);
        }
        else{
            m_stops.add(new StopInfo(stop, 0.0));
        }
    }
}