package com.hellowo.teamfinder.model;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;

public class User {
    public final static String DB_REF = "users";
    public final static String KEY_PHOTO_URL = "photoUrl";

    String id;
    String nickName;
    String email;
    String photoUrl;
    int gender;
    long dtCreated;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(long dtCreated) {
        this.dtCreated = dtCreated;
    }

    @Exclude
    public Member makeMember(String role) {
        return new Member(id, nickName, makePublicPhotoUrl(id), role);
    }

    @Exclude
    public static String makePublicPhotoUrl(String userId) {
        if(!TextUtils.isEmpty(userId)) {
            return "https://firebasestorage.googleapis.com/v0/b/teamfinder-32133.appspot.com/o/userPhoto%2F"
                    + userId + ".jpg?alt=media";
        }else{
            return "";
        }
    }
}
