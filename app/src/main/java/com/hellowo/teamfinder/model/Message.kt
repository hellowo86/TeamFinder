package com.hellowo.teamfinder.model

data class Message (
        var text: String? = null,
        var userName: String? = null,
        var userId: String? = null,
        var photoUrl: String? = null,
        var dtCreated: Long = 0,
        var type: Int = 0)
