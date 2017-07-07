package com.hellowo.teamfinder.model;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;

public class Member {
    String userId;
    String name;
    String photoUrl;
    String role;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Exclude
    public boolean isMe() {
        return userId != null && ConnectedUserLiveData.get().getValue().getId().equals(userId);
    }

    @Exclude
    public boolean isJoinable() {
        return TextUtils.isEmpty(userId);
    }
}
