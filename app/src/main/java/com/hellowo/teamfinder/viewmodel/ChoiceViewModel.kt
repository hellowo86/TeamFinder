package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.database.*
import com.hellowo.teamfinder.data.ChatFliterData
import com.hellowo.teamfinder.data.Me
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
    var viewMode = MutableLiveData<Int>()
    var limit = 0

    init {
        newList.value = ArrayList()
        interestMeList.value = ArrayList()
        loading.value = false
    }

    fun loadInterestMeList() {
        loading.value = true
        interestMeList.value?.clear()
        ref.child(KEY_USERS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            postSnapshot.getValue(User::class.java)?.let {
                                interestMeList.value?.add(it)
                            }
                        }
                        if(viewMode.value == null) {
                            viewMode.value = 0
                        }
                        interestMeList.value = interestMeList.value
                        loading.value = false
                    }
                })
    }

    fun startNewSearch(dtConnected: Double) {
        viewMode.value = 1
        ref.child(KEY_USERS)
                .orderByChild("dtConnected")
                .startAt(dtConnected)
                .limitToLast(100)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.children.count() > 0) {
                            for (postSnapshot in snapshot.children) {
                                postSnapshot.getValue(User::class.java)?.let {
                                    newList.value?.add(it)
                                    log(it.toString())
                                }
                            }
                            viewMode.value = 2
                        }else {
                            viewMode.value = 2
                        }
                    }
                })
    }


}