package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.util.Log
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.utils.FirebaseUtils
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.Contents
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.Title

class ChatCreateViewModel : ViewModel() {
    enum class CurrentProgress {Game, Title, Contents, Options, Finish}
    val currentProgress = MutableLiveData<CurrentProgress>()
    val checkCanGoNextProgress = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val confirmed = MutableLiveData<Boolean>()
    val gameId = MutableLiveData<Int>()
    val chat = Chat()

    init {
        currentProgress.value = CurrentProgress.Game
        gameId.value = 0
        checkCanGoNextProgress()
    }

    fun goNextProgress() {
        currentProgress.value = CurrentProgress.values()[currentProgress.value?.ordinal?.plus(1) as Int]
        checkCanGoNextProgress()
    }

    fun goPreviousProgress() {
        currentProgress.value = CurrentProgress.values()[currentProgress.value?.ordinal?.minus(1) as Int]
        checkCanGoNextProgress()
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
            Title -> checkCanGoNextProgress.value = !chat.title.isNullOrBlank()
            Contents ->  checkCanGoNextProgress.value = !chat.description.isNullOrBlank()
            else -> checkCanGoNextProgress.value = true
        }
    }

    fun confirm(allHashTags: MutableList<String>) {
        MeLiveData.value?.let {
            val ref = FirebaseDatabase.getInstance().reference
            loading.value = true
            chat.dtCreated = System.currentTimeMillis()
            chat.maxMemberCount = 2
            chat.king = it.id
            chat.members.add(it.id!!)
            chat.gameId = gameId.value as Int

            val childUpdates = HashMap<String, Any>()
            val tagMap = HashMap<String, Boolean>()
            val key = ref.child(FirebaseUtils.KEY_CHAT).push().key

            allHashTags.forEach {
                tagMap.put(it, true)
                ref.child(FirebaseUtils.KEY_HASH_TAG).child(it).runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        if(mutableData.value == null) {
                            mutableData.value = 1
                        }else {
                            mutableData.value = mutableData.value.toString().toInt() + 1
                        }
                        return Transaction.success(mutableData)
                    }
                    override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot) {}
                })
            }

            childUpdates.put("/${FirebaseUtils.KEY_CHAT}/${key}", chat)
            //childUpdates.put("/${FirebaseUtils.KEY_CHAT}/${key}/${FirebaseUtils.KEY_HASH_TAG}", tagMap)
            childUpdates.put("/${FirebaseUtils.KEY_USERS}/${it.id}/${FirebaseUtils.KEY_CHAT}/${key}", true)

            ref.updateChildren(childUpdates){ _, _ ->
                loading.value = false
                confirmed.value = true
            }
        }
    }

    fun selectGame(game: com.hellowo.teamfinder.model.Game?) {
        gameId.value = game?.id
    }
}
