package com.hellowo.teamfinder.model;

import java.util.List;

public class Team {
    List<User> members;
    long dtCreated;
    String title;
    String description;
    String gameId;
    long dtActive;
    long dtStart;
    long dtEnd;
    List<String> options;
    int Status;

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
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

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
