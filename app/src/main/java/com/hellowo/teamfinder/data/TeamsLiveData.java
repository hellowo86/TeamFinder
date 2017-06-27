package com.hellowo.teamfinder.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.MainThread;
import android.util.Log;
import android.util.TimeUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.model.User;

import java.util.ArrayList;
import java.util.List;

public class TeamsLiveData extends LiveData<List<Team>> {
    private static TeamsLiveData sInstance;
    private DatabaseReference mDatabase;
    private List<Team> currentList;

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

    public void getTeams() {
        mDatabase.child(Team.DB_REF)
                .orderByKey()
                .orderByChild(Team.KEY_DT_CREATED)
                .startAt(System.currentTimeMillis(), Team.KEY_DT_ACTIVE)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    Team team = postSnapshot.getValue(Team.class);
                                    currentList.add(team);
                                }
                                setValue(currentList);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
    }

    @Override
    protected void onActive() {
        getTeams();
    }

}
