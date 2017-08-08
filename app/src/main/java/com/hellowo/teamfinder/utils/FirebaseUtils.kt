package com.hellowo.teamfinder.utils

object FirebaseUtils {
    val KEY_USERS = "users"
    val KEY_PHOTO_URL = "photoUrl"
    val KEY_TEAMS = "teams"
    val KEY_FILTERING = "filteringKey"
    val KEY_COMMENTS = "comments"
    val KEY_CHAT = "chat"
    val KEY_MESSAGE = "message"
    val KEY_DT_CREATED = "dtCreated"

    fun makePublicPhotoUrl(userId: String?): String = "https://firebasestorage.googleapis.com/v0/b/teamfinder-32133.appspot.com/o/userPhoto%2F${userId}.jpg?alt=media"
}