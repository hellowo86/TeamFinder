package com.hellowo.teamfinder.data

import com.google.firebase.database.*
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.utils.*

object SearchableTagData {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child(KEY_HASH_TAG)

    init {}

    fun searchHashTag(text: String, param: ValueEventListener) {
        ref.orderByKey().startAt(text).endAt(text+"\uf8ff").addListenerForSingleValueEvent(param)
    }
}
