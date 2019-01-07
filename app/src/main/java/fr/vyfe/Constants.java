package fr.vyfe;

public final class Constants {

    public final static String FIREBASE_DB_VERSION_URL = "https://vyfe-dev-8702b.firebaseio.com/";
    public static final int DAY_TO_MILLISECOND_FACTOR = 86400000; // Operation = 24(day) * 60(hour) * 60(minute) * 1000 (millis)
    public static final int UNIT_TO_MILLI_FACTOR = 1000;
    public static final int SPLASH_TIME_OUT = 300;
    public static final int STANDARD_LEFT_OFFSET = 6;
    public static final int STANDARD_RIGHT_OFFSET = 3;
    public static final String SESSIONMODEL_EXTRA = "SessionModel";
    public static final String SESSIONMODELID_EXTRA = "SessionModelId";
    public static final String VYFE = "Vyfe";

    public static final String BDDV2_LICENSES_KEY = "Licenses";
    public static final String BDDV2_LICENSES_START_KEY = "start";
    public static final String BDDV2_LICENSES_END_KEY = "end";
    public static final String BDDV2_LICENSES_IDUSER_KEY = "idUser";
    public static final String BDDV2_USERS_KEY = "users";
    public static final String BDDV2_USERS_PROFILE_KEY = "profile";
    public static final String BDDV2_USERS_PROFILE_FIRSTNAME_KEY = "firstName";
    public static final String BDDV2_USERS_PROFILE_LASTNAME_KEY = "lastName";
    public static final String BDDV2_USERS_PROFILE_PROMO_KEY = "promo";
    public static final String BDDV2_USERS_PROFILE_EMAIL_KEY = "email";
    public static final String BDDV2_USERS_ROLE_KEY = "roles";
    public static final String BDDV2_USERS_TEMPLATES_KEY = "templates";
    public static final String BDDV2_USERS_ROLE_TEACHER_KEY = "teacher";
    public static final String BDDV2_USERS_ROLE_ADMIN_KEY = "admin";
    public static final String BDDV2_USERS_ROLE_VIEWER_KEY = "viewer";
    public static final String BDDV2_USERS_AUTHORIZESESSIONS_KEY = "authorizeSessions";
    public static final String BDDV2_USERS_TAGSETS_KEY = "tagSets";
    public static final String BDDV2_USERS_TAGS_KEY = "tags";
    public static final String BDDV2_SESSIONS_KEY = "sessions";
    public static final String BDDV2_VIMEOTOKEN = "vimeoAccessToken";
    public static final String BDDV2_SESSIONS_VIMEOLINK = "videoLink";
    public static final String VIMEO_API_VIDEOS_ENDPOINT = "https://api.vimeo.com/me/videos";
    public static final String VIMEO_TOKEN_EXTRA = "vimeo_token";
    public static final String COMPANYID_EXTRA = "company_id";

    public static final String SESSIONTITLE_EXTRA = "sessionTitle";
    public static final String TAGSETID_EXTRA = "tagSetID";
}
