package com.example.caravan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.Toast;

import com.example.caravan.Constant.Constants;
import com.example.caravan.Database;
import com.example.caravan.R;
import com.example.caravan.databinding.ActivityGroupBinding;
import com.example.caravan.network.ApiClient;
import com.example.caravan.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Object;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupActivity extends AppCompatActivity {
    private ActivityGroupBinding binding;
    private static final CharSequence LEAVE_GROUP = "Leave group";
    private static final CharSequence CREATE_GROUP = "Create group";
    private static final CharSequence ADD_USER = "Add user";
    private static final CharSequence OPEN_CHAT = "Open Chat";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GroupActivity", "onCreateCalled");
        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

        //binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());
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
    }

    private void leave_group(){
        Database.get_instance().leave_group();
        //Database.get_instance();
        Database.set_instance();
        binding.chat.setText(CREATE_GROUP);
        binding.chat.setOnClickListener(view -> {
            create_group();
        });

        binding.groupMembership.setVisibility(View.INVISIBLE);
        binding.groupMembership.setClickable(false);

        disable_group_functionality();
    }

    private void create_group(){
        Database.get_instance().create_group();
        enable_group_functionality();
    }

    //private void add_user(){
    //    if(!binding.addEmail.getText().toString().isEmpty()) {
    //        String email = binding.addEmail.getText().toString();
   //         binding.addEmail.setText(null);
   //         Database.get_instance().add_user(email);
      //  }
      //  else{
    //        Toast.makeText(this, "Must provide email", Toast.LENGTH_SHORT).show();
    //    }
   // }

    private void open_group_chat(){

        startActivity(new Intent(this, GroupChatActivity.class));
    }

    private void disable_group_functionality(){
       // binding.addUser.setText(CREATE_GROUP);
       // binding.addUser.setOnClickListener(view -> {
        //    create_group();
      //  });

        binding.chat.setVisibility(View.INVISIBLE);
        binding.chat.setClickable(false);
        binding.groupMembership.setVisibility(View.INVISIBLE);
        binding.groupMembership.setClickable(false);
    }

    private void enable_group_functionality(){
        //binding.addUser.setText(ADD_USER);
       // binding.addUser.setOnClickListener(view -> {
        //    add_user();
      //  });

        binding.chat.setVisibility(View.VISIBLE);
        binding.chat.setClickable(true);
        binding.groupMembership.setVisibility(View.VISIBLE);
        binding.groupMembership.setClickable(true);
    }

    private void setListeners() {
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        //binding.addUser.setOnClickListener(view -> add_user());
        binding.chat.setOnClickListener(view -> open_group_chat());
        binding.groupMembership.setOnClickListener(view -> leave_group());
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show(); }

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
                            showToast("Notification sent Successfully");
                        } else{
                            showToast("Error: "+ response.code()); } }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        showToast(t.getMessage()); }
                });
    }
}