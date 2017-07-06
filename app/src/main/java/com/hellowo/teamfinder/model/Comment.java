package com.hellowo.teamfinder.model;

import com.google.firebase.database.Exclude;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;

import java.text.DateFormat;
import java.util.Date;

public class Comment {
    public final static String DB_REF = "comments";

    String text;
    String userName;
    String userId;
    long dtCreated;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(long dtCreated) {
        this.dtCreated = dtCreated;
    }

    @Exclude
    public String makeActiveTimeText() {
        long m = (System.currentTimeMillis() - dtCreated) / (1000 * 60);
        if(m < 10) {
            return App.context.getString(R.string.just_before);
        }else if(m < 60){
            return String.format(App.context.getString(R.string.min_before), m);
        }else if(m < 60 * 24){
            return String.format(App.context.getString(R.string.hour_before), m / 60);
        }else {
            return DateFormat.getDateTimeInstance().format(new Date(dtCreated));
        }
    }
}
