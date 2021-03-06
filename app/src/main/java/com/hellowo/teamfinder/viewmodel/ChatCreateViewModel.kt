package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.text.Editable
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.hellowo.teamfinder.data.CategoryData
import com.hellowo.teamfinder.data.Me
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.*
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
        Me.value?.let {
            val ref = FirebaseDatabase.getInstance().reference
            loading.value = true
            chat.dtCreated = System.currentTimeMillis()
            chat.dtUpdated =  chat.dtCreated
            chat.maxMemberCount = 2
            chat.king = it.id

            val childUpdates = HashMap<String, Any>()
            val tagMap = HashMap<String, Boolean>()
            val key = ref.child(KEY_CHAT).push().key
            val chatMember = it.makeChatMember()
            chatMember.pushToken = FirebaseInstanceId.getInstance().token

            tagMap.put(CategoryData.CATEGORIES[chat.gameId].title, true)
            allHashTags.forEach {
                tagMap.put(it, true)
                ref.child(KEY_HASH_TAG).child(it).runTransaction(object : Transaction.Handler {
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

            childUpdates.put("/$KEY_CHAT/$key", chat.makeMap(tagMap))
            childUpdates.put("/$KEY_CHAT_MEMBERS/$key/${it.id}", chatMember)
            childUpdates.put("/$KEY_USERS/${it.id}/$KEY_CHAT/$key/$KEY_DT_ENTERED", chat.dtCreated)
            childUpdates.put("/$KEY_USERS/${it.id}/$KEY_CHAT/$key/$KEY_LAST_CHECK_INDEX", 0)

            ref.updateChildren(childUpdates){ _, _ ->
                loading.value = false
                confirmed.value = true
            }
        }
    }

    fun selectGame(category: com.hellowo.teamfinder.model.Category?) {
        gameId.value = category?.id
    }
}
