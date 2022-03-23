package com.example.caravantest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravantest.databinding.ActivityGroupChatBinding;
import com.google.android.gms.common.internal.Constants;

public class GroupChat extends AppCompatActivity {

    private ActivityGroupChatBinding binding;
    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}