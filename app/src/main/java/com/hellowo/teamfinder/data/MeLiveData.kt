package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hellowo.teamfinder.model.Member
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.utils.FirebaseUtils

object MeLiveData : LiveData<User>() {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mAuthListener: FirebaseAuth.AuthStateListener
    var valueEventListener: ValueEventListener
    val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    init {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                value = dataSnapshot.getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        mAuthListener = FirebaseAuth.AuthStateListener { auth ->
            mDatabase.removeEventListener(valueEventListener)
            if(auth.currentUser != null) {
                loadCurrentLoginUser(auth.currentUser as FirebaseUser)
            }else {
                value = null
            }
        }
    }

    private fun loadCurrentLoginUser(user: FirebaseUser) {
        mDatabase.child(FirebaseUtils.KEY_USERS).child(user.uid).addValueEventListener(valueEventListener)
    }

    override fun onActive() {
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onInactive() {
        mDatabase.removeEventListener(valueEventListener)
        mAuth.removeAuthStateListener(mAuthListener)
    }

    fun isMe(member: Member) = value?.id.equals(member.userId)
}