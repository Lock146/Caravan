package com.example.caravan;

import static com.example.caravan.Constant.Constants.KEY_COLLECTION_GROUPS;
import static com.example.caravan.Constant.Constants.KEY_COLLECTION_USERS;
import static com.example.caravan.Constant.Constants.KEY_CURRENT_LOCATION;
import static com.example.caravan.Constant.Constants.KEY_GROUP_ID;
import static com.example.caravan.Constant.Constants.KEY_GROUP_OWNER;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

    public String get_userID(){
        return m_userID;
    }

    public String get_groupID(){
        return m_groupID;
    }

    public void create_group(){
        // Create group
        DocumentReference group = m_database.collection(KEY_COLLECTION_GROUPS)
                .document();
        Map<String, Object> groupInfo = new HashMap<>();
        groupInfo.put(KEY_GROUP_OWNER, FirebaseAuth.getInstance().getUid());
        group.set(groupInfo);
        m_groupID = group.getId();

        // Update user info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(KEY_GROUP_ID, group.getId());
        Task<Void> user = m_database.collection(KEY_COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .set(userInfo, SetOptions.merge());
    }

    public void join_group(String groupID){

    }

    public Boolean in_group(){
        return m_groupID != null;
    }

    public void update_location(Location location){
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .update(KEY_CURRENT_LOCATION, location);
    }

    public void send_message(String message){
        if(m_groupID != null){
            DocumentReference ref = m_database.collection(KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .collection(Constants.KEY_CHAT)
                    .document();
            HashMap<String, Object> data = new HashMap<>();
            data.put(Constants.KEY_SENDER_ID, m_userID);
            data.put(Constants.KEY_MESSAGE, message);
            data.put(Constants.KEY_TIMESTAMP, new Date());
            ref.set(data)
                    .addOnFailureListener( e -> {
                        Log.d("Database", "Failed sending message: " + e.toString());
                    });
        }
    }

    public void add_message_listener(EventListener<QuerySnapshot> listener){
        FirebaseFirestore.getInstance().collection(KEY_COLLECTION_GROUPS)
                .document(Database.get_instance().get_groupID())
                .collection(Constants.KEY_CHAT)
                .addSnapshotListener(listener);
    }
}
