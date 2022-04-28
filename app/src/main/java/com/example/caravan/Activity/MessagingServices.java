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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import com.example.caravan.Constant.Constants;
import com.example.caravan.R;
import com.example.caravan.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        super.onMessageReceived(remoteMessage);

        User user = new User();
        //user.userID = remoteMessage.getData().get(com.example.caravan.Constant.Constants.KEY_USER_ID);
       // user.name = remoteMessage.getData().get(com.example.caravan.Constant.Constants.KEY_NAME);
        //user.token = remoteMessage.getData().get(com.example.caravan.Constant.Constants.KEY_FCM_TOKEN);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, GroupChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(com.example.caravan.Constant.Constants.KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notifications);
        //needs to be changed

        builder.setContentTitle(Constants.KEY_NAME);
        builder.setContentText(remoteMessage.getData().get(com.example.caravan.Constant.Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get(com.example.caravan.Constant.Constants.KEY_MESSAGE)));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "chat Message";
            String channelDescription = "This notification is used for chat messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());


//        String title = remoteMessage.getNotification().getTitle();
//        String text = remoteMessage.getNotification().getBody();
//        final String Channel_ID = "Heads Up Notification";
//        NotificationChannel channel =  new NotificationChannel(
//                Channel_ID,
//                "Heads up Notification",NotificationManager.IMPORTANCE_HIGH
//        );
//        getSystemService(NotificationManager.class).createNotificationChannel(channel);
//        Notification.Builder notification = new Notification.Builder(this, Channel_ID)
//                .setContentTitle(title)
//                .setContentText(text)
//                .setSmallIcon(R.drawable.ic_notifications)
//                .setAutoCancel(true);
//        NotificationManagerCompat.from(this).notify(1, notification.build());

        //Log.d("FCM", "Message: " + remoteMessage.getNotification().getBody());
        //User user = new User();
        //user.name = remoteMessage.getData().get(Constants.KEY_USER_ID);
    }



   }