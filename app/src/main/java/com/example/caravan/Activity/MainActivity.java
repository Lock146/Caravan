package com.example.caravan.Activity;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import com.google.firebase.auth.UserProfileChangeRequest;
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
    private String image;

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
       /*
        Uri imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/ariel.jpg?alt=media&token=3536ee44-72c2-46cc-9727-70f3bc956d26");
        //Uri imageUri = Uri.parse("android.resource://" + this.getPackageName() + R.drawable.ic_stars);
        UserProfileChangeRequest profileUpdate2 = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri)
                .build();

        String nameUser = "Kyler Parker";
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nameUser)
                .build();
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdate);
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdate2);
  */  }

    @Override
    protected void onResume(){
        Log.d("MainActivity", "onResume called");
        //getUserData();
        Glide.with(MainActivity.this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(imgHeader);
        super.onResume();
    }

    @Override
    public void onBackPressed() {


        if (navDrawerLayoutBinding.navDrawer.isDrawerOpen(GravityCompat.START))
            navDrawerLayoutBinding.navDrawer.closeDrawer(GravityCompat.START);
        else {
            super.onBackPressed();
        }


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



    public void getUserData() {

        String userID = Database.get_instance().get_userID();
        //image = Database.get_instance().get_user_image(firebaseAuth.getUid());
        //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/emoji_13.png?alt=media&token=8188a65f-9830-48e1-83d7-fa08f80a3d52").into(imgHeader);
        //imgHeader.setImageURI(Database.get_instance().get_user_image());
        if (Database.get_instance().get_profile_picture() != null) {

            //Log.e(TAG, "OOOOOOOOOOOOOO: " + image );
            Glide.with(this).load(Database.get_instance().get_profile_picture()).into(imgHeader);

            //imgHeader.setImageURI(Uri.parse(Database.get_instance().get_user_image(firebaseAuth.getUid())));
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getUserData();
                }
            }, 500);
        }
        //imgHeader.setImageURI(Uri.parse(image));
        if (Database.get_instance().get_user_username(firebaseAuth.getUid()) != null) {
            txtName.setText(Database.get_instance().get_user_username(firebaseAuth.getUid()));
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getUserData();
                }
            }, 500);
            //getUserData();
        }
        //Log.e(TAG, "getUserData: " + firebaseAuth.getUid() );
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