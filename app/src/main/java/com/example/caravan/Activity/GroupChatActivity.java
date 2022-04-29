package com.example.caravan.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TintTypedArray;

import com.example.caravan.Adapter.ChatAdapter;
import com.example.caravan.Database;
import com.example.caravan.Model.ChatMessage;
import com.example.caravan.User;
import com.example.caravan.databinding.ActivityGroupChatBinding;
import com.example.caravan.Constant.Constants;
import com.example.caravan.network.ApiClient;
import com.example.caravan.network.ApiService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.acl.Group;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatActivity extends AppCompatActivity {
    private static final String TAG = GroupChatActivity.class.getSimpleName();
    private ActivityGroupChatBinding m_binding;
    private List<ChatMessage> m_chatMessages;
    private ChatAdapter m_chatAdapter;
    private User receiverUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init() {
        m_chatMessages = new ArrayList<>();
        m_chatAdapter = new ChatAdapter(m_chatMessages);
        m_binding.chatRecyclerView.setAdapter(m_chatAdapter);
    }
    private void sendMessage() {
        if (!m_binding.message.getText().toString().equals("")) {


            try{
                JSONArray tokens = new JSONArray();
                tokens.put("e1wcHntoRTSdd4A5lJ0uP7:APA91bHrpNCRvPsKs8_vAmSPZ59CPM8qmClfJClxEXHPmORa50I4IRutjC5GklV4a2fz5wJGgZVvZkv3WLVT5bBx9ABrJZ-8gaVlVas-cQE8PpMAtEISLlRjoIbyi60rePzcvT-nr2Tl");
                tokens.put("cQ2DJqX4ScuPxMXu0DJimW:APA91bFVSJQZ4ka5pNlqQDdCRcxUhZO2tm7-PDwKLWMM2hrsdaH30Eew1vZKT59kRogocbwPl4zrgeNmHlQeePiyTDKyfkQGQCKy0YGhtiblIk7950kXZxg3gGm9d-3iSDZGzjG-RNVg");
                tokens.put("f3PF23maR5WevKikeHl5vE:APA91bFI_yMgbpWzVmPsGrP0qToiXnSuGVHFUCFnvlwnY5cZHc4BoMQZObXeXy_86-q9LW8hvce4yojpY2MJSZUT-hSPoI64fBY8q9dBbtfUF4j1DT9tGp4m6yvGEgwLW7oIlRFOy6fR");

                //Log.e(TAG, "sendMessage: " + receiverUser.token.toString() );

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, "c91Fm3d4NoYsVIcItySJwwKXYyq2");
                data.put(Constants.KEY_NAME, "Kyler Parker");
                //data.put(Constants.KEY_FCM_TOKEN, "e1wcHntoRTSdd4A5lJ0uP7:APA91bHrpNCRvPsKs8_vAmSPZ59CPM8qmClfJClxEXHPmORa50I4IRutjC5GklV4a2fz5wJGgZVvZkv3WLVT5bBx9ABrJZ-8gaVlVas-cQE8PpMAtEISLlRjoIbyi60rePzcvT-nr2Tl");
                data.put(Constants.KEY_MESSAGE, "This is the message");


                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
            }
            catch (Exception exception){
                showToast(exception.getMessage());
            }



            Log.d("GroupChatActivity", "Sending message: " + m_binding.message.getText().toString());
            Database.get_instance().send_message(m_binding.message.getText().toString());
            m_binding.message.setText(null);
        }
        }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void list_members(){
    }

    private void loadReceiverDetails() {
        //m_receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        //m_binding.textName.setText(m_receiverUser.name);
    }

    private void listenMessages(){
        Database.get_instance().add_message_listener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) ->{
        if(error != null){
            return;
        }
        if(value != null){
            int count = m_chatMessages.size();
            for(DocumentChange change : value.getDocumentChanges()){
                if(change.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage message = new ChatMessage();
                    message.senderId = change.getDocument().getString(Constants.KEY_SENDER_ID);
                    message.message = change.getDocument().getString(Constants.KEY_MESSAGE);
                    message.dateTime = getReadableDateTime(change.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    message.dateObject = change.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    m_chatMessages.add(message);
                    //sendNotification("message received");
                   // if (!isReceiverAvalaible){
                   // }
                }
            }
            Collections.sort(m_chatMessages, (msg1, msg2) -> msg1.dateObject.compareTo(msg2.dateObject));
            if(count == 0){
                m_chatAdapter.notifyDataSetChanged();
            }
            else{
                m_chatAdapter.notifyItemRangeInserted(m_chatMessages.size(), m_chatMessages.size());
                m_binding.chatRecyclerView.smoothScrollToPosition(m_chatMessages.size() - 1);
            }
            m_binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        m_binding.progressBar.setVisibility(View.GONE);
    };

    private void setListeners() {
        m_binding.imageBack.setOnClickListener(v -> onBackPressed());
        m_binding.layoutSend.setOnClickListener(v -> sendMessage());
        m_binding.imageInfo.setOnClickListener(v -> list_members());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void listenAvailabilityOfReceiver() {

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
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace(); }
                            showToast("Notification sent Successfully");
                        } else{
                            showToast("Error: "+ response.code()); }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        showToast(t.getMessage()); }
                });
    }
}