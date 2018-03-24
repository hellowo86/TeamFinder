package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.util.ArrayMap
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.MyChat
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.KEY_CHAT

class ChatFindViewModel : ViewModel() {
    val chatRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(KEY_CHAT)
    val chatList: ArrayMap<String, Chat> = ArrayMap()
    val chats: MutableLiveData<ArrayMap<String, Chat>> = MutableLiveData()
    val listener = object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(snapshot: DataSnapshot?) {
            chatList.clear()
            snapshot?.children?.forEach {
                it.getValue(Chat::class.java)?.let { chat ->
                    chat.id = it.key
                    chatList.put(chat.id, chat)
                }
            }
            chatList.filter { MyChat.itemsMap.containsKey(it.key) }
                    .map { chatList.remove(it.key) }
            chats.value = chatList
        }
    }

    init {
        chats.value = chatList
        search("")
    }

    fun  search(tag: String): Boolean {
        chatRef.orderByChild("hashTag/$tag").equalTo(true)
                .addListenerForSingleValueEvent(listener)
        return true
    }
}
