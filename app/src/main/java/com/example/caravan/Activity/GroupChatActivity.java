package com.example.caravan.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;

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
        m_chatAdapter = new ChatAdapter(m_chatMessages, getBitmapFromEncodedString(m_receiverUser.image));
        //database = FirebaseFirestore.getInstance();
    }
    private void sendMessage() {
        //HashMap<String, Object> message = new HashMap<>();
        //message.put(Constants.KEY_SENDER_ID, m_preferenceManager.getString(Constants.KEY_USER_ID));
        //message.put(Constants.KEY_RECEIVER_ID, m_receiverUser.id);
        //message.put(Constants.KEY_MESSAGE, m_binding.message.getText().toString());
        //message.put(Constants.KEY_TIMESTAMP, new Date());
        //database.collection(Constants.KEY_COLLECTION_CHAT).add(messsage);
        Database.get_instance().send_message(m_binding.message.getText().toString());
        m_binding.message.setText(null);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) { return; }
        if(value != null){
            int count = m_chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    m_chatMessages.add(chatMessage);
                }

            }
        }
    };
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(bytes,0, bytes.length); }

    private void loadReceiverDetails() {
        m_receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        m_binding.textName.setText(m_receiverUser.name);
    }
    private void setListeners() {
        m_binding.imageBack.setOnClickListener(v -> onBackPressed());
        m_binding.layoutSend.setOnClickListener(v -> sendMessage());
    }
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh =:mm a", Locale.getDefault()).format(date);
    }
}