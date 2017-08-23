package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
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

        FirebaseDatabase.getInstance().reference.child(KEY_CHAT).child(chatId)
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
            ref.child(KEY_CHAT).child(chatId).child(KEY_MESSAGE_COUNT)
                    .addListenerForSingleValueEvent( object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val childUpdates = HashMap<String, Any>()
                            val message_count = dataSnapshot.value.toString().toInt()
                            val dtEntered = System.currentTimeMillis()
                            val newMessage = Message("", it.nickName, it.id, dtEntered, 1)
                            val key = ref.child(KEY_MESSAGE).child(chatId).push().key
                            val chatMember = it.makeChatMember()
                            chatMember.pushToken = FirebaseInstanceId.getInstance().token

                            childUpdates.put("/$KEY_CHAT_MEMBERS/$chatId/${it.id}", chatMember)
                            childUpdates.put("/$KEY_USERS/${it.id}/$KEY_CHAT/$chatId/$KEY_DT_ENTERED", dtEntered)
                            childUpdates.put("/$KEY_USERS/${it.id}/$KEY_CHAT/$chatId/$KEY_LAST_CHECK_INDEX", message_count)
                            childUpdates.put("/$KEY_MESSAGE/$chatId/$key", newMessage)

                            ref.updateChildren(childUpdates) { error, _ ->
                                if(error == null) {
                                    isJoining.value = false
                                    joined.value = true
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
        }
    }
}
