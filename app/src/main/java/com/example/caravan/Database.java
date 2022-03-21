package com.example.caravan;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private FirebaseFirestore m_database;
    private String m_userID;

    public Database(){
        m_database = FirebaseFirestore.getInstance();
        m_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void create_group(){
        // Create group
        DocumentReference group = m_database.collection("Groups")
                .document();
        Map<String, Object> groupInfo = new HashMap<>();
        groupInfo.put("groupOwner", FirebaseAuth.getInstance().getUid());
        group.set(groupInfo);

        // Update user info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("groupID", group.getId());
        Task<Void> user = m_database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .set(userInfo, SetOptions.merge());
    }
}
