package com.hellowo.teamfinder.model

import com.google.firebase.database.Exclude

data class Chat (
        var id: String? = null,
        var title: String? = null,
        var description: String? = null,
        var maxMemberCount: Int? = 2,
        var dtCreated: Long = System.currentTimeMillis(),
        var king: String? = null,
        var lastMessage: String? = null,
        var lastMessageTime: Long = 0,
        var gameId: Int = 0,
        var messageCount: Int = 0,
        var lat: Double = 0.0,
        var lng: Double = 0.0,
        var location: String? = null) {

    @Exclude var dtEntered: Long = 0
    @Exclude var lastCheckIndex: Int = 0

    fun makeMap(hashTag: HashMap<String, Boolean>): HashMap<String, Any?> {
        val resultMap = HashMap<String, Any?>()
        resultMap.put("id", id)
        resultMap.put("title", title)
        resultMap.put("description", description)
        resultMap.put("maxMemberCount", maxMemberCount)
        resultMap.put("dtCreated", dtCreated)
        resultMap.put("king", king)
        resultMap.put("lastMessage", lastMessage)
        resultMap.put("lastMessageTime", lastMessageTime)
        resultMap.put("gameId", gameId)
        resultMap.put("messageCount", messageCount)
        resultMap.put("hashTag", hashTag)
        resultMap.put("lat", lat)
        resultMap.put("lng", lng)
        resultMap.put("latlng", "${lat}_$lng")
        resultMap.put("location", location)
        return resultMap
    }
}
