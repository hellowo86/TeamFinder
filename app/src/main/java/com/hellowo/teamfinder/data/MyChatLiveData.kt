package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.util.ArrayMap
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.KEY_CHAT
import com.hellowo.teamfinder.utils.KEY_DT_ENTERED
import com.hellowo.teamfinder.utils.KEY_LAST_CHECK_INDEX
import com.hellowo.teamfinder.utils.KEY_USERS

object MyChatLiveData : LiveData<ArrayMap<String, Chat>>() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    val chatRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(KEY_CHAT)
    val itemsMap: ArrayMap<String, Chat> = ArrayMap()
    val listenerMap: MutableMap<String, ValueEventListener> = HashMap()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    var currentUserId = ""
    var currentCount = 0

    val joinedChatEventListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(e: DatabaseError?) {}
        override fun onDataChange(chatListSnapshot: DataSnapshot?) {
            currentCount = chatListSnapshot?.children?.count() as Int
            if(currentCount > 0) {
                loading.value = currentCount != itemsMap.size
                val removedMap: MutableMap<String, Chat> = ArrayMap()
                removedMap.putAll(itemsMap)

                chatListSnapshot.children.forEach {
                    val chatId = it.key
                    val dtEntered = it.child(KEY_DT_ENTERED).value as Long
                    val lastCheckIndex = it.child(KEY_LAST_CHECK_INDEX).value.toString().toInt()
                    removedMap.remove(chatId)
                    if(listenerMap.containsKey(chatId)) {
                        itemsMap[chatId]?.dtEntered = dtEntered
                        itemsMap[chatId]?.lastCheckIndex = lastCheckIndex
                    }else {
                        val valueEventListener = object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {}
                            override fun onDataChange(chatSnapshot: DataSnapshot?) {
                                chatSnapshot?.getValue(Chat::class.java)?.let { chat ->
                                    chat.id = it.key
                                    chat.dtEntered = dtEntered
                                    chat.lastCheckIndex = lastCheckIndex
                                    itemsMap.put(chat.id, chat)
                                    value = itemsMap
                                }
                                loading.value = currentCount != itemsMap.size
                            }
                        }
                        listenerMap.put(chatId, valueEventListener)
                        chatRef.child(chatId).addValueEventListener(valueEventListener)
                    }
                }
                removedMap.forEach { itemsMap.remove(it.key) }
                value = itemsMap
            }else{
                itemsMap.clear()
                value = itemsMap
            }
            loading.value = currentCount != itemsMap.size
        }
    }

    init {
        loading.value = false
        value = itemsMap
    }

    override fun onActive() {
        currentUserId = MeLiveData.value?.id.toString()
        ref.child(KEY_USERS).child(currentUserId).child(KEY_CHAT).addValueEventListener(joinedChatEventListener)
    }

    override fun onInactive() {
        ref.child(KEY_USERS).child(currentUserId).child(KEY_CHAT).removeEventListener(joinedChatEventListener)
        listenerMap.forEach { chatRef.child(it.key).removeEventListener(it.value) }
        listenerMap.clear()
    }

    fun clear() {
        itemsMap.clear()
        value = itemsMap
    }
}
