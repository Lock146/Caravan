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
    private static final String TAG = GroupActivity.class.getSimpleName();
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

        setListeners();

        binding.GroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
                    Database.get_instance().update_group_name(binding.GroupName.getText().toString());
                }
            }
        });
    }

    private void open_group_chat(){
        Log.d(TAG, "open_group_chat called");
        startActivity(new Intent(this, GroupChatActivity.class));
    }
    private void go_to_group(){
        startActivity(new Intent(this, GroupMembersActivity.class));
    }

    private void open_list(){
        startActivity(new Intent(this, GroupListActivity.class));
    }

    private void setListeners() {
        Log.d(TAG, "setListeners called");
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.groupMember.setOnClickListener(view -> go_to_group());
        binding.chat.setOnClickListener(view -> open_group_chat());
    }
}