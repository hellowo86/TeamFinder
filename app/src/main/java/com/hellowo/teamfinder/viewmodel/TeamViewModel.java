package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.hellowo.teamfinder.model.Team;

public class TeamViewModel extends ViewModel {
    public MutableLiveData<Team> team = new MutableLiveData<>();

}
