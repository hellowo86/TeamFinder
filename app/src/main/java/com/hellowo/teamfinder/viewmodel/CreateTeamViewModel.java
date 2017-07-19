package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.ArrayMap;
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
import java.util.Map;

public class CreateTeamViewModel extends ViewModel {
    public MutableLiveData<Game> selectedGame = new MutableLiveData<>();
    public MutableLiveData<Map<String, Integer>> currentRoles = new MutableLiveData<>();
    public MutableLiveData<Integer> needMemberSize = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFullMember = new MutableLiveData<>();
    public MutableLiveData<Boolean> isConfirmable = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<Boolean> confirmed = new MutableLiveData<>();
    private Team team;

    public CreateTeamViewModel() {
        super();
        team = new Team();
        team.getMembers().add(ConnectedUserLiveData.INSTANCE.getValue().makeMember(App.context.getString(R.string.free_role)));
        team.setDtActive(Long.MAX_VALUE);
        team.getRoles().put(App.context.getString(R.string.free_role), 1);
        needMemberSize.setValue(1);
        selectedGame.setValue(GameData.INSTANCE.getGames().get(0));
        currentRoles.setValue(team.getRoles());
        checkFullMember();
        checkConfirmable();
    }

    public void selectGame(Game game) {
        int prevGameId = selectedGame.getValue().getId();
        team.setGameId(game.getId());
        selectedGame.setValue(game);

        if(game.getId() != prevGameId) {
            for(String role : team.getRoles().keySet()) {
                if(role.equals(App.context.getString(R.string.free_role))) {
                    if(game.getMaxMemberCount() - 1 < team.getRoles().get(role)) {
                        team.getRoles().put(role, game.getMaxMemberCount());
                    }
                }else {
                    team.getRoles().remove(role);
                }
            }
            currentRoles.setValue(team.getRoles());
            checkFullMember();
            checkConfirmable();
        }
    }

    public void setContents(Editable s) {
        team.setTitle(s.toString());
        checkConfirmable();
    }

    private void checkFullMember() {
        isFullMember.setValue(needMemberSize.getValue() >= selectedGame.getValue().getMaxMemberCount() - 1);
    }

    private void checkConfirmable() {
        isConfirmable.setValue(!TextUtils.isEmpty(team.getTitle()) && needMemberSize.getValue() > 0);
    }

    public void setActiveTime(long activeTime) {
        team.setDtActive(activeTime);
    }

    public void saveTeam() {
        loading.setValue(true);
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

    public void setRole(String role, int delta) {
        if(delta > 0) {
            if(team.getRoles().containsKey(role)) {
                team.getRoles().put(role, team.getRoles().get(role) + delta);
            }else {
                team.getRoles().put(role, delta);
            }
        }else if(delta < 0){
            if(team.getRoles().containsKey(role) && team.getRoles().get(role) + delta > 0) {
                team.getRoles().put(role, team.getRoles().get(role) + delta);
            }else {
                team.getRoles().remove(role);
            }
        }else {

        }
        needMemberSize.setValue(needMemberSize.getValue() + delta);
        currentRoles.setValue(team.getRoles());
        checkFullMember();
        checkConfirmable();
    }
}
