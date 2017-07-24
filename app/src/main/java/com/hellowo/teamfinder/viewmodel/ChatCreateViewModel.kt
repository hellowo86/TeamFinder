package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.*

class ChatCreateViewModel : ViewModel() {
    enum class CurrentProgress {Title, Contents, Options}
    val currentProgress = MutableLiveData<CurrentProgress>()
    val checkCanGoNextProgress = MutableLiveData<Boolean>()
    val chat = Chat()

    init {
        currentProgress.value = CurrentProgress.Title
        checkCanGoNextProgress.value = false
    }

    fun goNextProgress() {
        if(checkCanGoNextProgress.value == true) {
            when(currentProgress.value) {
                Options -> confirm()
                else -> currentProgress.value = CurrentProgress.values()[currentProgress.value?.ordinal?.plus(1) as Int]
            }
        }
    }

    fun goPreviousProgress() {
        currentProgress.value = CurrentProgress.values()[currentProgress.value?.ordinal?.minus(1) as Int]
    }

    private fun confirm() {

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
