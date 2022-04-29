package com.example.caravan.Activity;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravan.Database;
import com.example.caravan.R;
import com.example.caravan.databinding.ActivityProfileMenuBinding;

public class PPMenuActivity extends AppCompatActivity {

    private ActivityProfileMenuBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);

        binding = ActivityProfileMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageButton1.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Bunny.png?alt=media&token=35bf15dd-9af7-48eb-9d56-7addc22b7401"));
        binding.imageButton2.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Cat.png?alt=media&token=072603f3-b678-4209-98e8-47dc4bc92850"));
        binding.imageButton3.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/emoji_13.png?alt=media&token=8188a65f-9830-48e1-83d7-fa08f80a3d52"));
        binding.imageButton4.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Fish.png?alt=media&token=a606def0-d6df-4915-92fe-e7a933dc0077"));
        binding.imageButton5.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Orange.png?alt=media&token=504f9df8-98a2-4244-8f04-dfb12e68b5a1"));
        binding.imageButton6.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/SantaCat.png?alt=media&token=a64f6e2a-ed4a-49f2-9b61-00f001dc7b5b"));
        binding.imageButton7.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Smiley_Faces.png?alt=media&token=c8741d61-5237-486e-9a64-f3c5bc5fbaaf"));
        binding.imageButton8.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Unfathomable.png?alt=media&token=4daad176-dd37-47b6-94a1-0ea2396832d3"));
        binding.imageButton9.setOnClickListener(view -> changePicture("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/caomptttt.gif?alt=media&token=f1ef80ed-1d6f-4271-b650-eaca73ccdd14"));
    }

    private void changePicture(String url){
        Log.e(TAG, "changePicture called with URL: " + url);

        Database.get_instance().set_profile_picture(url);
    }
}