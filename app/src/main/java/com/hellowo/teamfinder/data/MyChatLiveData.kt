package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.support.v4.util.ArrayMap
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.KEY_CHAT
import com.hellowo.teamfinder.utils.KEY_USERS

object MyChatLiveData : LiveData<ArrayMap<String, Chat>>() {
    internal val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal val chatRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(KEY_CHAT)
    internal val itemsMap: ArrayMap<String, Chat> = ArrayMap()
    internal val listenerMap: MutableMap<String, ValueEventListener> = HashMap()
    internal val joinedChatEventListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(chatListSnapshot: DataSnapshot?) {
            itemsMap.clear()
            if(chatListSnapshot?.children?.count() as Int > 0) {
                chatListSnapshot.children?.forEach {
                    val chatId = it.key
                    if(!listenerMap.containsKey(chatId)) {
                        val dtEntered = it.value as Long
                        val valueEventListener = object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {}
                            override fun onDataChange(chatSnapshot: DataSnapshot?) {
                                chatSnapshot?.getValue(Chat::class.java)?.let { chat ->
                                    chat.id = it.key
                                    chat.dtEntered = dtEntered
                                    itemsMap.put(chat.id, chat)
                                    value = itemsMap
                                }
                            }
                        }
                        listenerMap.put(chatId, valueEventListener)
                        chatRef.child(chatId).addValueEventListener(valueEventListener)
                    }
                }
            }else{
                value = itemsMap
            }
        }
    }
    internal lateinit var currentUserId: String

    init {
        value = itemsMap
    }

    override fun onActive() {
        MeLiveData.value?.id?.let {
            currentUserId = it
            ref.child(KEY_USERS)
                    .child(currentUserId)
                    .child(KEY_CHAT)
                    .addValueEventListener(joinedChatEventListener)
        }
    }

    override fun onInactive() {
        ref.child(KEY_USERS)
                .child(currentUserId)
                .child(KEY_CHAT)
                .removeEventListener(joinedChatEventListener)
        listenerMap.forEach {
            chatRef.child(it.key).removeEventListener(it.value)
        }
        listenerMap.clear()
    }

    fun clear() {
        itemsMap.clear()
        value = itemsMap
    }
}
