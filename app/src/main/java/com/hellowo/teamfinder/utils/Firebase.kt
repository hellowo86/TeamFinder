package com.hellowo.teamfinder.utils

val KEY_USERS = "users"
val KEY_PHOTO_URL = "photoUrl"
val KEY_TEAMS = "teams"
val KEY_FILTERING = "filteringKey"
val KEY_COMMENTS = "comments"
val KEY_CHAT = "chat"
val KEY_MESSAGE = "message"
val KEY_DT_CREATED = "dtCreated"
val KEY_TYPING = "typing"
val KEY_LAST_MESSAGE = "lastMessage"
val KEY_LAST_MESSAGE_TIME = "lastMessageTime"
val KEY_HASH_TAG = "hashTag"

fun makePublicPhotoUrl(userId: String?): String = "https://firebasestorage.googleapis.com/v0/b/teamfinder-32133.appspot.com/o/userPhoto%2F${userId}.jpg?alt=media"
/*
쿼리하는법
databaseReference.orderByChild('_searchLastName')
             .startAt(queryText)
             .endAt(queryText+"\uf8ff")
             .once("value")
 */