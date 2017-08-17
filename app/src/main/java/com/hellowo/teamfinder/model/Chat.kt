package com.hellowo.teamfinder.model

data class Chat (
        var id: String? = null,
        var title: String? = null,
        var description: String? = null,
        var maxMemberCount: Int? = 2,
        var dtCreated: Long = System.currentTimeMillis(),
        var king: String? = null,
        var members: MutableList<String> = ArrayList(),
        var lastMessage: String? = null,
        var lastMessageTime: Long = 0,
        var gameId: Int = 0) {

    fun  makeMap(hashTag: HashMap<String, Boolean>): HashMap<String, Any?> {
        val resultMap = HashMap<String, Any?>()
        resultMap.put("id", id)
        resultMap.put("title", title)
        resultMap.put("description", description)
        resultMap.put("maxMemberCount", maxMemberCount)
        resultMap.put("dtCreated", dtCreated)
        resultMap.put("king", king)
        resultMap.put("members", members)
        resultMap.put("lastMessage", lastMessage)
        resultMap.put("lastMessageTime", lastMessageTime)
        resultMap.put("gameId", gameId)
        resultMap.put("hashTag", hashTag)
        return resultMap
    }
}
