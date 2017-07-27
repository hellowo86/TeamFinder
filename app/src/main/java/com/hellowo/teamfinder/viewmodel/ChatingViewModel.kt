package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.ViewModel
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Comment
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.utils.FirebaseUtils

class ChatingViewModel : ViewModel() {
    val chat = Chat()
    val messageList: MutableList<Message> = ArrayList()

    init {
    }

    override fun onCleared() {
        super.onCleared()

    }

    fun  postMessage(message: String) {
        MeLiveData.value?.id?.let {
            val message = Message(
                    message,
                    MeLiveData.value!!.nickName,
                    MeLiveData.value!!.id,
                    MeLiveData.value!!.photoUrl,
                    System.currentTimeMillis(),
                    0)
        }
    }
}
