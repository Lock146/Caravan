package com.example.ui;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ui.databinding.ActivityMainBinding;
import com.example.ui.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity<actionCodeSettings> extends AppCompatActivity {
    FirebaseAuth mAuth;
    //Button btnLogOut;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //btnLogout = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        //btnLogout.setOnClickListener(view -> {
        //mAuth.signOut();
        // startActivity(new Intent(MainActivity.this, LoginActivity.class));});
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}
    //public void getGoogleCredentials() {
        //String googleIdToken = "";
        //AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null); }


