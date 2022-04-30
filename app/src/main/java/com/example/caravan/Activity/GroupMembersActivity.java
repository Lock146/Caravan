package com.example.caravan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caravan.Adapter.GroupListAdapter;
import com.example.caravan.Adapter.suggestedStopsAdapter;
import com.example.caravan.Constant.Constants;
import com.example.caravan.Database;
import com.example.caravan.MemberInfo;
import com.example.caravan.R;
import com.example.caravan.StopInfo;
import com.example.caravan.databinding.ActivityGroupBinding;
import com.example.caravan.databinding.ActivityGroupMembersBinding;
import com.example.caravan.databinding.ActivityGrouplistBinding;
import com.example.caravan.network.ApiClient;
import com.example.caravan.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMembersActivity extends AppCompatActivity {
    private ActivityGroupMembersBinding binding;
    private static final CharSequence LEAVE_GROUP = "Leave group";
    private static final CharSequence CREATE_GROUP = "Create group";
    private static final CharSequence ADD_USER = "Add user";
    private static final CharSequence OPEN_CHAT = "Open Chat";
    private PreferenceManager m_preferenceManager;
    private RecyclerView recyclerView;
    private GroupListAdapter groupListAdapter;
    private ArrayList<MemberInfo> MemberList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GroupMembersActivity", "onCreateCalled");

        binding = ActivityGroupMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setFitsSystemWindows(true);


        // MemberList = Database.get_instance().get_group_members();

        MemberList = new ArrayList<>();
        Collection<ArrayList<String>> memberList = Database.get_instance().get_group_members().values();
        for(ArrayList<String> member : memberList){
            MemberList.add(new MemberInfo(member.get(Database.MemberData.Name), member.get(Database.MemberData.ProfilePicture)));
        }
        //suggestedStopsAdapter = new suggestedStopsAdapter(Database.get_instance().get_suggested_stops());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(groupListAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        //itemTouchHelper.attachToRecyclerView(recyclerView);




       // binding = ActivityGroupMembersBinding.inflate(getLayoutInflater());
        groupListAdapter = new GroupListAdapter(MemberList);
        binding.recyclerView.setAdapter(groupListAdapter);
       // setContentView(binding.getRoot());
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Database.get_instance();
        //recyclerView.addItemDecoration(dividerItemDecoration);



        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());
        binding.btnBack.setOnClickListener(view -> onBackPressed());


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
//        binding.GroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(!view.hasFocus()){
//                    Database.get_instance().update_group_name(binding.GroupName.getText().toString());
//                }
//            }
//        });

        if(Database.get_instance().in_group()){
            enable_group_functionality();
        }
        else{
            disable_group_functionality();
        }


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
        if(!binding.addEmail.getText().toString().isEmpty()) {
            String email = binding.addEmail.getText().toString();
            binding.addEmail.setText(null);
            Database.get_instance().add_user(email);
            try{
                Set<String> members = Database.get_instance().get_group_members().keySet();
                for(String member : members){
                    if(!member.equals(Database.get_instance().get_userID())){
                        JSONArray token = new JSONArray();
                        String memberToken = Database.get_instance()
                                .get_group_member(member)
                                .get(Database.MemberData.fcmToken);
                        token.put(memberToken);

                        JSONObject data = new JSONObject();
                        data.put(Constants.KEY_NAME, " Group Add");
                        data.put(Constants.KEY_MESSAGE, Database.get_instance()
                                .display_name() + " added you to their group");

                        JSONObject body = new JSONObject();
                        body.put(Constants.REMOTE_MSG_DATA, data);
                        body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token);

                        sendNotification(body.toString());
                    }
                }
            }
            catch (Exception exception){
                showToast(exception.getMessage());
            }
        }
        else{
            Toast.makeText(this, "Must provide email", Toast.LENGTH_SHORT).show();
        }
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
//        binding.groupMembership.setVisibility(View.INVISIBLE);
//        binding.groupMembership.setClickable(false);
//
//        binding.groupList.setVisibility(View.INVISIBLE);
//        binding.groupList.setClickable(false);
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
        //binding.groupMembership.setVisibility(View.VISIBLE);
        //binding.groupMembership.setClickable(true);
        //binding.groupList.setVisibility(View.VISIBLE);
        //binding.groupList.setClickable(true);
    }

    private void open_list(){
        //startActivity(new Intent(this, GroupListActivity.class));
    }

    private void setListeners() {
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());
        //binding.groupMembership.setOnClickListener(view -> leave_group());
        //binding.groupMembership.setOnClickListener(view -> leave_group());
        //binding.groupList.setOnClickListener(view -> open_list());
    }
    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if(response.isSuccessful()){
                            try {
                                if(response.body() != null){
                                    JSONObject responseJson = new JSONObject(response.body());
                                    JSONArray results = responseJson.getJSONArray("results");
                                    if(responseJson.getInt("failure")== 1){
                                        JSONObject error = (JSONObject) results.get(0);
                                        showToast(error.getString("error"));
                                        return; } }
                            } catch (JSONException e) {
                                e.printStackTrace(); }
                            showToast("User Added to Group");
                        } else{
                            showToast("Error: "+ response.code()); } }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        showToast(t.getMessage()); }
                }); }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show(); }
}