package com.example.caravan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.example.caravan.Database;
import com.example.caravan.R;
import com.example.caravan.databinding.ActivityGroupBinding;

public class GroupActivity extends AppCompatActivity {
    private ActivityGroupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());
    }

    private void add_user(){
        if(!binding.addEmail.getText().toString().isEmpty()) {
            String email = binding.addEmail.getText().toString();
            binding.addEmail.setText(null);
            Database.get_instance().add_user(email);
        }
        else{
            Toast.makeText(this, "Must provide email", Toast.LENGTH_SHORT).show();
        }
    }

    private void open_group_chat(){
        startActivity(new Intent(this, GroupChatActivity.class));
    }
}