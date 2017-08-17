package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.text.TextUtils

import com.google.firebase.database.FirebaseDatabase
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.data.GameData
import com.hellowo.teamfinder.model.Game
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.utils.KEY_TEAMS

class CreateTeamViewModel : ViewModel() {
    var selectedGame = MutableLiveData<Game>()
    var currentRoles = MutableLiveData<Map<String, Int>>()
    var needMemberSize = MutableLiveData<Int>()
    var isFullMember = MutableLiveData<Boolean>()
    var isConfirmable = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()
    var confirmed = MutableLiveData<Boolean>()
    private val team: Team

    init {
        team = Team()
        team.members.add(MeLiveData.value!!.makeMember(App.context.getString(R.string.free_role)))
        team.dtActive = java.lang.Long.MAX_VALUE
        team.roles.put(App.context.getString(R.string.free_role), 1)
        needMemberSize.value = 1
        selectedGame.value = GameData.games[0]
        currentRoles.value = team.roles
        checkFullMember()
        checkConfirmable()
    }

    fun selectGame(game: Game) {
        val prevGameId = selectedGame.value!!.id
        team.gameId = game.id
        selectedGame.setValue(game)

        if (game.id != prevGameId) {
            for (role in team.roles.keys) {
                if (role == App.context.getString(R.string.free_role)) {
                } else {
                    team.roles.remove(role)
                }
            }
            currentRoles.setValue(team.roles)
            checkFullMember()
            checkConfirmable()
        }
    }

    fun setContents(s: Editable) {
        team.title = s.toString()
        checkConfirmable()
    }

    private fun checkFullMember() {
        isFullMember.setValue(needMemberSize.value!! >= selectedGame.value!!.maxMemberCount - 1)
    }

    private fun checkConfirmable() {
    }

    fun setActiveTime(activeTime: Long) {
        team.dtActive = activeTime
    }

    fun saveTeam() {
        loading.setValue(true)
        team.dtCreated = System.currentTimeMillis()
        team.commentCount = 0
        team.status = 0

        val key = FirebaseDatabase.getInstance().reference.child(KEY_TEAMS).push().key
        FirebaseDatabase.getInstance().reference
                .child(KEY_TEAMS)
                .child(key)
                .setValue(team) { error, databaseReference ->
                    loading.setValue(false)
                    confirmed.setValue(true)
                }
    }

    fun setRole(role: String, delta: Int) {
        if (delta > 0) {
        } else if (delta < 0) {
        } else {

        }
        needMemberSize.setValue(needMemberSize.value!! + delta)
        currentRoles.setValue(team.roles)
        checkFullMember()
        checkConfirmable()
    }
}

