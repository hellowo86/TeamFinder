package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.Editable;
import android.text.TextUtils;

import com.google.firebase.database.FirebaseDatabase;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.data.GameData;
import com.hellowo.teamfinder.model.Game;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.model.Team;

import java.util.List;

public class CreateTeamViewModel extends ViewModel {
    public MutableLiveData<Game> selectedGame = new MutableLiveData<>();
    public MutableLiveData<List<Member>> currentMember = new MutableLiveData<>();
    public MutableLiveData<Integer> addedMemberPos = new MutableLiveData<>();
    public MutableLiveData<Integer> deletedMemberPos = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFullMember = new MutableLiveData<>();
    public MutableLiveData<Boolean> isConfirmable = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<Boolean> confirmed = new MutableLiveData<>();
    private Team team;

    public CreateTeamViewModel() {
        super();
        team = new Team();
        selectedGame.setValue(GameData.get().getGame(0));
        team.getMembers().add(ConnectedUserLiveData.get().getSnapshot()
                .makeMember(App.context.getString(R.string.free_role)));
        team.setDtActive(Long.MAX_VALUE);
        currentMember.setValue(team.getMembers());
        isConfirmable.setValue(false);
    }

    public void addNewMember() {
        Member member = new Member();
        member.setRole(App.context.getString(R.string.free_role));
        member.setName(App.context.getString(R.string.team_member));
        team.getMembers().add(member);
        addedMemberPos.setValue(team.getMembers().size() - 1);
        if(selectedGame.getValue().getMaxMemberCount() == team.getMembers().size()) {
            isFullMember.setValue(true);
        }
        checkConfirmable();
    }

    public void deleteMember(Member member) {
        int removePos = team.getMembers().indexOf(member);
        team.getMembers().remove(member);
        deletedMemberPos.setValue(removePos);
        isFullMember.setValue(false);
        checkConfirmable();
    }

    public void selectGame(Game game) {
        team.setGameId(game.getId());
        selectedGame.setValue(game);

        if(game.getMaxMemberCount() < team.getMembers().size()) {
            for (int i = game.getMaxMemberCount(); i < team.getMembers().size(); i++) {
                team.getMembers().remove(i);
            }
        }

        for (Member member : team.getMembers()) {
            member.setRole(App.context.getString(R.string.free_role));
        }

        isFullMember.setValue(game.getMaxMemberCount() == team.getMembers().size());
        currentMember.setValue(team.getMembers());
    }

    public void setTitle(Editable s) {
        team.setTitle(s.toString());
        checkConfirmable();
    }

    private void checkConfirmable() {
        isConfirmable.setValue(!TextUtils.isEmpty(team.getTitle()) && team.getMembers().size() > 1);
    }

    public void setActiveTime(long activeTime) {
        team.setDtActive(activeTime);
    }

    public void saveTeam(String description) {
        loading.setValue(true);
        team.setDescription(description);
        team.setDtCreated(System.currentTimeMillis());
        team.setCommentCount(0);
        team.setStatus(0);

        String key = FirebaseDatabase.getInstance().getReference().child(Team.DB_REF).push().getKey();
        FirebaseDatabase.getInstance().getReference()
                .child(Team.DB_REF)
                .child(key)
                .setValue(team, (error, databaseReference)->{
                    loading.setValue(false);
                    confirmed.setValue(true);
                });
    }
}
