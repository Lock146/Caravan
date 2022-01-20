package com.example.caravan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.caravan.User;

public class DisplayMessageActivity extends AppCompatActivity {
    private User m_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        m_user = new User(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));

        String message = "Hello, " + m_user.m_name;
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }

    public void openMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}