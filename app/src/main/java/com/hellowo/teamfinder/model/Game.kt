package com.hellowo.teamfinder.model

import org.json.JSONArray

data class Game (
        val id: Int,
        val title: String,
        val roles: List<String>,
        val maxMemberCount: Int,
        val iconId: Int)
