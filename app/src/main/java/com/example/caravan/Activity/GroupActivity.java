package com.example.caravan.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.caravan.Adapter.ChatAdapter;
import com.example.caravan.Adapter.RouteTimelineAdapter;
import com.example.caravan.Adapter.suggestedStopsAdapter;
import com.example.caravan.Constant.Constants;
import com.example.caravan.Database;
import com.example.caravan.Model.ChatMessage;
import com.example.caravan.R;
import com.example.caravan.StopInfo;
import com.example.caravan.User;
import com.example.caravan.databinding.ActivityGroupBinding;
import com.example.caravan.databinding.ActivityGroupChatBinding;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private ActivityGroupBinding binding;
    private static final CharSequence LEAVE_GROUP = "Leave group";
    private static final CharSequence CREATE_GROUP = "Create group";
    private static final CharSequence ADD_USER = "Add user";
    private static final CharSequence OPEN_CHAT = "Open Chat";
    private PreferenceManager m_preferenceManager;
    private RecyclerView recyclerView;
    private suggestedStopsAdapter suggestedStopsAdapter;
    private ArrayList<StopInfo> CurrentRoute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GroupActivity", "onCreateCalled");

        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setFitsSystemWindows(true);

        suggestedStopsAdapter = new suggestedStopsAdapter(Database.get_instance().get_suggested_stops());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(suggestedStopsAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        //itemTouchHelper.attachToRecyclerView(recyclerView);


        /*

        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        routeTimelineAdapter = new RouteTimelineAdapter(CurrentRoute);
        binding.recyclerView.setAdapter(routeTimelineAdapter);
        setContentView(binding.getRoot());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Database.get_instance();

        setContentView(R.layout.activity_routetimeline);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras.containsKey(Constants.KEY_STOPS)) {
            CurrentRoute = extras.getParcelableArrayList(Constants.KEY_STOPS);
        }
        else{
            CurrentRoute = new ArrayList<>();
        }
        setListeners();
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());


//        binding.GroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(!view.hasFocus()){
//                    Database.get_instance().update_group_name(binding.GroupName.getText().toString());
//                }
//            }
//        });
//        binding.groupMembership.setVisibility(Database.get_instance().in_group() ? View.VISIBLE : View.INVISIBLE);
//        CharSequence groupMembership = "Leave group";
//        binding.groupMembership.setText(groupMembership);
//        binding.groupMembership.setOnClickListener(view -> {
//            leave_group();
//        });
        binding.GroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
                    Database.get_instance().update_group_name(binding.GroupName.getText().toString());
                }
            }
        });

        if(Database.get_instance().in_group()){
            enable_group_functionality();
        }
        else{
            disable_group_functionality();
        }
        */

    }
    private void leave_group(){
        Database.get_instance().leave_group();
        binding.chat.setText(CREATE_GROUP);
        binding.chat.setOnClickListener(view -> {
            create_group();
        });

//        binding.groupMembership.setVisibility(View.INVISIBLE);
//        binding.groupMembership.setClickable(false);

        disable_group_functionality();
    }

    private void create_group(){
        Database.get_instance().create_group();
        enable_group_functionality();
    }

    private void add_user(){
        //if(!binding.addEmail.getText().toString().isEmpty()) {
        //    String email = binding.addEmail.getText().toString();
        //    binding.addEmail.setText(null);
        //    Database.get_instance().add_user(email);
        //}
        //else{
        //    Toast.makeText(this, "Must provide email", Toast.LENGTH_SHORT).show();
        //}
    }

    private void open_group_chat(){
        startActivity(new Intent(this, GroupChatActivity.class));
    }

    private void disable_group_functionality(){
        binding.addUser.setText(CREATE_GROUP);
        binding.addUser.setOnClickListener(view -> {
            create_group();
        });

        binding.chat.setVisibility(View.INVISIBLE);
        binding.chat.setClickable(false);
        //binding.groupMembership.setVisibility(View.INVISIBLE);
        //binding.groupMembership.setClickable(false);
    }

    private void enable_group_functionality(){
        binding.addUser.setText(ADD_USER);
        binding.addUser.setOnClickListener(view -> {
            add_user();
        });

        binding.chat.setVisibility(View.VISIBLE);
        binding.chat.setClickable(true);
        //binding.groupMembership.setVisibility(View.VISIBLE);
        //binding.groupMembership.setClickable(true);
    }

    private void setListeners() {
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());
        //binding.groupMembership.setOnClickListener(view -> leave_group());
    }
}