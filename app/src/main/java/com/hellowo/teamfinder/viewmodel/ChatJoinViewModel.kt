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
        loading.value =  true
        val ref = FirebaseDatabase.getInstance().reference
        ref.child(KEY_CHAT).child(chatId).child(KEY_MEMBERS).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                MeLiveData.value?.let {
                    mutableData.child(mutableData.childrenCount.toString()).value = it.id
                }
                return Transaction.success(mutableData)
            }
            override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot) {
                if(databaseError == null) {
                    MeLiveData.value?.let {
                        val newMessage = Message(
                                null,
                                it.nickName,
                                it.id,
                                it.photoUrl,
                                System.currentTimeMillis(),
                                1)

                        val childUpdates = HashMap<String, Any>()
                        val key = ref.child(KEY_MESSAGE).child(chatId).push().key

                        //
                        childUpdates.put("/${KEY_MESSAGE}/$chatId/$key", newMessage)

                        ref.updateChildren(childUpdates) { error, _ ->
                            if(error == null) {
                                loading.value = false
                                joined.value = true
                            }
                        }
                    }
                }
            }
        })
    }
}