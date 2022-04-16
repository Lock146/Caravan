package com.example.caravan.Fragments;

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

import com.example.caravan.Activity.DirectionActivity;
import com.example.caravan.DestinationInterface;
import com.example.caravan.DestinationModel;
import com.example.caravan.GooglePlaceModel;
import com.example.caravan.R;
import com.example.caravan.Utility.LoadingDialog;
import com.example.caravan.databinding.CurrentItemLayoutBinding;
import com.example.caravan.databinding.FragmentCurrentLocationsBinding;
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


public class GroupMembersFragment extends Fragment implements DestinationInterface {

    private FragmentCurrentLocationsBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<DestinationModel> destinationModelArrayList;
    private LoadingDialog loadingDialog;
    private FirebaseRecyclerAdapter<String, ViewHolder> firebaseRecyclerAdapter;
    private DestinationInterface destinationInterface;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCurrentLocationsBinding.inflate(inflater, container, false);
        destinationInterface = this;
        firebaseAuth = FirebaseAuth.getInstance();
        destinationModelArrayList = new ArrayList<>();

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

                            DestinationModel destinationModel = snapshot.getValue(DestinationModel.class);
                            holder.binding.setDestinationModel(destinationModel);
                            holder.binding.setListener(destinationInterface);
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
    public void onLocationClick(DestinationModel destinationModel) {

        if (destinationModel.getLat() != null && destinationModel.getLng() != null) {
            Intent intent = new Intent(requireContext(), DirectionActivity.class);
            intent.putExtra("placeId", destinationModel.getPlaceId());
            intent.putExtra("lat", destinationModel.getLat());
            intent.putExtra("lng", destinationModel.getLng());

            startActivity(intent);

        } else {
            Toast.makeText(requireContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationClick2(GooglePlaceModel googlePlaceModel) {

    }

    @Override
    public void onConfirmationClick(DestinationModel destinationModel) {

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
