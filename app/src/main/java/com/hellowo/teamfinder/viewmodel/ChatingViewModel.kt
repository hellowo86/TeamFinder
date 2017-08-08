package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Comment
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.utils.FirebaseUtils

class ChatingViewModel : ViewModel() {
    val chat = MutableLiveData<Chat>()
    val messageList: MutableList<Message> = ArrayList()
    var loading = MutableLiveData<Boolean>()
    val newMessage = MutableLiveData<Message>()

    init {}

    val messageChildListener: ChildEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError?) {}

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            Log.d("aaa", "onChildChanged" + p0.toString())
        }

        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
            Log.d("aaa", "onChildAdded" + p0.toString())
            p0?.getValue(Message::class.java)?.let {
                messageList.add(0, it)
                newMessage.value = it
            }
        }

        override fun onChildRemoved(p0: DataSnapshot?) {
            Log.d("aaa", "onChildRemoved" + p0.toString())
        }
    }

    fun initChat(chatId: String?) {
        loading.value = true
        FirebaseDatabase.getInstance().reference.child(FirebaseUtils.KEY_CHAT)
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

        FirebaseDatabase.getInstance().reference.child(FirebaseUtils.KEY_MESSAGE)
                .child(chatId)
                .orderByChild(FirebaseUtils.KEY_DT_CREATED)
                .addChildEventListener(messageChildListener)
    }

    fun postMessage(message: String) {
        MeLiveData.value?.id?.let {
            val message = Message(
                    message,
                    MeLiveData.value!!.nickName,
                    MeLiveData.value!!.id,
                    MeLiveData.value!!.photoUrl,
                    System.currentTimeMillis(),
                    0)

            val chatId = chat.value!!.id

            if(!chatId.isNullOrBlank()) {
                Log.d("aaa", message.toString())
                val ref = FirebaseDatabase.getInstance().reference.child(FirebaseUtils.KEY_MESSAGE).child(chatId)
                val key = ref.push().key
                ref.child(key).setValue(message) { error, databaseReference ->

                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chat.value?.id?.let {
            FirebaseDatabase.getInstance().reference.child(FirebaseUtils.KEY_MESSAGE)
                    .child(it)
                    .removeEventListener(messageChildListener)
        }
    }
}
