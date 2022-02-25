package com.example.caravantest.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.caravantest.Utility.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class EmailChangeFragment extends Fragment {

    private com.example.caravantest.databinding.FragmentEmailChangeBinding binding;
    private LoadingDialog loadingDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.example.caravantest.databinding.FragmentEmailChangeBinding.inflate(inflater, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnUpdateEmail.setOnClickListener(update -> {

            String email = binding.edtUEmail.getText().toString().trim();
            if (email.isEmpty()) {
                binding.edtUEmail.setError("Email is required");
                binding.edtUEmail.requestFocus();
            } else {
                loadingDialog.startLoading();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                            Map<String, Object> map = new HashMap<>();
                            map.put("email", email);
                            databaseReference.updateChildren(map);
                            loadingDialog.stopLoading();
                            Navigation.findNavController(update).popBackStack();

                        } else {
                            loadingDialog.stopLoading();
                            Log.d("TAG", "onComplete: " + task.getException());
                            Toast.makeText(requireContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }
}
