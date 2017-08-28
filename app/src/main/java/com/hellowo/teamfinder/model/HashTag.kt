package com.hellowo.teamfinder.model

data class HashTag (
        var name: String? = null,
        var count: Int = 0){

    fun makeTag(): String {
        return "#$name "
    }
}
