package com.example.caravan.Activity;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavDrawerLayoutBinding navDrawerLayoutBinding;
    private ActivityMainBinding activityMainBinding;
    private ToolbarLayoutBinding toolbarLayoutBinding;
    private FirebaseAuth firebaseAuth;
    private CircleImageView imgHeader;
    private TextView txtName, txtEmail;
    private Timer m_currentLocationUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");
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

        //getUserData();

        imgHeader = headerLayout.findViewById(R.id.imgHeader);
        txtName = headerLayout.findViewById(R.id.txtHeaderName);
        txtEmail = headerLayout.findViewById(R.id.txtHeaderEmail);

        getUserData();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
    }

    @Override
    protected void onResume(){
        Log.d("MainActivity", "onResume called");
        super.onResume();
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
        //Database.get_instance().leave_group();
        m_currentLocationUpdater.cancel();
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        Log.d("MainActivity", "onStop called");
        super.onStop();
    }

    @Override
    protected void onPause(){
        Log.d("MainActivity", "onPause called");
        super.onPause();
    }

    private void getUserData() {

        String userID = Database.get_instance().get_userID();
        txtName.setText(Database.get_instance().get_userID());
        txtEmail.setText(Database.get_instance().get_user_email(userID));
        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                //.child(firebaseAuth.getUid());
        //databaseReference.addValueEventListener(new ValueEventListener() {
            //@Override
            //public void onDataChange(@NonNull DataSnapshot snapshot) {

                //if (snapshot.exists()) {

                    //UserModel userModel = snapshot.getValue(UserModel.class);
                    //Glide.with(MainActivity.this).load(userModel.getImage()).into(imgHeader);
                    //txtName.setText(userModel.getUsername());
                   // txtEmail.setText(userModel.getEmail());


                //}

            }

            //@Override
            //public void onCancelled(@NonNull DatabaseError error) {

            //}
        //});
    //}
}