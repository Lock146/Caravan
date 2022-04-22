package com.example.caravan.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravan.R;

public class PPMenuActivity extends AppCompatActivity {

    ImageButton profilePicture9;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);

        profilePicture9 = (ImageButton) findViewById(R.id.imageButton9);

        profilePicture9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(PPMenuActivity.this, "Work", Toast.LENGTH_SHORT).show();

            }
        });
    }
}