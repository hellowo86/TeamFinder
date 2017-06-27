package com.hellowo.teamfinder.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    public final static String DB_REF = "teams";
    public final static String KEY_DT_CREATED = "dtCreated";
    public final static String KEY_DT_ACTIVE = "dtActive";

    List<Member> members = new ArrayList<>();
    String id;
    long dtCreated;
    String title;
    String description;
    int gameId;
    long dtActive;
    long dtStart;
    long dtEnd;
    int Status;



    public List<Member> getMembers() {
        return members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public long getDtStart() {
        return dtStart;
    }

    public void setDtStart(long dtStart) {
        this.dtStart = dtStart;
    }

    public long getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(long dtEnd) {
        this.dtEnd = dtEnd;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "Team{" +
                "members=" + members +
                ", id='" + id + '\'' +
                ", dtCreated=" + dtCreated +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", gameId=" + gameId +
                ", dtActive=" + dtActive +
                ", dtStart=" + dtStart +
                ", dtEnd=" + dtEnd +
                ", Status=" + Status +
                '}';
    }
}
