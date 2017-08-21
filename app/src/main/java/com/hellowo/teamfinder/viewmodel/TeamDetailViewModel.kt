package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.Comment
import com.hellowo.teamfinder.model.Member
import com.hellowo.teamfinder.model.Team

import java.util.ArrayList
import java.util.HashMap

import com.hellowo.teamfinder.AppConst.DB_KEY_DT_CREATED
import com.hellowo.teamfinder.utils.KEY_COMMENTS
import com.hellowo.teamfinder.utils.KEY_TEAMS

class TeamDetailViewModel : ViewModel() {
    var team = MutableLiveData<Team>()
    var members = MutableLiveData<List<Member>>()
    var roles = MutableLiveData<Map<String, Int>>()
    var comments = MutableLiveData<List<Comment>>()
    var currentPage = MutableLiveData<Int>()
    var loading = MutableLiveData<Boolean>()
    private var teamId: String? = null
    private val memberList = ArrayList<Member>()
    private val commentList = ArrayList<Comment>()
    private val roleMap = HashMap<String, Int>()

    init {
        currentPage.value = 0
    }

    fun initTeam(teamId: String) {
        this.teamId = teamId
        members.value = memberList
        comments.value = commentList
        roles.value = roleMap
        loadTeam()
        loadComments()
    }

    fun loadTeam() {
        loading.setValue(true)
        FirebaseDatabase.getInstance().reference.child(KEY_TEAMS)
                .child(teamId!!)
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                team.setValue(dataSnapshot.getValue(Team::class.java))
                                memberList.clear()
                                memberList.addAll(team.value!!.members)
                                members.setValue(memberList)
                                roleMap.clear()
                                roleMap.putAll(team.value!!.roles)
                                roles.setValue(roleMap)
                                loading.setValue(false)
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                loading.setValue(false)
                            }
                        })
    }

    fun loadComments() {
        FirebaseDatabase.getInstance().reference.child(KEY_COMMENTS)
                .child(teamId!!)
                .orderByChild(DB_KEY_DT_CREATED)
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                commentList.clear()
                                for (postSnapshot in dataSnapshot.children) {
                                    postSnapshot.getValue(Comment::class.java)?.let {
                                        commentList.add(0, it)
                                    }
                                }
                                comments.setValue(commentList)
                                Log.d("aaa", commentList.size.toString() + "")
                                loading.setValue(false)
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
    }

    fun postComment(message: String) {
        loading.setValue(true)
        val comment = Comment(
                message,
                MeLiveData.value!!.nickName,
                MeLiveData.value!!.id,
                System.currentTimeMillis())

        val ref = FirebaseDatabase.getInstance().reference.child(KEY_COMMENTS).child(teamId!!)
        val key = ref.push().key
        ref.child(key)
                .setValue(comment) { error, databaseReference ->
                    loadComments()

                    FirebaseDatabase.getInstance().reference.child(KEY_TEAMS).child(teamId!!)
                            .runTransaction(object : Transaction.Handler {
                                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                                    val t = mutableData.getValue(Team::class.java) ?: return Transaction.success(mutableData)

                                    t.commentCount = t.commentCount + 1

                                    // Set value and report transaction success
                                    mutableData.setValue(t)
                                    return Transaction.success(mutableData)
                                }

                                override fun onComplete(databaseError: DatabaseError, b: Boolean,
                                                        dataSnapshot: DataSnapshot) {
                                }
                            })

                }
    }
}
