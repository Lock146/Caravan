package com.example.caravan;

import static com.example.caravan.Constant.Constants.KEY_COLLECTION_GROUPS;
import static com.example.caravan.Constant.Constants.KEY_COLLECTION_USERS;
import static com.example.caravan.Constant.Constants.KEY_CURRENT_LOCATION;
import static com.example.caravan.Constant.Constants.KEY_GROUP_ID;
import static com.example.caravan.Constant.Constants.KEY_GROUP_MEMBERS;
import static com.example.caravan.Constant.Constants.KEY_GROUP_NAME;
import static com.example.caravan.Constant.Constants.KEY_GROUP_OWNER;
import static com.example.caravan.Constant.Constants.KEY_MEMBER_LOCATIONS;
import static com.example.caravan.Constant.Constants.KEY_ROUTE;
import static com.example.caravan.Constant.Constants.KEY_SUGG_STOPS;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.caravan.Activity.MainActivity;
import com.example.caravan.Constant.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Database {
    private static final String TAG = Database.class.getSimpleName();
    static private Database m_instance;
    private FirebaseFirestore m_database;
    private String m_userID;
    private String m_groupID;
    private String m_owner;
    private String m_email;
    private String m_profilePicture;
    private String m_displayName;
    private EventListener<DocumentSnapshot> m_userListener;
    private ListenerRegistration m_userListenerRegistration;
    private EventListener<DocumentSnapshot> m_groupListener;
    private ListenerRegistration m_groupListenerRegistration;
    private ArrayList<StopInfo> m_route;
    public static class MemberData {
    private ArrayList<GooglePlaceModel> m_stops1;
    private String Token;
    private static class MemberData {
        // Changes will break compatibility with data in database. Be thorough.
        public static final int Email = 0;
        public static final int Name = 1;
        public static final int ProfilePicture = 2;
        public static final int fmcToken = 3;
        public static final int size = 4;
    }
    private HashMap<String, ArrayList<String>> m_members;

    private static class MemberLocation{
        public static final int Latitude = 0;
        public static final int Longitude = 1;
        public static final int size = 2;
    }
    private HashMap<String, ArrayList<Double>> m_memberLocations;

    private static class MemberVotes{
        public static final String For = "for";
        public static final String Against = "against";
    }
    private HashMap<String, HashMap<String, ArrayList<String>>> m_votes;
    private HashMap<String, Object> m_batchFieldUpdate;

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
        if(m_members != null && m_members.containsKey(userID)){
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
        if(m_members != null && m_members.containsKey(userID)){
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

            cleanup();
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

    public void current_location(Location location){
        if(in_group()){
            ArrayList<Double> myLocation = new ArrayList<>(MemberLocation.size);
            if(location != null) {
                myLocation.add(MemberLocation.Latitude, location.getLatitude());
                myLocation.add(MemberLocation.Longitude, location.getLongitude());
            }
            else{
                myLocation.add(MemberLocation.Latitude, 0.0);
                myLocation.add(MemberLocation.Longitude, 0.0);
            }

            HashMap<String, ArrayList<Double>> myLocationMap = new HashMap<>();
            myLocationMap.put(m_userID, myLocation);

            HashMap<String, Object> mergeMyLocation = new HashMap<>();
            mergeMyLocation.put(KEY_MEMBER_LOCATIONS, myLocationMap);

            m_database.collection(KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .set(mergeMyLocation, SetOptions.merge());
        }
    }

    public void display_name(String displayName){
        update_user_field(Constants.KEY_NAME, displayName);
    }

    public String get_name_of(String userID){
        if(m_members.containsKey(userID)){
            return Objects.requireNonNull(m_members.get(userID)).get(MemberData.Name);
        }
        else{
            return null;
        }
    }

    public String display_name(){
        return m_displayName;
    }

    public void set_profile_picture(String profilePicture){
        update_user_field(Constants.KEY_PROFILE_PICTURE, profilePicture);
    }

    public String get_profile_picture_of(String userID){
        if(m_members.containsKey(userID)){
            return Objects.requireNonNull(m_members.get(userID)).get(MemberData.ProfilePicture);
        }
        else{
            return null;
        }
    }

    public String get_profile_picture(){
        return m_profilePicture;
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
                    .addOnFailureListener( e -> Log.d(TAG, "Failed sending message: " + e));
        }
    }

    public void update_profilePicture() {
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .update("profilePicture", m_profilePicture.toString());
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

    @SuppressWarnings("unchecked")
    public void append_to_route(StopInfo stop){
        ArrayList<StopInfo> route = m_route != null ? new ArrayList<>(m_route) : new ArrayList<>();
        route.add(stop);
        update_group_map(Constants.KEY_ROUTE, route);
    }

    public void append_to_suggestions(GooglePlaceModel suggestion) {
        ArrayList<StopInfo> currentSuggestions = m_suggestedStops == null ? new ArrayList<>() : new ArrayList<>(m_suggestedStops);
        currentSuggestions.add(new StopInfo(suggestion, 0.0));
        update_group_map(KEY_SUGG_STOPS, currentSuggestions);
    }

    public void update_route(ArrayList<GooglePlaceModel> placeIDs){
        if(in_group()) {
            HashMap<String, Object> routeInfo = new HashMap<>();
            routeInfo.put(Constants.KEY_ROUTE, placeIDs);
            m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .set(routeInfo, SetOptions.merge())
                    .addOnSuccessListener(result -> Log.d(TAG, "Successfully published route to group"))
                    .addOnFailureListener(error -> Log.d(TAG, "Failed publishing route to group: " + error))
                    .addOnCompleteListener(result -> Log.d(TAG, "Completed route publishing task"));
        }
    }

    public void suggest_stops(ArrayList<GooglePlaceModel> placeIDs){
        if(in_group()) {
            HashMap<String, Object> routeInfo = new HashMap<>();
            routeInfo.put(KEY_SUGG_STOPS, placeIDs);
            m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .set(routeInfo, SetOptions.merge())
                    .addOnSuccessListener(result -> Log.d(TAG, "Successfully published route to group"))
                    .addOnFailureListener(error -> Log.d(TAG, "Failed publishing route to group: " + error))
                    .addOnCompleteListener(result -> Log.d(TAG, "Completed route publishing task"));
        }
    }

    public ArrayList<StopInfo> get_suggested_stops(){
        return m_suggestedStops;
    }

    public void update_suggestion_list(ArrayList<String> placeIDs){
        if(in_group()) {
            HashMap<String, Object> routeInfo = new HashMap<>();
            routeInfo.put(Constants.KEY_SUGG_LIST, placeIDs);
            m_database.collection(Constants.KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .set(routeInfo)
                    .addOnSuccessListener(result -> Log.d(TAG, "Successfully published suggestion to group"))
                    .addOnFailureListener(error -> Log.d(TAG, "Failed publishing suggestion to group: " + error))
                    .addOnCompleteListener(result -> Log.d(TAG, "Completed suggestions publishing task"));
        }
    }

    public void logout(){
        m_userListenerRegistration.remove();
        if(m_groupID != null){
            m_groupListenerRegistration.remove();
        }
        cleanup();
    }

    public void vote_for(String placeID){
        record_vote(placeID, true);
    }

    public void vote_against(String placeID){
        record_vote(placeID, false);
    }

    public boolean is_owner() {
        if(m_owner != null && m_userID != null) {
            Log.d(TAG, "is_owner -> " + m_owner.equals(m_userID));
            return m_owner.equals(m_userID);
        }
        else{
            Log.d(TAG, "is_owner: either m_owner or m_userID is null");
            return false;
        }
    }

    public HashMap<String, ArrayList<String>> get_group_members(){
        return m_members;
    }

    public void getToken(String token){
        Token = token;
        upload_user_info();
        Log.e(TAG, "getToken: " + Token );
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
        m_suggestedStops = new ArrayList<>();
        m_route = new ArrayList<>();

        init_user_listener();
    }

    private HashMap<String, ArrayList<String>> init_votes(){
        HashMap<String, ArrayList<String>> votes = new HashMap<>();
        votes.put(MemberVotes.For, new ArrayList<>());
        votes.put(MemberVotes.Against, new ArrayList<>());
        return votes;
    }

    private void init_user_listener(){
        m_userListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG, "User event: " + (value != null ? value : "Error: " + error));
                if(value != null) {
                    boolean dataChanged = false;
                    Object obj = value.get(Constants.KEY_GROUP_ID);
                    String groupID = obj == null ? null : obj.toString();
                    if(groupID == null){
                        if(m_groupListenerRegistration != null){
                            m_groupListenerRegistration.remove();
                        }
                        cleanup();
                        m_groupID = null;
                    }
                    else if(!groupID.equals(m_groupID)){
                        cleanup();
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

                    m_displayName = value.get(Constants.KEY_NAME, String.class);

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
                    m_owner = value.get(KEY_GROUP_OWNER, String.class);
                    m_members = (HashMap<String, ArrayList<String>>) value.get(KEY_GROUP_MEMBERS);
                    m_memberLocations = (HashMap<String, ArrayList<Double>>) value.get(KEY_MEMBER_LOCATIONS);

                    // TODO: See if there's another way to do this, it's fucking ugly
                    ArrayList<HashMap<String, Object>> route = (ArrayList<HashMap<String, Object>>) value.get(KEY_ROUTE);
                    if(route != null){
                        m_route = new ArrayList<>(route.size());
                        for(HashMap<String, Object> stop : route){
                            m_route.add(StopInfo.get_stop_info(stop));
                        }
                    }
                    else{
                        m_route = null;
                    }

                    ArrayList<HashMap<String, Object>> suggestions = (ArrayList<HashMap<String, Object>>) value.get(KEY_SUGG_STOPS);
                    if(suggestions != null) {
                        if(m_suggestedStops != null) {
                            m_suggestedStops.clear();
                        }
                        for (HashMap<String, Object> suggestion : suggestions) {
                            m_suggestedStops.add(StopInfo.get_stop_info(suggestion));
                        }
                    }
                    else{
                        m_suggestedStops = new ArrayList<>();
                    }

                    m_votes = (HashMap<String, HashMap<String, ArrayList<String>>>) value.get(Constants.KEY_VOTE);
                    if(m_votes != null && is_owner()){
                        tally_votes();
                    }
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

    public ArrayList<StopInfo> get_caravan_stops(){
        return m_route;
    }

    // Compile the user's information needed for the group
    private HashMap<String, ArrayList<String>> generate_user_info(){
        ArrayList<String> userInfo = new ArrayList<>(MemberData.size);
        userInfo.add(MemberData.Email, m_email);
        userInfo.add(MemberData.Name, m_displayName);
        userInfo.add(MemberData.ProfilePicture, m_profilePicture);
        userInfo.add(MemberData.fmcToken, Token);
        HashMap<String, ArrayList<String>> userInfoMap = new HashMap<>();
        userInfoMap.put(m_userID, userInfo);
        return userInfoMap;
    }

    // Upload user info to the group on Firestore
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

    // Helper function to update a field for the user's info in Firestore
    private void update_user_field(String key, String value){
        m_database.collection(KEY_COLLECTION_USERS)
                .document(m_userID)
                .update(key, value);
    }

    private void update_group_map(String key, Object value){
        HashMap<String, Object> update = new HashMap<>();
        update.put(key, value);
        m_database.collection(KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .set(update, SetOptions.merge());
    }

    private void batch_field_update(){
        m_database.collection(KEY_COLLECTION_GROUPS)
                .document(m_groupID)
                .set(m_batchFieldUpdate, SetOptions.merge());
        m_batchFieldUpdate.clear();
    }

    private void add_to_batch(String key, Object value){
        m_batchFieldUpdate.put(key, value);
    }

    private void record_vote(String placeID, boolean vote){
        // Copy voting info / make new HashMap if there is none
        HashMap<String, HashMap<String, ArrayList<String>>> votes = m_votes == null ? new HashMap<>() : new HashMap<>(m_votes);
        if(!votes.containsKey(placeID)) {
            votes.put(placeID, init_votes());
        }
        HashMap<String, ArrayList<String>> placeVote = votes.get(placeID);

        // Vote
        assert placeVote != null;
        boolean voted = Objects.requireNonNull(placeVote.get(MemberVotes.For)).contains(m_userID) ||
                Objects.requireNonNull(placeVote.get(MemberVotes.Against)).contains(m_userID);
        if (!voted) {
            String selection = vote ? MemberVotes.For : MemberVotes.Against;
            Objects.requireNonNull(placeVote.get(selection)).add(m_userID);

            votes.put(placeID, placeVote);

            // Update database
            HashMap<String, Object> voteMap = new HashMap<>();
            voteMap.put(Constants.KEY_VOTE, votes);
            m_database.collection(KEY_COLLECTION_GROUPS)
                    .document(m_groupID)
                    .set(voteMap, SetOptions.merge());
        }
    }

    private void tally_votes(){
        if(is_owner()) {
            Set<String> places = m_votes.keySet();
            for (String place : places) {
                HashMap<String, ArrayList<String>> votes = m_votes.get(place);
                int yes = votes.get(MemberVotes.For).size();
                int no = votes.get(MemberVotes.Against).size();
                int memberCount = m_members.size();
                if (yes + no == memberCount) {
                    remove_from_voting(place);
                    remove_suggested_stop(place);
                    if (yes > no) {
                        add_suggested_stop(place);
                    } else if (yes == no) {
                        if (votes.get(MemberVotes.For).contains(m_userID)){
                            add_suggested_stop(place);
                        }
                    }
                    batch_field_update();
                }
            }
        }
    }

    private void remove_suggested_stop(String placeID){
        assert m_suggestedStops != null;
        ArrayList<StopInfo> stops = new ArrayList<>(m_suggestedStops);
        for(int i = 0; i < stops.size(); i++){
            if(stops.get(i).equals(placeID)){
                stops.remove(i);
                break;
            }
        }
        add_to_batch(KEY_SUGG_STOPS, stops);
    }

    private void add_suggested_stop(String placeID){
        ArrayList<StopInfo> route = m_route != null ? new ArrayList<>(m_route) : new ArrayList<>();
        for(StopInfo suggestion : m_suggestedStops){
            if(suggestion.equals(placeID)){
                route.add(suggestion);
                break;
            }
        }
        add_to_batch(Constants.KEY_ROUTE, route);
    }

    private void remove_from_voting(String placeID){
        assert m_votes != null;
        HashMap<String, HashMap<String, ArrayList<String>>> votes = new HashMap<>(m_votes);
        votes.remove(placeID);

        add_to_batch(Constants.KEY_VOTE, votes);
    }

    // cleanup() is for getting the database (local class) in a state ready for another group
    private void cleanup(){
        remove_group_listener();
        if(m_suggestedStops != null){
            m_suggestedStops.clear();
        }

        m_votes = new HashMap<>();
        m_batchFieldUpdate = new HashMap<>();
        m_route = new ArrayList<>();
    }
}
