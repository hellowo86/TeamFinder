package com.hellowo.teamfinder.model;

import android.text.TextUtils;

public class User {
    public final static String DB_REF = "users";
    public final static String KEY_PUSH_TOKEN = "pushToken";
    public final static String KEY_PHOTO_URL = "photoUrl";

    String id;
    String nickName;
    String email;
    String photoUrl;
    String pushToken;
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

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public long getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(long dtCreated) {
        this.dtCreated = dtCreated;
    }

    public Member makeMember(String role) {
        Member member = new Member();
        member.setName(nickName);
        if(!TextUtils.isEmpty(photoUrl)) {
            member.setPhotoUrl(photoUrl.substring(0, photoUrl.indexOf("&token")));
        }
        member.setUserId(id);
        member.setRole(role);
        return member;
    }
}
