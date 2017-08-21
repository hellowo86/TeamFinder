package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.util.ArrayMap
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.data.TeamsLiveData
import com.hellowo.teamfinder.model.*
import com.hellowo.teamfinder.utils.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.util.*

class ChatingViewModel : ViewModel() {
    val messageList: MutableList<Message> = ArrayList()
    val typingList: MutableList<String> = ArrayList()
    val memberMap: ArrayMap<String, ChatMember> = ArrayMap()
    val currentChat: Chat = Chat()

    val chat = MutableLiveData<Chat>()
    val isReady = MutableLiveData<Boolean>()
    val messages = MutableLiveData<List<Message>>()
    val newMessage = MutableLiveData<Message>()
    val typings = MutableLiveData<List<String>>()
    val members = MutableLiveData<ArrayMap<String, ChatMember>>()
    val isUploading = MutableLiveData<Boolean>()
    val outOfChat = MutableLiveData<Boolean>()

    val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    val me = MeLiveData.value
    var lastTime: Long = System.currentTimeMillis()
    var chatLoading = false
    var messagesLoading = false
    var membersLoading = false
    var isTyping = false
    var isOut = false
    val limit = 100
    var chatId: String = ""
    var dtEntered: Long = 0

    val chatValueListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.getValue(Chat::class.java)?.let {
                currentChat.id = dataSnapshot.key
                currentChat.title = it.title
                currentChat.description = it.description
                currentChat.maxMemberCount = it.maxMemberCount
                currentChat.dtCreated = it.dtCreated
                currentChat.king = it.king
                currentChat.lastMessage = it.lastMessage
                currentChat.lastMessageTime = it.lastMessageTime
                currentChat.gameId = it.gameId
                currentChat.messageCount = it.messageCount
                chat.value = currentChat
            }
            chatLoading = false
            checkReady()
        }
        override fun onCancelled(databaseError: DatabaseError) {
            chatLoading = false
            checkReady()
        }
    }

    val messageListListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError?) {
            messagesLoading = false
            checkReady()
        }
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
            checkReady()
        }
    }

    val membersListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError?) {
            membersLoading = false
            checkReady()
        }
        override fun onDataChange(snapshot: DataSnapshot) {
            memberMap.clear()
            for (postSnapshot in snapshot.children) {
                postSnapshot.getValue(ChatMember::class.java)?.let {
                    memberMap.put(it.userId, it)
                }
            }
            members.value = memberMap
            membersLoading = false
            checkReady()
        }
    }

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

    val typingListListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError?) {}
        override fun onDataChange(snapshot: DataSnapshot) {
            typingList.clear()
            for (postSnapshot in snapshot.children) {
                postSnapshot.getValue(Boolean::class.java)?.let {
                    if(it) { typingList.add(postSnapshot.key) }
                }
            }
            typings.value = typingList
        }
    }

    fun initChat(chatId: String, dtEntered: Long) {
        isReady.value = false
        messagesLoading = true
        chatLoading = true
        membersLoading = true

        this.chatId = chatId
        this.dtEntered = dtEntered

        loadMessages(chatId, lastTime.toDouble())
        ref.child(KEY_MESSAGE).child(chatId).orderByChild(KEY_DT_CREATED).startAt(lastTime.toDouble())
                .addChildEventListener(messageAddListener)
        ref.child(KEY_TYPING).child(chatId).addValueEventListener(typingListListener)
        ref.child(KEY_CHAT).child(chatId).addValueEventListener(chatValueListener)
        ref.child(KEY_CHAT_MEMBERS).child(chatId).addValueEventListener(membersListener)
    }

    private fun loadMessages(chatId: String, lastTime: Double) {
        messagesLoading = true
        ref.child(KEY_MESSAGE)
                .child(chatId)
                .orderByChild(KEY_DT_CREATED)
                .startAt(dtEntered.toDouble())
                .endAt(lastTime)
                .limitToLast(limit)
                .addListenerForSingleValueEvent(messageListListener)
    }

    fun loadMoreMessages() {
        if(!messagesLoading && lastTime > 0) {
            loadMessages(chatId, lastTime.toDouble())
        }
    }

    fun postMessage(text: String, type: Int) {
        chat.value?.let {
            val newMessage = Message(text, me?.nickName, me?.id, System.currentTimeMillis(), type)

            val childUpdates = HashMap<String, Any>()
            val key = ref.child(KEY_MESSAGE).child(chatId).push().key

            childUpdates.put("/$KEY_MESSAGE/$chatId/$key", newMessage)
            childUpdates.put("/$KEY_CHAT/$chatId/$KEY_LAST_MESSAGE", if(type == 0) text else App.context.getString(R.string.photo))
            childUpdates.put("/$KEY_CHAT/$chatId/$KEY_LAST_MESSAGE_TIME", newMessage.dtCreated)

            ref.updateChildren(childUpdates) { e, _ ->
                if(e == null) {
                    increaseMessageCount()
                }
            }
        }
    }

    private fun increaseMessageCount() {
        ref.child(KEY_CHAT).child(chatId).child(KEY_MESSAGE_COUNT).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                if(mutableData.value == null) {
                    mutableData.value = 1
                }else {
                    mutableData.value = mutableData.value.toString().toInt() + 1
                }
                return Transaction.success(mutableData)
            }
            override fun onComplete(e: DatabaseError?, c: Boolean, d: DataSnapshot) {
                if(e == null) {}
            }
        })
    }

    fun typingText(text: CharSequence) {
        if((text.isNotEmpty() && !isTyping) || (text.isEmpty() && isTyping)) {
            isTyping = !isTyping
            ref.child(KEY_TYPING).child(chatId).child(me?.id).setValue(isTyping)
        }
    }

    fun getlastPostionDate(itemPos: Int) = messageList[itemPos].dtCreated

    fun loginChat() {
        me?.let {
            val childUpdates = HashMap<String, Any>()
            childUpdates.put("/$KEY_CHAT_MEMBERS/$chatId/${it.id}/$KEY_LIVE", true)
            childUpdates.put("/$KEY_CHAT_MEMBERS/$chatId/${it.id}/$KEY_LAST_CONNECTED_TIME", System.currentTimeMillis())
            ref.updateChildren(childUpdates) { _, _ -> }
        }
    }

    fun logoutChat() {
        if(!isOut) {
            isTyping = false
            me?.let {
                val childUpdates = HashMap<String, Any>()
                childUpdates.put("/$KEY_CHAT_MEMBERS/$chatId/${it.id}/$KEY_LIVE", false)
                childUpdates.put("/$KEY_CHAT_MEMBERS/$chatId/${it.id}/$KEY_LAST_CONNECTED_TIME", System.currentTimeMillis())
                childUpdates.put("/$KEY_TYPING/$chatId/${it.id}", isTyping)
                ref.updateChildren(childUpdates) { _, _ -> }
            }
        }
    }

    fun checkReady() {
        isReady.value = !chatLoading && !messagesLoading && !membersLoading
    }

    override fun onCleared() {
        super.onCleared()
        ref.child(KEY_MESSAGE).child(chatId).removeEventListener(messageAddListener)
        ref.child(KEY_TYPING).child(chatId).removeEventListener(typingListListener)
        ref.child(KEY_CHAT).child(chatId).removeEventListener(chatValueListener)
        ref.child(KEY_CHAT_MEMBERS).child(chatId).removeEventListener(membersListener)
    }

    fun outOfChat() {
        me?.let {
            val newMessage = Message("", it.nickName, it.id, System.currentTimeMillis(), 2)
            val key = ref.child(KEY_MESSAGE).child(chatId).push().key
            val childUpdates = HashMap<String, Any?>()
            childUpdates.put("/$KEY_MESSAGE/$chatId/$key", newMessage)
            childUpdates.put("/$KEY_CHAT_MEMBERS/$chatId/${it.id}", null)
            childUpdates.put("/$KEY_TYPING/$chatId/${it.id}", null)
            childUpdates.put("/$KEY_USERS/${it.id}/$KEY_CHAT/$chatId", null)

            ref.updateChildren(childUpdates) { error, _ ->
                if(error == null) {
                    isOut = true
                    outOfChat.value = isOut
                }
            }
        }
    }

    fun sendPhotoMessage(uri: Uri?) {
        uri?.let {
            try {
                isUploading.value = true
                val filePath = FileUtil.getPath(App.context, uri)
                val bitmap = BitmapUtil.makeProfileBitmapFromFile(filePath)

                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                        "gs://teamfinder-32133.appspot.com/chatPhoto/$chatId/${System.currentTimeMillis()}.jpg")

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val bis = ByteArrayInputStream(data)
                val uploadTask = storageRef.putStream(bis)

                uploadTask.addOnFailureListener { e ->
                    bitmap.recycle()
                    isUploading.value = false
                }.addOnSuccessListener { taskSnapshot ->
                    bitmap.recycle()
                    taskSnapshot.downloadUrl?.let{ postMessage(it.toString(), 3) }
                    isUploading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isUploading.value = false
            }
        }
    }
}