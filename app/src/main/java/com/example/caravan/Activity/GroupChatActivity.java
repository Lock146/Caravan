package com.example.caravan.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravan.Adapter.ChatAdapter;
import com.example.caravan.Database;
import com.example.caravan.Model.ChatMessage;
import com.example.caravan.User;
import com.example.caravan.databinding.ActivityGroupChatBinding;
import com.example.caravan.Constant.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    private ActivityGroupChatBinding m_binding;
    private User m_receiverUser;
    private List<ChatMessage> m_chatMessages;
    private ChatAdapter m_chatAdapter;
    private PreferenceManager m_preferenceManager;
    //private FirebaseFirestore m_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
    }

    private void init() {
        m_chatMessages = new ArrayList<>();
        //m_chatAdapter = new ChatAdapter(m_chatMessages, getBitmapFromEncodedString(m_receiverUser.image));
        //database = FirebaseFirestore.getInstance();
    }
    private void sendMessage() {
        Log.d("GroupChatActivity", "Sending message: " + m_binding.message.getText().toString());
        Database.get_instance().send_message(m_binding.message.getText().toString());
        m_binding.message.setText(null);
    }
    private void list_members(){

    }

//    private Bitmap getBitmapFromEncodedString(String encodedImage) {
//        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }

    private void loadReceiverDetails() {
        m_receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        //m_binding.textName.setText(m_receiverUser.name);
    }
    private void setListeners() {
        m_binding.imageBack.setOnClickListener(v -> onBackPressed());
        m_binding.layoutSend.setOnClickListener(v -> sendMessage());
        m_binding.imageInfo.setOnClickListener(v -> list_members());
    }
//    private String getReadableDateTime(Date date) {
//        return new SimpleDateFormat("MMMM dd, yyyy - hh =:mm a", Locale.getDefault()).format(date);
//    }
}