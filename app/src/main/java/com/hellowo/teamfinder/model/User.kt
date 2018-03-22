package com.hellowo.teamfinder.model

import com.google.firebase.database.Exclude
import com.hellowo.teamfinder.utils.makePublicPhotoUrl
import java.io.Serializable

data class User (
        var id: String ?= null,
        var nickName: String? = null,
        var email: String? = null,
        var photoUrl: String? = null,
        var gender: Int = 0,
        var birth: Int = 0,
        var lat: Double = 0.0,
        var lng: Double = 0.0,
        var location: String? = null,
        var moreInfo: String? = null,
        var dtConnected: Long = 0,
        var dtCreated: Long = 0): Serializable{

    @Exclude
    fun makeMember(role: String): Member {
        return Member(id, nickName, makePublicPhotoUrl(id), role)
    }

    @Exclude
    fun makeChatMember(): ChatMember {
        return ChatMember(id, nickName, makePublicPhotoUrl(id), null, 0)
    }
}
