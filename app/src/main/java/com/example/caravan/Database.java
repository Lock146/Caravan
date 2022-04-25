package com.example.caravan;

import static com.example.caravan.Constant.Constants.KEY_COLLECTION_GROUPS;
import static com.example.caravan.Constant.Constants.KEY_COLLECTION_USERS;
import static com.example.caravan.Constant.Constants.KEY_CURRENT_LOCATION;
import static com.example.caravan.Constant.Constants.KEY_GROUP_ID;
import static com.example.caravan.Constant.Constants.KEY_GROUP_MEMBERS;
import static com.example.caravan.Constant.Constants.KEY_GROUP_NAME;
import static com.example.caravan.Constant.Constants.KEY_GROUP_OWNER;
import static com.example.caravan.Constant.Constants.KEY_MEMBER_LOCATIONS;

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
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Database {
    private static final String TAG = Database.class.getSimpleName();
    static private Database m_instance;
    private FirebaseFirestore m_database;
    private String m_userID;
    private String m_groupID;
    private String m_email;
    private String m_profilePicture;
    private EventListener<DocumentSnapshot> m_userListener;
    private ListenerRegistration m_userListenerRegistration;
    private EventListener<DocumentSnapshot> m_groupListener;
    private ListenerRegistration m_groupListenerRegistration;
    private static class MemberData {
        // Changes will break compatibility with data in database. Be thorough.
        public static final int Email = 0;
        public static final int Name = 1;
        public static final int ProfilePicture = 2;
        public static final int size = 3;
    }
    private HashMap<String, ArrayList<String>> m_members;

    private static class MemberLocation{
        public static final int Latitude = 0;
        public static final int Longitude = 1;
        public static final int size = 2;
    }
    private HashMap<String, ArrayList<Double>> m_memberLocations;

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
        init_group_listener();
        upload_user_info();

        // Update user info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(KEY_GROUP_ID, group.getId());
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener(result -> Log.d(TAG, "Successfully added group info to user"))
                .addOnFailureListener(error -> Log.d(TAG, "Error adding group info to user: " + error))
                .addOnCompleteListener(result -> Log.d(TAG, "Completed added group info to user"));
    }

    public String get_user_email(String userID){
        if(m_members.containsKey(userID)){
            return Objects.requireNonNull(m_members.get(userID)).get(MemberData.Email);
        }
        else{
            return null;
        }
    }

    public Uri get_user_image(String userID){
        if(m_members.containsKey(userID)){
            return Uri.parse(Objects.requireNonNull(m_members.get(userID)).get(MemberData.ProfilePicture));
        }
        else{
            return null;
        }
    }

    public String get_user_username(String userID){
        if(m_members.containsKey(userID)){
            return Objects.requireNonNull(m_members.get(userID)).get(MemberData.Name);
        }
        else{
            return null;
        }
    }

    public void leave_group(){
        Log.d(TAG, "leave_group called");
        if(!(m_groupID == null || m_groupID.equals(""))){
            // Remove from members list
            DocumentReference group = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID);
            HashMap<String, Object> membersCopy = new HashMap<>(m_members);
            membersCopy.remove(m_userID);
            HashMap<String, Object> membersUpdated = new HashMap<>();
            membersUpdated.put(KEY_GROUP_MEMBERS, membersCopy);
            group.update(membersUpdated);

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

            HashMap<String, Object> memberLocations = new HashMap<>(m_memberLocations);
            memberLocations.remove(m_userID);
            HashMap<String, Object> memberLocationsUpdated = new HashMap<>();
            memberLocationsUpdated.put(KEY_MEMBER_LOCATIONS, memberLocations);
            group.update(memberLocationsUpdated);

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
                        // Update user's group info
                        m_database.collection(Constants.KEY_COLLECTION_USERS)
                                .document(user.getId())
                                .update(Constants.KEY_GROUP_ID, m_groupID);
                    }
                }
            });
        }
        catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
        }
    }

    public Boolean in_group(){
        return m_groupID != null;
    }

    public void update_location(Location location){
        if(in_group()){
            ArrayList<Double> myLocation = new ArrayList<>(MemberLocation.size);
            myLocation.add(MemberLocation.Latitude, location.getLatitude());
            myLocation.add(MemberLocation.Longitude, location.getLongitude());

            HashMap<String, ArrayList<Double>> myLocationMap = new HashMap<>();
            myLocationMap.put(m_userID, myLocation);

            HashMap<String, Object> mergeMyLocation = new HashMap<>();
            mergeMyLocation.put(KEY_MEMBER_LOCATIONS, myLocationMap);

            m_database.collection(KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .set(mergeMyLocation, SetOptions.merge());
        }
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
                        Log.d(TAG, "Failed sending message: " + e.toString());
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

    public void logout(){
        m_userListenerRegistration.remove();
        if(m_groupID != null){
            m_groupListenerRegistration.remove();
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
        m_members = new HashMap<>();
        init_user_listener();
    }

    private void init_user_listener(){
        m_userListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "User event: " + (value != null ? value : "Error: " + error));
                if(value != null) {
                    Boolean dataChanged = false;
                    Object obj = value.get(Constants.KEY_GROUP_ID);
                    String groupID = obj == null ? null : obj.toString();
                    if(groupID == null){
                        if(m_groupListenerRegistration != null){
                            m_groupListenerRegistration.remove();
                        }
                        m_groupID = null;
                    }
                    else if(!groupID.equals(m_groupID)){
                        remove_group_listener();
                        m_groupID = groupID;
                        init_group_listener();
                        dataChanged = true;
                    }

                    obj = value.get(Constants.KEY_PROFILE_PICTURE);
                    String profilePicture = obj == null ? null : obj.toString();
                    if(profilePicture != null && !profilePicture.equals(m_profilePicture)){
                        m_profilePicture = profilePicture;
                        dataChanged = true;
                    }

                    if(dataChanged && m_groupID != null){
                        upload_user_info();
                    }
                }
            }
        };
        m_userListenerRegistration = m_database.collection(Constants.KEY_COLLECTION_USERS)
                .document(m_userID)
                .addSnapshotListener(m_userListener);
    }

    @SuppressWarnings("unchecked")
    private void init_group_listener(){
        m_groupListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "Group event: " + (value != null ? value.toString() : "Error: " + error));
                if(value != null){
                    m_members = (HashMap<String, ArrayList<String>>) value.get(KEY_GROUP_MEMBERS);
                    m_memberLocations = (HashMap<String, ArrayList<Double>>) value.get(KEY_MEMBER_LOCATIONS);
                }
            }
        };
        m_groupListenerRegistration = m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .addSnapshotListener(MetadataChanges.EXCLUDE, m_groupListener);
    }

    private void remove_group_listener(){
        if(m_groupListenerRegistration != null) {
            m_groupListenerRegistration.remove();
            m_groupListenerRegistration = null;
        }
    }

    private HashMap<String, ArrayList<String>> generate_user_info(){
        ArrayList<String> userInfo = new ArrayList<>(MemberData.size);
        userInfo.add(MemberData.Email, m_email);
        userInfo.add(MemberData.Name, "name");
        userInfo.add(MemberData.ProfilePicture, m_profilePicture);
        HashMap<String, ArrayList<String>> userInfoMap = new HashMap<>();
        userInfoMap.put(m_userID, userInfo);
        return userInfoMap;
    }

    private void upload_user_info(){
        assert m_groupID != null;
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put(KEY_GROUP_MEMBERS, generate_user_info());
        m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener(result -> Log.d(TAG, "Successfully added member info"))
                .addOnFailureListener(error -> Log.d(TAG, "Error adding member info: " + error))
                .addOnCompleteListener(result -> Log.d(TAG, "Completed adding member info"));
    }
}
