package com.example.caravan.Constant;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_COLLECTION_GROUPS = "groups";
    public static final String KEY_GROUP_MEMBERS = "members";
    public static final String KEY_GROUP_ID = "groupID";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_GROUP_OWNER = "groupOwner";
    public static final String KEY_CHAT = "chat";
    public static final String KEY_MEMBER_LOCATIONS = "memberLocations";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_SENDER_EMAIL = "senderEmail";
    public static final String KEY_CURRENT_LOCATION = "currentLocation";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_NAME = "displayName";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION,"key=AAAAAQUSFTc:APA91bEqVttlQUxYkjfSPD_X2IpXXRxGR4yk3qSsxm-1mlHg3sfgKXeXGOA-wp5_-Oe1VDEtnGhrHDv24bCcly_eHI3HTswPzeXCcWyqm-V5HKciGf1ws-9DQcP5HPTO4K3an4r-z_Su");
            remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE,"application/json"); }
            return remoteMsgHeaders; }

    public static final String KEY_STOPS = "stops";
    public static final String KEY_DESTINATIONS = "destinations";
    public static final String KEY_ROUTE = "route";
}
