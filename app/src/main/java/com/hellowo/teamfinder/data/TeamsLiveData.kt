package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.utils.KEY_FILTERING
import com.hellowo.teamfinder.utils.KEY_TEAMS

import java.util.ArrayList
import java.util.Collections

object TeamsLiveData : LiveData<List<Team>>() {
    internal val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal val currentList: MutableList<Team> = ArrayList()

    init {
        value = currentList
    }

    fun loadTeams() {
        mDatabase.child(KEY_TEAMS)
                .orderByChild(KEY_FILTERING)
                .startAt(System.currentTimeMillis().toString() + "_0")
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                currentList.clear()

                                for (postSnapshot in dataSnapshot.children) {
                                    val team = postSnapshot.getValue(Team::class.java)
                                    team!!.id = postSnapshot.key
                                    currentList.add(0, team)
                                }

                                Collections.sort(currentList) { l, r ->
                                    if (l.dtCreated > r.dtCreated) -1
                                    else if (l.dtCreated < r.dtCreated) 1
                                    else 0
                                }

                                value = currentList
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
    }

    override fun onActive() {
        loadTeams()
    }
}
