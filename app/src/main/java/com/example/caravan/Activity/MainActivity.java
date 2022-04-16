package com.example.caravan.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.caravan.CurrentLocationUpdateTask;
import com.example.caravan.Database;
import com.example.caravan.DeviceInfo;
import com.example.caravan.R;
import com.example.caravan.UserModel;
import com.example.caravan.databinding.ActivityMainBinding;
import com.example.caravan.databinding.NavDrawerLayoutBinding;
import com.example.caravan.databinding.ToolbarLayoutBinding;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavDrawerLayoutBinding navDrawerLayoutBinding;
    private ActivityMainBinding activityMainBinding;
    private ToolbarLayoutBinding toolbarLayoutBinding;
    private FirebaseAuth firebaseAuth;
    private CircleImageView imgHeader;
    private TextView txtName, txtEmail;
    private Timer m_currentLocationUpdater;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Implement location update pausing
        m_currentLocationUpdater = new Timer();
        long period = 5000;
        m_currentLocationUpdater.schedule(new CurrentLocationUpdateTask(getApplicationContext(), period), 0, period);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.MAPS_API_KEY));
        navDrawerLayoutBinding = NavDrawerLayoutBinding.inflate(getLayoutInflater());
        setContentView(navDrawerLayoutBinding.getRoot());
        activityMainBinding = navDrawerLayoutBinding.mainActivity;
        toolbarLayoutBinding = activityMainBinding.toolbar;

        setSupportActionBar(toolbarLayoutBinding.toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                navDrawerLayoutBinding.navDrawer,
                toolbarLayoutBinding.toolbar,
                R.string.open_navigation_drawer,
                R.string.close_navigation_drawer
        );

        navDrawerLayoutBinding.navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavController navController = Navigation.findNavController(this, R.id.fragmentContainer);
        NavigationUI.setupWithNavController(
                navDrawerLayoutBinding.navigationView,
                navController
        );

        View headerLayout = navDrawerLayoutBinding.navigationView.getHeaderView(0);

        imgHeader = headerLayout.findViewById(R.id.imgHeader);
        txtName = headerLayout.findViewById(R.id.txtHeaderName);
        txtEmail = headerLayout.findViewById(R.id.txtHeaderEmail);

        getUserData();
    }

    @Override
    public void onBackPressed() {

        if (navDrawerLayoutBinding.navDrawer.isDrawerOpen(GravityCompat.START))
            navDrawerLayoutBinding.navDrawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy(){
        m_currentLocationUpdater.cancel();
        super.onDestroy();
    }

    private void getUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    UserModel userModel = snapshot.getValue(UserModel.class);
                    Glide.with(MainActivity.this).load(userModel.getImage()).into(imgHeader);
                    txtName.setText(userModel.getUsername());
                    txtEmail.setText(userModel.getEmail());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }

    public void deviceGroupUpstream() {
        // [START fcm_device_group_upstream]
        String to = "a_unique_key"; // the notification key
        AtomicInteger msgId = new AtomicInteger();
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(to)
                .setMessageId(String.valueOf(msgId.get()))
                .addData("hello", "world")
                .build());
        // [END fcm_device_group_upstream]
    }

    public void sendUpstream() {
        final String SENDER_ID = "YOUR_SENDER_ID";
        final int messageId = 0; // Increment for each
        // [START fcm_send_upstream]
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(messageId))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());
        // [END fcm_send_upstream]
    }
}