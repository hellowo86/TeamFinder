package com.hellowo.teamfinder.model

data class Chat (
        var id: String? = null,
        var title: String? = null,
        var description: String? = null,
        var maxMemberCount: Int? = 2,
        var dtCreated: Long = System.currentTimeMillis(),
        var king: String? = null,
        var members: MutableList<String> = ArrayList())
