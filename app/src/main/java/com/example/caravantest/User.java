package com.example.caravantest;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravantest.listeners.UserListener;

public class User extends AppCompatActivity implements UserListener {
    private ActivityUsersBinding binding;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
    private void getUsers(){
        loading(true);
        FirebaseFirestone database = FirebaseFirestone.getInstance();
        //need to finish writing getUsers - adding database aspects
    }

    private void showErrorMessage(){
    binding.textErrorMessage.setText(String.format("&s","No user available"));
    binding.textErrorMessage.setVisibility(View.VISIBLE); }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
