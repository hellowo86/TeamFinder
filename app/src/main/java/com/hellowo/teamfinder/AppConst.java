package com.hellowo.teamfinder;

public class AppConst {
    public final static String EXTRA_TEAM_ID = "team_id";
    public final static String EXTRA_USER_ID = "user_id";
    public final static String EXTRA_CHAT_ID = "chat_id";
    public final static String DB_KEY_DT_CREATED = "dtCreated";
    /*
    쿼리하는법
    databaseReference.orderByChild('_searchLastName')
                 .startAt(queryText)
                 .endAt(queryText+"\uf8ff")
                 .once("value")
     */
}
