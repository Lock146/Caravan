package com.example.caravan.Activity;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravan.Database;
import com.example.caravan.R;
import com.example.caravan.databinding.ActivityProfileMenuBinding;
import com.example.caravan.generated.callback.OnClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PPMenuActivity extends AppCompatActivity {

    private ActivityProfileMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);

        binding = ActivityProfileMenuBinding.inflate(getLayoutInflater());

        binding.imageButton1.setOnClickListener(view -> {
        changepicture1();
        });
    }

    private void changepicture1() {
        Log.e(TAG, "onCrete: " + "ITS BEEN CLICKED" );
        Uri imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Bunny.png?alt=media&token=35bf15dd-9af7-48eb-9d56-7addc22b7401");
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri)
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdate);

        Database.get_instance().update_profilePicture();
    }
}