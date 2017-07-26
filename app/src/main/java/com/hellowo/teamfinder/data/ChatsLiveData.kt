package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread
import com.google.firebase.database.*

import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Team

import java.util.ArrayList
import java.util.Collections
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener



object ChatsLiveData : LiveData<List<Chat>>() {
    internal val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal val currentList: MutableList<Chat> = ArrayList()
    internal val childEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            currentList.clear()

            for (postSnapshot in dataSnapshot.children) {
                val chat = postSnapshot.getValue(Chat::class.java)
                chat!!.id = postSnapshot.key
                currentList.add(0, chat)
            }

            Collections.sort(currentList) { l, r ->
                if (l.dtCreated > r.dtCreated) -1
                else if (l.dtCreated < r.dtCreated) 1
                else 0
            }

            value = currentList
        }
        override fun onCancelled(databaseError: DatabaseError) {}
    }

    init {
        value = currentList
    }

    override fun onActive() {
        mDatabase.child(Chat.DB_REF)
                .child(MeLiveData.value?.id ?: "_")
                .addValueEventListener(childEventListener)
    }

    override fun onInactive() {
        mDatabase.child(Chat.DB_REF)
                .child(MeLiveData.value?.id ?: "_")
                .removeEventListener(childEventListener)
    }
}
