package com.example.caravantest.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.caravantest.Activity.DirectionActivity;
import com.example.caravantest.CurrentLocationInterface;
import com.example.caravantest.CurrentLocationModel;
import com.example.caravantest.GooglePlaceModel;
import com.example.caravantest.R;
import com.example.caravantest.SavedLocationInterface;
import com.example.caravantest.SavedPlaceModel;
import com.example.caravantest.Utility.LoadingDialog;
import com.example.caravantest.databinding.CurrentItemLayoutBinding;
import com.example.caravantest.databinding.FragmentCurrentLocationsBinding;
import com.example.caravantest.databinding.FragmentSavedPlacesBinding;
import com.example.caravantest.databinding.SavedItemLayoutBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupMembersFragment extends Fragment implements CurrentLocationInterface {

    private FragmentCurrentLocationsBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<CurrentLocationModel> currentLocationModelArrayList;
    private LoadingDialog loadingDialog;
    private FirebaseRecyclerAdapter<String, ViewHolder> firebaseRecyclerAdapter;
    private CurrentLocationInterface currentLocationInterface;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCurrentLocationsBinding.inflate(inflater, container, false);
        currentLocationInterface = this;
        firebaseAuth = FirebaseAuth.getInstance();
        currentLocationModelArrayList = new ArrayList<>();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Group Members");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(requireActivity());
        binding.savedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.savedRecyclerView);
        getCurrentLocations();
    }


    private void getCurrentLocations() {
        loadingDialog.startLoading();
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Users");

        FirebaseRecyclerOptions<String> options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(query, String.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull String currentLocationId) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentLocationId);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            CurrentLocationModel currentLocationModel = snapshot.getValue(CurrentLocationModel.class);
                            holder.binding.setCurrentLocationModel(currentLocationModel);
                            holder.binding.setListener(currentLocationInterface);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                CurrentItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                        R.layout.current_item_layout, parent, false);
                return new ViewHolder(binding);
            }
        };

        binding.savedRecyclerView.setAdapter(firebaseRecyclerAdapter);
        loadingDialog.stopLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public void onLocationClick(CurrentLocationModel currentLocationModel) {

        if (currentLocationModel.getLat() != null && currentLocationModel.getLng() != null) {
            Intent intent = new Intent(requireContext(), DirectionActivity.class);
            intent.putExtra("placeId", currentLocationModel.getPlaceId());
            intent.putExtra("lat", currentLocationModel.getLat());
            intent.putExtra("lng", currentLocationModel.getLng());

            startActivity(intent);

        } else {
            Toast.makeText(requireContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationClick2(GooglePlaceModel googlePlaceModel) {

    }

    @Override
    public void onConfirmationClick(CurrentLocationModel currentLocationModel) {

    }

    @Override
    public void onStartClick() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CurrentItemLayoutBinding binding;

        public ViewHolder(@NonNull CurrentItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}