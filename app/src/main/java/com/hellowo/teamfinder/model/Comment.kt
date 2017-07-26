package com.hellowo.teamfinder.model

import com.google.firebase.database.Exclude
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R

import java.text.DateFormat
import java.util.Date

class Comment (var text: String,
               var userName: String,
               var userId: String,
               var dtCreated: Long = 0){

    companion object {
        val DB_REF = "comments"
    }

    @Exclude
    fun makeActiveTimeText(): String {
        val m = (System.currentTimeMillis() - dtCreated) / (1000 * 60)
        when(m){
            in 0..9 -> return App.context.getString(R.string.just_before)
            in 10..59 -> return String.format(App.context.getString(R.string.min_before), m)
            in 60..(60 * 24) -> return String.format(App.context.getString(R.string.hour_before), m / 60)
            else -> return DateFormat.getDateTimeInstance().format(Date(dtCreated))
        }
    }
}
