package com.hellowo.teamfinder.model

data class ChatMember (
        var userId: String? = null,
        var name: String? = null,
        var photoUrl: String? = null,
        var role: String? = null,
        var lastConnectedTime: Long = 0,
        var live: Boolean = false,
        var pushToken: String? = null)
