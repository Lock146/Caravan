package com.example.caravan.Adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caravan.Activity.GroupChatActivity;
import com.example.caravan.Database;
import com.example.caravan.Model.ChatMessage;
import com.example.caravan.databinding.ItemContainerReceivedMessageBinding;
import com.example.caravan.databinding.ItemContainerSentMessageBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> m_chatMessages;
    private final String m_senderId;


    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        m_chatMessages = chatMessages;
        m_senderId = Database.get_instance().get_userID();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent,
                    false));
        } else {
            return new ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(m_chatMessages.get(position));
        } else {
            ((ReceiverMessageViewHolder) holder).setData(m_chatMessages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return m_chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (m_chatMessages.get(position).senderId.equals(m_senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }


    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            Glide.with(itemView.getContext())
                    .load(Database.get_instance().get_profile_picture_of(chatMessage.senderId))
                    .into(binding.userProfile);
            binding.textUserName.setText(Database.get_instance().get_name_of(chatMessage.senderId));

            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
}