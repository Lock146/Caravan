package com.example.caravantest;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    private FirebaseDatabase m_database;
    private DatabaseReference m_dbReference;
    private String m_userID;

    public Database(){
        m_database = FirebaseDatabase.getInstance();
        m_dbReference = m_database.getReference();
        m_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void publish_message(String message){
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
}
