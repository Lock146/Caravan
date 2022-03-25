package com.example.caravan;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.caravan.Constant.Constants;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Database {
    static private Database m_instance;
    private FirebaseFirestore m_database;
    private String m_userID;
    private String m_groupID;

    public static Database get_instance(){
        if(m_instance == null){
            m_instance = new Database();
        }
        return m_instance;
    }

    private Database(){
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

    public void join_group(String groupID){

    }

    public void update_location(Location location){
        m_database.collection("Users")
                .document(m_userID)
                .update("currentLocation", location);
    }

    public void send_message(String message){
        if(m_groupID != null){
            DocumentReference ref = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .collection(Constants.KEY_DOCUMENT_CHAT)
                    .document();
            HashMap<String, Object> data = new HashMap<>();
            data.put("sender", m_userID);
            data.put("text", message);
            data.put("timestamp", new Date());
        }
    }
}
