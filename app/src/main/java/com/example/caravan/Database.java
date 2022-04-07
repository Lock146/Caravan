package com.example.caravan;

import static com.example.caravan.Constant.Constants.KEY_COLLECTION_GROUPS;
import static com.example.caravan.Constant.Constants.KEY_COLLECTION_USERS;
import static com.example.caravan.Constant.Constants.KEY_CURRENT_LOCATION;
import static com.example.caravan.Constant.Constants.KEY_GROUP_ID;
import static com.example.caravan.Constant.Constants.KEY_GROUP_OWNER;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.caravan.Constant.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private String m_email;
    private EventListener<DocumentSnapshot> m_dbUserListener;

    public static Database get_instance(){
        if(m_instance == null){
            m_instance = new Database();
        }
        return m_instance;
    }

    private Database(){
        m_database = FirebaseFirestore.getInstance();
        m_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        m_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        m_dbUserListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.get(KEY_GROUP_ID) != null) {
                    m_groupID = value.get(KEY_GROUP_ID).toString();
                }
            }
        };
        m_database.collection(Constants.KEY_COLLECTION_USERS)
                .document(m_userID)
                .addSnapshotListener(m_dbUserListener);
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
        CollectionReference groupMembers = group.collection(Constants.KEY_COLLECTION_GROUP_MEMBERS);
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        Map<String, Object> groupMember = new HashMap<>();
        groupMember.put(m_email, m_userID);
        groupMembers.add(groupMember)
                .addOnSuccessListener(documentReference -> Log.d("Database", "Successfully added group owner as member."))
                .addOnFailureListener(e -> Log.d("Database", "Unable to add group owner as member. Error: " + e.toString()))
                .addOnCompleteListener(task -> Log.d("Database", "Completed task adding group owner as member."));
        m_groupID = group.getId();

        // Update user info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(KEY_GROUP_ID, group.getId());
        m_database.collection(KEY_COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getUid())
                .set(userInfo, SetOptions.merge());
    }

    public void join_group(String groupID){

    }

    public void add_user(String email){
        try {
            CollectionReference userCollection = m_database.collection(KEY_COLLECTION_USERS);
            Task<QuerySnapshot> userTask = userCollection.get();
            userTask.addOnSuccessListener(users ->
            {
                for (DocumentSnapshot user : users.getDocuments()) {
                    if(email.equals(user.get(Constants.KEY_EMAIL).toString())){
                        // Add user to group
                        CollectionReference groupMembers = m_database.collection(KEY_COLLECTION_GROUPS)
                                .document(m_groupID)
                                .collection(Constants.KEY_COLLECTION_GROUP_MEMBERS);
                        Map<String, Object> newMember = new HashMap<>();
                        newMember.put(email, user.getId());
                        groupMembers.add(newMember)
                                .addOnSuccessListener(documentReference -> Log.d("Database", "Successfully added user " + email))
                                .addOnFailureListener(e -> Log.d("Database", "Failed adding user " + email + ". Error: " + e.toString()))
                                .addOnCompleteListener(task -> Log.d("Database", "Completed operation trying to add user " + email));

                        // Update user's group info
                        m_database.collection(Constants.KEY_COLLECTION_USERS)
                                .document(user.getId())
                                .update(Constants.KEY_GROUP_ID, m_groupID);
                    }
                }
            });
        }
        catch(Exception e){
            Log.d("Database", "Exception: " + e.toString());
        }
    }

    private String find_user(String email){
//        try {
//            CollectionReference userCollection = m_database.collection(KEY_COLLECTION_USERS);
//            Task<QuerySnapshot> userTask = userCollection.get();
//            while(!userTask.isComplete());
//            QuerySnapshot users = userTask.getResult();
//            for (DocumentSnapshot user : users.getDocuments()) {
//
//                Log.d("Database", "User: " + user.toString());
//            }
//        }
//        catch(Exception e){
//            Log.d("Database", "Exception: " + e.toString());
//        }
        return "";
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
                .document(m_groupID)
                .collection(Constants.KEY_CHAT)
                .addSnapshotListener(listener);
    }

    public void add_group_join_listener(EventListener<DocumentSnapshot> listener){
        m_database.collection(Constants.KEY_COLLECTION_USERS)
                .document(m_userID)
                .addSnapshotListener(listener);
    }
}
