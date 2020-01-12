package practice.und3i2c0v3i2.dusterchat;

public interface Contract {

    public static final String NODE_USERS = "users";
    public static final String NODE_GROUPS = "groups";
    public static final String NODE_CONTACTS = "contacts";
    public static final String NODE_CHAT_REQUESTS = "chat_requests";
    public static final String NODE_CHAT_MESSAGES = "chat_messages";

    public static final String PROFILE_IMAGES = "profile_images";
    public static final String FRIEND_ID = "friend_id";
    public static final String REQ_STATUS = "request_status";
    public static final String STATUS_SENT = "sent_req";
    public static final String STATUS_RECEIVED = "received_req";


    public static final String RECEIVER_UID = "receiver_uid";

    /* profile */
    public static final String CURRENT_UID = "current_uid";
    public static final String USERNAME = "username";
    public static final String STATUS = "status";
    public static final String PROFILE_IMG = "imgUrl";

    /* personal */
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String WEB_PAGE = "webPage";

    /* social */
    public static final String LINKED_IN = "linkedIn";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";


    /* chat */
    public static final String MESSAGE = "message";
    public static final String TIMESTAMP = "timestamp";
    public static final String TYPE = "type";
    public static final String FROM = "from";

}
