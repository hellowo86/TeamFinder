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
    public MutableLiveData<Integer> addedMemberPos = new MutableLiveData<>();
    public MutableLiveData<Integer> deletedMemberPos = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFullMember = new MutableLiveData<>();
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
        Member member = new Member();
        member.setRole(App.context.getString(R.string.free_role));
        member.setName(App.context.getString(R.string.team_member));
        memberList.add(member);
        addedMemberPos.setValue(memberList.size() - 1);
        if(selectedGame.getValue().getMaxMemberCount() == memberList.size()) {
            isFullMember.setValue(true);
        }
    }

    public void deleteMember(Member member) {
        int removePos = memberList.indexOf(member);
        memberList.remove(member);
        deletedMemberPos.setValue(removePos);
        isFullMember.setValue(false);
    }

    public void selectGame(Game game) {
        selectedGame.setValue(game);

        if(game.getMaxMemberCount() < memberList.size()) {
            for (int i = game.getMaxMemberCount(); i < memberList.size(); i++) {
                memberList.remove(i);
            }
        }

        for (Member member : memberList) {
            member.setRole(App.context.getString(R.string.free_role));
        }

        isFullMember.setValue(game.getMaxMemberCount() == memberList.size());
        currentMember.setValue(memberList);
    }
}
