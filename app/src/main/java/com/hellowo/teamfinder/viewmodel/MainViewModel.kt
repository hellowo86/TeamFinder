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
    enum class BottomTab {Find, ChatList, Clan, Profile}
    var bottomTab = MutableLiveData<BottomTab>()
    var location = MutableLiveData<LatLng>()

    val chatRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(KEY_CHAT)
    val chatList: ArrayMap<String, Chat> = ArrayMap()
    val chats: MutableLiveData<ArrayMap<String, Chat>> = MutableLiveData()
    val listener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {}
        override fun onDataChange(snapshot: DataSnapshot?) {
            chatList.clear()
            snapshot?.children?.forEach {
                it.getValue(Chat::class.java)?.let { chat ->
                    chat.id = it.key
                    chatList.put(chat.id, chat)
                    Log.d("aaa", chat.toString())
                }
            }
            chats.value = chatList
        }
    }

    init {
        bottomTab.value = BottomTab.ChatList
    }

    fun search(visibleRegion: VisibleRegion): Boolean {
        chatRef.orderByChild("latlng")
                .startAt("${visibleRegion.nearLeft.latitude}_${visibleRegion.nearLeft.longitude}")
                .endAt("${visibleRegion.farRight.latitude}_${visibleRegion.farRight.longitude}")
                .addListenerForSingleValueEvent(listener)
        return true
    }
}
