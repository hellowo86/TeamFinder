package com.hellowo.teamfinder.model

data class HashTag (
        var id: Int,
        var name: String,
        var iconId: Int,
        var count: Int){

    fun makeTag(): String {
        return "#$name "
    }
}
