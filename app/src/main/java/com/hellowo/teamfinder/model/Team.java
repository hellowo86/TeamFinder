package com.hellowo.teamfinder.model;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team {
    public final static String DB_REF = "teams";
    public final static String KEY_FILTERING = "filteringKey";

    List<Member> members = new ArrayList<>();
    Map<String, Integer> roles = new HashMap<>();
    String id;
    long dtCreated;
    String title;
    int gameId;
    long dtActive;
    int status;
    int commentCount;

    public List<Member> getMembers() {
        return members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(long dtCreated) {
        this.dtCreated = dtCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public long getDtActive() {
        return dtActive;
    }

    public void setDtActive(long dtActive) {
        this.dtActive = dtActive;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilteringKey() {
        return dtActive + "_" + gameId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Map<String, Integer> getRoles() {
        return roles;
    }

    @Exclude
    public String makeMemberText() {
        int attendedMemberCount = 0;
        for (Member member : members) {
            if(!TextUtils.isEmpty(member.getUserId())) {
                attendedMemberCount++;
            }
        }
        return String.format(App.context.getString(R.string.member_count),
                attendedMemberCount, members.size());
    }

    @Exclude
    public String makeActiveTimeText() {
        if(dtActive == Long.MAX_VALUE) {
            return App.context.getString(R.string.active_infinity_time);
        }else {
            int h = (int) ((dtActive - System.currentTimeMillis()) / (1000 * 60 * 60));
            if(h < 1) {
                return App.context.getString(R.string.active_time_finish_soon);
            }else {
                return h + "h";
            }
        }
    }

    @Exclude
    public Member getOrganizer() {
        return members.get(0);
    }
}
