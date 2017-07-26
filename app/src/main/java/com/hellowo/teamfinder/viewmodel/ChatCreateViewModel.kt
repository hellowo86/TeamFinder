package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import com.google.firebase.database.FirebaseDatabase
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.*

class ChatCreateViewModel : ViewModel() {
    enum class CurrentProgress {Title, Contents, Options}
    val currentProgress = MutableLiveData<CurrentProgress>()
    val checkCanGoNextProgress = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val confirmed = MutableLiveData<Boolean>()
    val chat = Chat()

    init {
        currentProgress.value = CurrentProgress.Title
        checkCanGoNextProgress.value = false
    }

    fun goNextProgress() {
        currentProgress.value = CurrentProgress.values()[currentProgress.value?.ordinal?.plus(1) as Int]
    }

    fun goPreviousProgress() {
        currentProgress.value = CurrentProgress.values()[currentProgress.value?.ordinal?.minus(1) as Int]
    }

    fun confirm() {
        if(!MeLiveData.value?.id.isNullOrBlank()) {
            loading.value = true
            chat.dtCreated = System.currentTimeMillis()
            chat.maxMemberCount = 2

            val key = FirebaseDatabase.getInstance().reference.child(Chat.DB_REF).push().key
            FirebaseDatabase.getInstance().reference
                    .child(Chat.DB_REF)
                    .child(MeLiveData.value?.id)
                    .child(key)
                    .setValue(chat) { _, _ ->
                        loading.value = false
                        confirmed.value = true }
        }
    }

    fun  setTitle(s: Editable) {
        chat.title = s.toString()
        checkCanGoNextProgress()
    }

    fun  setContents(s: Editable) {
        chat.description = s.toString()
        checkCanGoNextProgress()
    }

    fun checkCanGoNextProgress() {
        when(currentProgress.value) {
            Title -> if(!chat.title.isNullOrBlank()) checkCanGoNextProgress.value = true
            Contents -> if(!chat.description.isNullOrBlank()) checkCanGoNextProgress.value = true
            Options -> checkCanGoNextProgress.value = true
            else -> checkCanGoNextProgress.value = false
        }
    }
}
