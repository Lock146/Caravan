package com.example.caravan.Activity;

import android.annotation.SuppressLint;
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
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import com.example.caravan.R;
import com.example.caravan.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import java.util.HashMap;
import java.util.Map;

public class MessagingServices extends FirebaseMessagingService {

    @Override
    //called when new token is changed or generated then we update
    //that token in user data
   public void onNewToken(@NonNull String token){
        super.onNewToken(token);
    //Log.d("FCM", "Token: " + token);
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        final String Channel_ID = "Heads Up Notification";
        NotificationChannel channel =  new NotificationChannel(
                Channel_ID,
                "Heads up Notification",NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, Channel_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(remoteMessage);
        //Log.d("FCM", "Message: " + remoteMessage.getNotification().getBody());
        //User user = new User();
        //user.name = remoteMessage.getData().get(Constants.KEY_USER_ID);
    }



   }