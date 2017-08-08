package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread
import android.util.Log
import com.google.firebase.database.*

import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Team

import java.util.ArrayList
import java.util.Collections
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.utils.FirebaseUtils


object ChatsLiveData : LiveData<List<Chat>>() {
    internal val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal val currentList: MutableList<Chat> = ArrayList()
    internal val joinedChatEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            currentList.clear()

            for (postSnapshot in dataSnapshot.children) {
                postSnapshot.getValue(Chat::class.java)?.let {
                    it.id = postSnapshot.key
                    currentList.add(it)
                }
            }

            value = currentList
        }
        override fun onCancelled(databaseError: DatabaseError) {}
    }
    internal lateinit var currentUserId: String

    init {
        value = currentList
    }

    override fun onActive() {
        currentUserId = MeLiveData.value?.id ?: "_"
        mDatabase.child(FirebaseUtils.KEY_USERS)
                .child(currentUserId)
                .child(FirebaseUtils.KEY_CHAT)
                .addValueEventListener(joinedChatEventListener)
    }

    override fun onInactive() {
        mDatabase.child(FirebaseUtils.KEY_USERS)
                .child(currentUserId)
                .child(FirebaseUtils.KEY_CHAT)
                .removeEventListener(joinedChatEventListener)
    }
}
