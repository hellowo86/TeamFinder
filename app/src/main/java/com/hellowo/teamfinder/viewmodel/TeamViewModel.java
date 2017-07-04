package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hellowo.teamfinder.model.Team;

import java.util.Collections;

public class TeamViewModel extends ViewModel {
    public MutableLiveData<Team> team = new MutableLiveData<>();
    private String teamId;
    public MutableLiveData<Integer> currentPage = new MutableLiveData<>();

    public TeamViewModel() {
        currentPage.setValue(0);
    }

    public void initTeam(String teamId) {
        this.teamId = teamId;
        getTeam();
    }

     public void getTeam(){
         FirebaseDatabase.getInstance().getReference().child(Team.DB_REF)
                 .child(teamId)
                 .addListenerForSingleValueEvent(
                         new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 team.setValue(dataSnapshot.getValue(Team.class));
                             }
                             @Override
                             public void onCancelled(DatabaseError databaseError) {}
                         });
     }
}
