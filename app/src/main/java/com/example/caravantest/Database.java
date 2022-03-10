package com.example.caravantest;

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

        ref.setValue(message);
    }
}
