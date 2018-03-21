package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.ChatFliterData
import com.hellowo.teamfinder.model.ChatMember
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.utils.KEY_DT_ENTERED
import com.hellowo.teamfinder.utils.KEY_USERS
import com.hellowo.teamfinder.utils.log

class ChoiceViewModel : ViewModel() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    var newList = MutableLiveData<ArrayList<User>>()
    var interestMeList = MutableLiveData<ArrayList<User>>()
    var loading = MutableLiveData<Boolean>()

    init {
        newList.value = ArrayList()
        interestMeList.value = ArrayList()
        loading.value = false
    }

    fun loadInterestMeList() {
        loading.value = true
        ref.child(KEY_USERS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            postSnapshot.getValue(User::class.java)?.let {
                                log(it.toString())
                                interestMeList.value?.add(it)
                            }
                        }
                        interestMeList.value = interestMeList.value
                        loading.value = false
                    }
                })
    }


}