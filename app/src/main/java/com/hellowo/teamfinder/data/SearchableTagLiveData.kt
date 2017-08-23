package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.util.ArrayMap
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.utils.KEY_CHAT
import com.hellowo.teamfinder.utils.KEY_DT_ENTERED
import com.hellowo.teamfinder.utils.KEY_LAST_CHECK_INDEX
import com.hellowo.teamfinder.utils.KEY_USERS

object SearchableTagLiveData : LiveData<List<HashTag>>() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    val chatRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(KEY_CHAT)

    val listenerMap: MutableMap<String, ValueEventListener> = HashMap()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    var currentUserId = ""
    var currentCount = 0


    init {
    }

    override fun onActive() {
    }

    override fun onInactive() {
    }

    fun clear() {
    }
}
