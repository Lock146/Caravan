package com.example.caravantest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravantest.Adapter.ChatAdapter;
import com.example.caravantest.Model.ChatMessage;
import com.example.caravantest.databinding.ActivityGroupChatBinding;
import com.google.android.gms.common.internal.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GroupChat extends AppCompatActivity {

    private ActivityGroupChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
    }

    private void init() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, getBitmapFromEncodedString(receiverUser.image));
        database = FirebaseFirestore.getInstance();
    }
    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(messsage);
        binding.inputMessage.settext(null);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) { return; }
        if(value != null){
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }

            }
        }
    };
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(bytes,0, bytes.length); }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh =:mm a", Locale.getDefault()).format(date);
    }
}