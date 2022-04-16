package com.example.caravan.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.caravan.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.HashMap;
import java.util.Map;

public class MessagingServices extends FirebaseMessagingService {

    @Override
   public void onNewToken(@NonNull String token){
        super.onNewToken(token);
    Log.d("FCM", "Token: " + token); }

    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "Message: " + remoteMessage.getNotification().getBody());
    }

   }