package com.example.caravan.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.caravan.Constant.AllConstant;
import com.example.caravan.Constant.Constants;
import com.example.caravan.Database;
import com.example.caravan.Permissions.AppPermissions;
import com.example.caravan.R;
import com.example.caravan.UserModel;
import com.example.caravan.Utility.LoadingDialog;
import com.example.caravan.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private Uri imageUri;
    private AppPermissions appPermissions;
    private LoadingDialog loadingDialog;
    private String email, username, password;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appPermissions = new AppPermissions();
        loadingDialog = new LoadingDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();


        //imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Bunny.png?alt=media&token=35bf15dd-9af7-48eb-9d56-7addc22b7401");
        imageUri = Uri.parse("android.resource://com.example.caravan/" + R.drawable.ic_person_outline);

                //getDrawable(R.mipmap.ic_bunny_avatar_round);
        //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/caravan-338702.appspot.com/o/Bunny.png?alt=media&token=35bf15dd-9af7-48eb-9d56-7addc22b7401").into(binding.imgPick);
        Glide.with(this).load(("android.resource://com.example.caravan/" + R.drawable.ic_person_outline)).into(binding.imgPick);

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.txtLogin.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.btnSignUp.setOnClickListener(view -> {
            if (areFieldReady()) {
                if (imageUri != null) {
                    signUp();
                } else {
                    Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.imgPick.setOnClickListener(view -> {


//            Intent intent = new Intent(this, PPMenuActivity.class);
//            startActivity(intent);
//            if (appPermissions.isStorageOk(this)) {
//                pickImage();
//            } else {
//                appPermissions.requestStoragePermission(this);
//            }
        });
    }

    private void pickImage() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    private boolean areFieldReady() {
        username = binding.edtUsername.getText().toString().trim();
        email = binding.edtEmail.getText().toString().trim();
        password = binding.edtPassword.getText().toString().trim();

        boolean flag = false;
        View requestView = null;

        if (username.isEmpty()) {
            binding.edtUsername.setError("Field is required");
            flag = true;
            requestView = binding.edtUsername;
        } else if (email.isEmpty()) {
            binding.edtEmail.setError("Field is required");
            flag = true;
            requestView = binding.edtEmail;
        } else if (password.isEmpty()) {
            binding.edtPassword.setError("Field is required");
            flag = true;
            requestView = binding.edtPassword;
        } else if (password.length() < 8) {
            binding.edtPassword.setError("Minimum 8 characters");
            flag = true;
            requestView = binding.edtPassword;
        }

        if (flag) {
            requestView.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void signUp() {
        loadingDialog.startLoading();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        CollectionReference databaseReference = FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> signUp) {
                if (signUp.isSuccessful()) {
                    storageReference.child(firebaseAuth.getUid() + AllConstant.IMAGE_PATH).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> image = taskSnapshot.getStorage().getDownloadUrl();
                            image.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> imageTask) {

                                    if (imageTask.isSuccessful()) {

                                        String url = imageTask.getResult().toString();
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .setPhotoUri(Uri.parse(url))
                                                .build();
                                        firebaseAuth.getCurrentUser().updateProfile(profileChangeRequest).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            UserModel userModel = new UserModel(email,
                                                                    username, url, true);
                                                            databaseReference.document(firebaseAuth.getUid())
                                                                    .set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    firebaseAuth.getCurrentUser().sendEmailVerification()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    loadingDialog.stopLoading();
                                                                                    Toast.makeText(SignUpActivity.this, "Verify email", Toast.LENGTH_SHORT).show();
                                                                                    Database.get_instance().display_name(username);
                                                                                    Database.get_instance().set_profile_picture(url);
                                                                                    onBackPressed();
                                                                                }
                                                                            });

                                                                }
                                                            });
                                                        } else {
                                                            loadingDialog.stopLoading();
                                                            Log.d("TAG", "onComplete: Update Profile" + task.getException());
                                                            Toast.makeText(SignUpActivity.this, "Update Profile" + task.getException(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        loadingDialog.stopLoading();
                                        Log.d("TAG", "onComplete: Image Path" + imageTask.getException());
                                        Toast.makeText(SignUpActivity.this, "Image Path" + imageTask.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                } else {
                    loadingDialog.stopLoading();
                    Log.d("TAG", "onComplete: Create user" + signUp.getException());
                    Toast.makeText(SignUpActivity.this, "" + signUp.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Glide.with(this).load(imageUri).into(binding.imgPick);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Log.d("TAG", "onActivityResult: " + exception);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}