package com.example.caravan;

import static com.example.caravan.Constant.Constants.KEY_COLLECTION_GROUPS;
import static com.example.caravan.Constant.Constants.KEY_COLLECTION_USERS;
import static com.example.caravan.Constant.Constants.KEY_CURRENT_LOCATION;
import static com.example.caravan.Constant.Constants.KEY_GROUP_ID;
import static com.example.caravan.Constant.Constants.KEY_GROUP_NAME;
import static com.example.caravan.Constant.Constants.KEY_GROUP_OWNER;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.caravan.Constant.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static final String TAG = Database.class.getSimpleName();
    static private Database m_instance;
    private FirebaseFirestore m_database;
    private String m_userID;
    private String m_groupID;
    private String m_memberID;
    private String m_email;
    private EventListener<DocumentSnapshot> m_userListener;
    private EventListener<DocumentSnapshot> m_groupListener;
    private ListenerRegistration m_groupListenerRegistration;

    public static Database get_instance(){
        if(m_instance == null){
            m_instance = new Database();
        }
        return m_instance;
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
        m_groupID = group.getId();
        Map<String, Object> groupInfo = new HashMap<>();
        groupInfo.put(KEY_GROUP_OWNER, m_userID);
        groupInfo.put(KEY_GROUP_NAME, null);
        group.set(groupInfo)
                .addOnSuccessListener(result -> Log.d(TAG, "Successfully created group"))
                .addOnFailureListener(error -> Log.d(TAG, "Error creating group: " + error))
                .addOnCompleteListener(result -> Log.d(TAG, "Completed group creation"));
        add_user_info_to_group(m_email, m_userID);
        init_group_listener();

        // Update user info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(KEY_GROUP_ID, m_groupID);
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener(result -> Log.d(TAG, "Successfully added group info to user"))
                .addOnFailureListener(error -> Log.d(TAG, "Error adding group info to user: " + error))
                .addOnCompleteListener(result -> Log.d(TAG, "Completed added group info to user"));
    }

    public String get_user_email(String userID){
        // Implementation
       // CollectionReference email = (m_database.collection(KEY_COLLECTION_USERS)
                //.document(userID).collection(Constants.KEY_EMAIL));
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return (email);

    }

    public Uri get_user_image(){
        // Implementation
        // CollectionReference email = (m_database.collection(KEY_COLLECTION_USERS)
        //.document(userID).collection(Constants.KEY_EMAIL));
        Uri image = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        return (image);

    }

    public String get_user_username(){
        // Implementation
        // CollectionReference email = (m_database.collection(KEY_COLLECTION_USERS)
        //.document(userID).collection(Constants.KEY_EMAIL));
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        return (username);

    }

    public void leave_group(){
        Log.d("Database", "leave_group called");
        if(!(m_groupID == null || m_groupID.equals(""))){
            // Remove from members list
            DocumentReference group = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID);
            group.collection(Constants.KEY_COLLECTION_GROUP_MEMBERS)
                    .document(m_memberID)
                    .delete();
            group.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String groupOwner = documentSnapshot.get(KEY_GROUP_OWNER, String.class);
                            if (groupOwner != null) {
                                if (groupOwner.equals(m_userID)) {
                                    group.update(Constants.KEY_GROUP_OWNER, null);
                                }
                            }
                        }
                    });
            m_database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(m_userID)
                    .update(Constants.KEY_GROUP_ID, null);

            remove_group_listener();
        }
    }

    public void add_user(String email){
        try {
            CollectionReference userCollection = m_database.collection(KEY_COLLECTION_USERS);
            Task<QuerySnapshot> userTask = userCollection.get();
            userTask.addOnSuccessListener(users ->
            {
                for (DocumentSnapshot user : users.getDocuments()) {
                    Object userEmail = user.get(Constants.KEY_EMAIL);
                    if(userEmail != null && userEmail.equals(email)){
                        // Add user to group
                        add_user_info_to_group(email, user.getId());

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

    public Boolean in_group(){
        if  (m_groupID != null) {
            return true;
        } else {
            return false;
        }

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

    public void update_group_name(String newName){
        m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .update(Constants.KEY_GROUP_NAME, newName);
    }

    public void append_dest_to_route(String destination){
        Task<DocumentSnapshot> groupInfo = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .get();
        groupInfo.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot info) {
                Object query = info.get(Constants.KEY_ROUTE);
                ArrayList<String> route = (ArrayList<String>) query;
                assert route != null;
                route.add(destination);
                update_route(route);
            }
        });
    }

    public void update_route(ArrayList<String> placeIDs){
        if(in_group()) {
            HashMap<String, Object> routeInfo = new HashMap<>();
            routeInfo.put(Constants.KEY_ROUTE, placeIDs);
            m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .set(routeInfo)
                    .addOnSuccessListener(result -> Log.d(TAG, "Successfully published route to group"))
                    .addOnFailureListener(error -> Log.d(TAG, "Failed publishing route to group: " + error))
                    .addOnCompleteListener(result -> Log.d(TAG, "Completed route publishing task"));
        }
    }

    private Database(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Log.d(TAG, "Unable to get current Firebase user");
            assert false;
        }
        m_database = FirebaseFirestore.getInstance();
        m_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        m_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        init_user_listener();
    }

    private void get_member_id(){
        m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .collection(Constants.KEY_COLLECTION_GROUP_MEMBERS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot member : queryDocumentSnapshots){
                            Object email = member.get("email");
                            if(email != null && email.toString().equals(m_email)){
                                m_memberID = member.getId();
                                return;
                            }
                        }
                    }
                });
    }

    private void add_user_info_to_group(String email, String userID){
        if(in_group()) {
            CollectionReference groupMembers = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .collection(Constants.KEY_COLLECTION_GROUP_MEMBERS);
            Map<String, Object> groupMember = new HashMap<>();
            groupMember.put(Constants.KEY_EMAIL, email);
            groupMember.put(Constants.KEY_USER_ID, userID);
            groupMembers.add(groupMember)
                    .addOnSuccessListener(documentReference -> Log.d("Database", "Successfully added user to group."))
                    .addOnFailureListener(e -> Log.d("Database", "Unable to add user to group. Error: " + e.toString()))
                    .addOnCompleteListener(task -> Log.d("Database", "Completed task adding user to group"));
        }
    }

    private void init_user_listener(){
        m_userListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "User event: " + (value != null ? value : "Error: " + error));
                if(value.get(KEY_GROUP_ID) != null) {
                    Object groupID = value.get(Constants.KEY_GROUP_ID);
                    m_groupID = groupID == null ? null : groupID.toString();
                    if(m_groupID != null) {
                        get_member_id();
                        init_group_listener();
                    }
                }
            }
        };
        m_database.collection(Constants.KEY_COLLECTION_USERS)
                .document(m_userID)
                .addSnapshotListener(m_userListener);
    }

    private void init_group_listener(){
        m_groupListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "Group event: " + (value != null ? value.toString() : "Error: " + error));
            }
        };
        m_groupListenerRegistration = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .addSnapshotListener(m_groupListener);
    }

    private void remove_group_listener(){
        m_groupListenerRegistration.remove();
    }
}
