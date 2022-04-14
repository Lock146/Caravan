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
    private static final CharSequence LEAVE_GROUP = "Leave group";
    private static final CharSequence CREATE_GROUP = "Create group";
    private static final CharSequence ADD_USER = "Add user";
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
        binding.groupMembership.setVisibility(Database.get_instance().in_group() ? View.VISIBLE : View.INVISIBLE);
        CharSequence groupMembership = "Leave group";
        binding.groupMembership.setText(groupMembership);
        binding.groupMembership.setOnClickListener(view -> {
            leave_group();
        });
    }

    private void leave_group(){
        Database.get_instance().leave_group();

        binding.chat.setText(CREATE_GROUP);
        binding.chat.setOnClickListener(view -> {
            create_group();
        });

        binding.groupMembership.setVisibility(View.INVISIBLE);
        binding.groupMembership.setClickable(false);
    }

    private void create_group(){
        Database.get_instance().create_group();

        binding.chat.setText(ADD_USER);
        binding.chat.setOnClickListener(view -> {
            add_user();
        });

        binding.groupMembership.setVisibility(View.VISIBLE);
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