package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.data.GameData;
import com.hellowo.teamfinder.model.Game;
import com.hellowo.teamfinder.model.Member;

import java.util.ArrayList;
import java.util.List;

public class CreateTeamViewModel extends ViewModel {
    public MutableLiveData<Game> selectedGame = new MutableLiveData<>();
    public MutableLiveData<List<Member>> currentMember = new MutableLiveData<>();
    private List<Member> memberList;

    public CreateTeamViewModel() {
        super();
        selectedGame.setValue(GameData.get().getGame("lol"));
        memberList = new ArrayList<>();
        memberList.add(ConnectedUserLiveData.get().getSnapshot()
                .makeMember(App.context.getString(R.string.free_role)));
        currentMember.setValue(memberList);
    }

    public void addNewMember() {

    }
}
