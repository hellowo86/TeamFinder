package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.util.ArrayMap
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.VisibleRegion
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.MyChatLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.KEY_CHAT


class MainViewModel : ViewModel() {
    var region: VisibleRegion? = null

    val chatRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(KEY_CHAT)
    val chatList: ArrayMap<String, Chat> = ArrayMap()
    val chats: MutableLiveData<ArrayMap<String, Chat>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val address: MutableLiveData<String> = MutableLiveData()
    val listener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) { loading.value = false }
        override fun onDataChange(snapshot: DataSnapshot?) {
            Log.d("aaa", "-------------------------")
            chatList.clear()
            snapshot?.children?.forEach {
                it.getValue(Chat::class.java)?.let { chat ->
                    chat.id = it.key
                    chatList.put(chat.id, chat)
                }
            }

            region?.let {
                chatList.filter { it.value.lng < (region as VisibleRegion).nearLeft.longitude
                                || it.value.lng > (region as VisibleRegion).farRight.longitude }
                        .map { chatList.remove(it.key) }
            }

            chatList.forEach {
                Log.d("aaa", it.toString())
            }

            chats.value = chatList
            loading.value = false
        }
    }

    init {
    }

    fun search(visibleRegion: VisibleRegion): Boolean {
        loading.value = true
        region = visibleRegion
        chatRef.orderByChild("lat")
                .startAt(visibleRegion.nearLeft.latitude)
                .endAt(visibleRegion.farRight.latitude)
                .addListenerForSingleValueEvent(listener)
        return true
    }
}
