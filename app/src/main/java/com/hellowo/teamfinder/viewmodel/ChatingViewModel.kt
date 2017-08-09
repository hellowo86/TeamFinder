package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.data.TeamsLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Comment
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.utils.FirebaseUtils
import java.text.DateFormat
import java.util.*

class ChatingViewModel : ViewModel() {
    val limit = 100
    val chat = MutableLiveData<Chat>()
    val messageList: MutableList<Message> = ArrayList()
    val loading = MutableLiveData<Boolean>()
    val messages = MutableLiveData<List<Message>>()
    val newMessage = MutableLiveData<Message>()
    val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    var lastTime: Long = System.currentTimeMillis()
    var messagesLoading: Boolean = false

    init {}

    val messageAddListener: ChildEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.getValue(Message::class.java)?.let {
                messageList.add(0, it)
                newMessage.value = it
            }
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {}
    }

    val messageListListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError?) { messagesLoading = false }
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentPos = messageList.size
            for (postSnapshot in snapshot.children) {
                postSnapshot.getValue(Message::class.java)?.let {
                    messageList.add(currentPos, it)
                }
            }

            if(snapshot.children.count() > 0) {
                lastTime = messageList[messageList.size - 1].dtCreated - 1
            }else {
                lastTime = 0
            }
            messages.value = messageList
            messagesLoading = false
        }
    }

    fun initChat(chatId: String) {
        loading.value = true

        loadMessages(chatId, lastTime.toDouble())

        ref.child(FirebaseUtils.KEY_MESSAGE)
                .child(chatId)
                .orderByChild(FirebaseUtils.KEY_DT_CREATED)
                .startAt(lastTime.toDouble())
                .addChildEventListener(messageAddListener)

        ref.child(FirebaseUtils.KEY_CHAT)
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

    private fun loadMessages(chatId: String, lastTime: Double) {
        messagesLoading = true
        ref.child(FirebaseUtils.KEY_MESSAGE)
                .child(chatId)
                .orderByChild(FirebaseUtils.KEY_DT_CREATED)
                .endAt(lastTime)
                .limitToLast(limit)
                .addListenerForSingleValueEvent(messageListListener)
    }

    fun loadMoreMessages() {
        if(!messagesLoading && lastTime > 0) {
            chat.value?.id?.let { loadMessages(it, lastTime.toDouble()) }
        }
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
                    .removeEventListener(messageAddListener)
        }
    }

    fun getlastPostionDate(findLastVisibleItemPosition: Int) = messageList[findLastVisibleItemPosition].dtCreated
}