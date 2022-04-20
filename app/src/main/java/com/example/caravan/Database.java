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

import com.example.caravan.Activity.MainActivity;
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
    private String m_displayName;
    private String displayName;
    private String profilePicture;
    private Uri m_profilePicture;
    private EventListener<DocumentSnapshot> m_dbUserListener;

    public static Database get_instance(){
        if(m_instance == null){
            m_instance = new Database();
        }
        return m_instance;
    }

    public static Database set_instance(){
        m_instance = new Database();
        return m_instance;
    }

    private Database(){
        m_database = FirebaseFirestore.getInstance();
        m_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        m_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        m_displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        m_profilePicture = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        m_dbUserListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.get(KEY_GROUP_ID) != null) {
                    Object groupID = value.get(Constants.KEY_GROUP_ID);
                    m_groupID = groupID == null ? null : groupID.toString();
                    get_member_id();
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
        m_groupID = group.getId();
        Map<String, Object> groupInfo = new HashMap<>();
        groupInfo.put(KEY_GROUP_OWNER, m_userID);
        groupInfo.put(KEY_GROUP_NAME, null);
        group.set(groupInfo);
        add_user_info_to_group(m_email, m_userID);

        // Update user info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(KEY_GROUP_ID, m_groupID);
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .set(userInfo, SetOptions.merge());
    }

    public String get_user_email(String userID){
        // Implementation
       // CollectionReference email = (m_database.collection(KEY_COLLECTION_USERS)
                //.document(userID).collection(Constants.KEY_EMAIL));
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return (email);

    }

    public String get_user_image(String userID){
        // Implementation
        // CollectionReference email = (m_database.collection(KEY_COLLECTION_USERS)
        //.document(userID).collection(Constants.KEY_EMAIL));
        //Uri image = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        //return (image);
        DocumentReference userImage = (m_database.collection(KEY_COLLECTION_USERS)
                .document(userID));
        Log.e(TAG, "USER ID: " + userID);
        userImage.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String image = documentSnapshot.get("profilePicture", String.class);
                Log.e(TAG, "image: " + image);
                if (image != null) {
                    profilePicture = image;
                    Log.e(TAG, "onSuccess: " + image);
                    return;
                }

            }

        });
        Log.e(TAG, "profilePicture!!!!!: " + profilePicture);
        return (profilePicture);

    }

    public String get_user_username(String userID){
        // Implementation
         DocumentReference userName = (m_database.collection(KEY_COLLECTION_USERS)
        .document(userID));
        Log.e(TAG, "USER ID: " + userID);
         userName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                     //@Override
                     public void onSuccess(DocumentSnapshot documentSnapshot) {
                         String name = documentSnapshot.get("displayName", String.class);
                         Log.e(TAG, "name: " + name);

                         if (name != null) {
                             displayName = name;
                             Log.e(TAG, "onSuccess: " + name);
                             //get_user_username(userID);
                             return;
                         }

                     }

                 });



        Log.e(TAG, "displayName: " + displayName);
        return (displayName);
    }
    public String get_displayName(String displayName) {
        return displayName;
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
    public void update_displayName() {
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .update("displayName", m_displayName);
    }
    public void update_profilePicture() {
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .update("profilePicture", m_profilePicture.toString());
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

    private void add_user_info_to_group(String email, String userID){
        if(in_group()) {
            CollectionReference groupMembers = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .collection(Constants.KEY_COLLECTION_GROUP_MEMBERS);
            Map<String, Object> groupMember = new HashMap<>();
            groupMember.put(Constants.KEY_EMAIL, m_email);
            groupMember.put(Constants.KEY_USER_ID, m_userID);
            groupMembers.add(groupMember)
                    .addOnSuccessListener(documentReference -> Log.d("Database", "Successfully added group owner as member."))
                    .addOnFailureListener(e -> Log.d("Database", "Unable to add group owner as member. Error: " + e.toString()))
                    .addOnCompleteListener(task -> Log.d("Database", "Completed task adding group owner as member."));
        }
    }
}
