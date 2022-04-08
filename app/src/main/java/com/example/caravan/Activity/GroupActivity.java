package com.example.caravan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
        Log.d("GroupActivity", "onCreateCalled");
        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());
        binding.GroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
                    Database.get_instance().update_group_name(binding.GroupName.getText().toString());
                }
            }
        });
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