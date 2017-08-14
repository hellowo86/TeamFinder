package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import com.google.firebase.database.FirebaseDatabase
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.FirebaseUtils
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

    fun confirm() {
        MeLiveData.value?.let {
            loading.value = true
            chat.dtCreated = System.currentTimeMillis()
            chat.maxMemberCount = 2
            chat.king = it.id
            chat.members.add(it.id!!)

            val childUpdates = HashMap<String, Any>()
            val key = FirebaseDatabase.getInstance().reference.child(FirebaseUtils.KEY_CHAT).push().key

            childUpdates.put("/${FirebaseUtils.KEY_CHAT}/${key}", chat)
            childUpdates.put("/${FirebaseUtils.KEY_USERS}/${it.id}/${FirebaseUtils.KEY_CHAT}/${key}", true)

            FirebaseDatabase.getInstance().reference
                    .updateChildren(childUpdates){ _, _ ->
                        loading.value = false
                        confirmed.value = true }
        }
    }
}
