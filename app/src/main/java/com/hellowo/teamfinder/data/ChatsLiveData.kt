package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Team

import java.util.ArrayList
import java.util.Collections

object ChatsLiveData : LiveData<List<Chat>>() {
    internal val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal val currentList: MutableList<Chat> = ArrayList()

    init {
        value = currentList
    }

    fun loadTeams() {
        mDatabase.child(Chat.DB_REF)
                .child(MeLiveData.value?.id ?: "_")
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                currentList.clear()

                                for (postSnapshot in dataSnapshot.children) {
                                    val chat = postSnapshot.getValue(Chat::class.java)
                                    chat!!.id = postSnapshot.key
                                    currentList.add(0, chat)
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
