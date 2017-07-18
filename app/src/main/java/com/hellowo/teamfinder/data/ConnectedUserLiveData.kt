package com.hellowo.teamfinder.data

import android.arch.lifecycle.LiveData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.model.Member
import com.hellowo.teamfinder.model.User

object ConnectedUserLiveData : LiveData<User>() {
    val mAuth: FirebaseAuth
    val mAuthListener: FirebaseAuth.AuthStateListener
    var valueEventListener: ValueEventListener
    val mDatabase: DatabaseReference

    init {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                value = dataSnapshot.getValue(User::class.java)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(auth: FirebaseAuth) {
                mDatabase.removeEventListener(valueEventListener)
                if(auth.currentUser != null) {
                    loadCurrentLoginUser(auth.currentUser as FirebaseUser)
                }else {
                    value = null
                }
            }
        }
    }

    private fun loadCurrentLoginUser(user: FirebaseUser) {
        mDatabase.child(User.DB_REF).child(user.uid).addValueEventListener(valueEventListener)
    }

    override fun onActive() {
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onInactive() {
        mDatabase.removeEventListener(valueEventListener)
        mAuth.removeAuthStateListener(mAuthListener)
    }

    fun isMe(member: Member) : Boolean {
        return value?.id.equals(member.userId)
    }
}