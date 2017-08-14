package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.support.v4.util.ArrayMap
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.FirebaseUtils


object ChatsLiveData : LiveData<ArrayMap<String, Chat>>() {
    internal val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal val chatRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(FirebaseUtils.KEY_CHAT)
    internal val itemsMap: ArrayMap<String, Chat> = ArrayMap()
    internal val listenerMap: MutableMap<String, ValueEventListener> = HashMap()
    internal val joinedChatEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
        override fun onChildChanged(snapshot: DataSnapshot?, p1: String?) {}
        override fun onChildAdded(snapshot: DataSnapshot?, p1: String?) {
            snapshot?.key?.let {
                val valueEventListener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(chatSnapshot: DataSnapshot?) {
                        val chat = chatSnapshot?.getValue(Chat::class.java)
                        chat?.id = chatSnapshot?.key
                        chat?.id?.let {
                            itemsMap.put(it, chat)
                            value = itemsMap
                        }
                    }
                }
                listenerMap.put(it, valueEventListener)
                chatRef.child(it).addValueEventListener(valueEventListener)
            }
        }
        override fun onChildRemoved(snapshot: DataSnapshot?) {
            val chat = itemsMap[snapshot?.key]
            chat?.let {
                chatRef.child(it.id).removeEventListener(listenerMap.remove(it.id))
                itemsMap.remove(it.id)
                value = itemsMap
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {}
    }
    internal lateinit var currentUserId: String

    init {
        value = itemsMap
    }

    override fun onActive() {
        currentUserId = MeLiveData.value?.id ?: "_"
        ref.child(FirebaseUtils.KEY_USERS)
                .child(currentUserId)
                .child(FirebaseUtils.KEY_CHAT)
                .addChildEventListener(joinedChatEventListener)
    }

    override fun onInactive() {
        ref.child(FirebaseUtils.KEY_USERS)
                .child(currentUserId)
                .child(FirebaseUtils.KEY_CHAT)
                .removeEventListener(joinedChatEventListener)

        listenerMap.forEach {
            chatRef.child(it.key).removeEventListener(it.value)
        }

        listenerMap.clear()
        //itemsMap.clear()
    }
}
