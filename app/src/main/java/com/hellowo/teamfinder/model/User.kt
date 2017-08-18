package com.hellowo.teamfinder.model

import com.google.firebase.database.Exclude
import com.hellowo.teamfinder.utils.makePublicPhotoUrl

data class User (
        var id: String ?= null,
        var nickName: String? = null,
        var email: String? = null,
        var photoUrl: String? = null,
        var gender: Int = 0,
        var dtCreated: Long = 0){

    @Exclude
    fun makeMember(role: String): Member {
        return Member(id, nickName, makePublicPhotoUrl(id), role)
    }

    @Exclude
    fun makeChatMember(): ChatMember {
        return ChatMember(id, nickName, makePublicPhotoUrl(id), null, 0)
    }
}
