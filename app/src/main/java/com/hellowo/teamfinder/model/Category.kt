package com.hellowo.teamfinder.model

data class Category(
        val id: Int,
        val title: String,
        val roles: List<String>,
        val maxMemberCount: Int,
        val iconId: Int,
        val backgroundId: Int)
