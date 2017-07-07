package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.data.TeamsLiveData;

public class MainViewModel extends ViewModel {
    public enum ViewMope{SearchTeam}
    public ConnectedUserLiveData connectedUserLiveData = ConnectedUserLiveData.get();
    public MutableLiveData<ViewMope> viewMode = new MutableLiveData<>();
    public TeamsLiveData teamsLiveData = TeamsLiveData.get();

    public MainViewModel() {
        viewMode.setValue(ViewMope.SearchTeam);
    }

}
