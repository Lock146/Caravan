package com.example.caravan;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Database {
    private FirebaseDatabase m_database;
    private String m_userID;

    public Database(){
        m_database = FirebaseDatabase.getInstance();
        m_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void publish_message(String message){
        DatabaseReference dbRef = m_database.getReference("Users");

        String path = m_userID + "/data";
        DatabaseReference ref = m_database.getReference(path);

        Task<Void> t = ref.setValue(message)
                .addOnCompleteListener(e -> {
                    Log.d("Database", "Completed sending data to database: " + e.toString());
                })
                .addOnFailureListener(e ->{
                    Log.d("Database", "Failed to send data to database: " + e.toString());
                })
                .addOnSuccessListener(e -> {
                    Log.d("Database", "Successfully sent data to database.");
                });
    }

    public String get_userID(String email){
        DatabaseReference ref = m_database.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    String currentLocations = child.child("Current Locations").getValue().toString();
                    String savedLocations = child.child("Saved Locations").getValue().toString();
                    Log.d("Current Locations", "Value(s): " + currentLocations);
                    Log.d("Saved Locations", "Value(s): " + savedLocations);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return "email";
    }
}
