package com.hellowo.teamfinder.model

import com.google.firebase.database.Exclude
import com.hellowo.teamfinder.utils.FirebaseUtils

data class User (
        var id: String ?= null,
        var nickName: String? = null,
        var email: String? = null,
        var photoUrl: String? = null,
        var gender: Int = 0,
        var dtCreated: Long = 0,
        var joinedChats: MutableList<String> = ArrayList()){

    @Exclude
    fun makeMember(role: String): Member {
        return Member(id, nickName, FirebaseUtils.makePublicPhotoUrl(id), role)
    }
}
