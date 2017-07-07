package com.hellowo.teamfinder.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.MainThread;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hellowo.teamfinder.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamsLiveData extends LiveData<List<Team>> {
    private static TeamsLiveData sInstance;
    private DatabaseReference mDatabase;
    private List<Team> currentList;
    int gameId = -1;

    @MainThread
    public static TeamsLiveData get() {
        if (sInstance == null) {
            sInstance = new TeamsLiveData();
        }
        return sInstance;
    }

    private TeamsLiveData() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentList = new ArrayList<>();
        setValue(currentList);
    }

    public void loadTeams() {
        mDatabase.child(Team.DB_REF)
                .orderByChild(Team.KEY_FILTERING)
                .startAt(System.currentTimeMillis() + "_0")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentList.clear();
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    Team team = postSnapshot.getValue(Team.class);
                                    team.setId(postSnapshot.getKey());
                                    currentList.add(0, team);
                                }
                                Collections.sort(currentList, (l,r)->
                                    l.getDtCreated() > r.getDtCreated() ? -1 :
                                            l.getDtCreated() < r.getDtCreated() ? 1 : 0);
                                setValue(currentList);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
    }

    @Override
    protected void onActive() {
        loadTeams();
    }

}
