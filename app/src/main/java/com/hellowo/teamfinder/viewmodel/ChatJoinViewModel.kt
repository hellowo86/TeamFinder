package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.utils.*
import java.util.HashMap

class ChatJoinViewModel : ViewModel() {
    val chat = MutableLiveData<Chat>()
    val loading = MutableLiveData<Boolean>()
    val isJoining = MutableLiveData<Boolean>()
    val joined = MutableLiveData<Boolean>()
    lateinit var chatId: String

    init {
        loading.value = false
    }

    fun initChat(chatId: String) {
        loading.value = true

        this.chatId = chatId

        FirebaseDatabase.getInstance().reference.child(KEY_CHAT)
                .child(chatId)
                .addListenerForSingleValueEvent( object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.getValue(Chat::class.java)
                        data?.id = dataSnapshot.key
                        chat.value = data
                        loading.value = false
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        loading.value = false
                    }
                })
    }

    fun joinChat() {
        isJoining.value =  true
        val ref = FirebaseDatabase.getInstance().reference
        MeLiveData.value?.let {
            ref.child(KEY_CHAT_MEMBERS).child(chatId).child(it.id).setValue(it.makeChatMember()) { e,_->
                if(e == null) {
                    val dtEntered = System.currentTimeMillis()
                    val newMessage = Message("", it.nickName, it.id, dtEntered, 1)
                    val childUpdates = HashMap<String, Any>()
                    val key = ref.child(KEY_MESSAGE).child(chatId).push().key

                    childUpdates.put("/$KEY_USERS/${it.id}/$KEY_CHAT/$chatId", dtEntered)
                    childUpdates.put("/$KEY_MESSAGE/$chatId/$key", newMessage)

                    ref.updateChildren(childUpdates) { error, _ ->
                        if(error == null) {
                            isJoining.value = false
                            joined.value = true
                        }
                    }
                }
            }
        }
    }
}
