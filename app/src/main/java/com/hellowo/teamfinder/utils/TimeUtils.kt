package com.hellowo.teamfinder.utils

import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import java.text.DateFormat
import java.util.*

fun makeActiveTimeText(dtCreated: Long): String {
    val m = (System.currentTimeMillis() - dtCreated) / (1000 * 60)
    when(m){
        in 0..9 -> return App.context.getString(R.string.just_before)
        in 10..59 -> return String.format(App.context.getString(R.string.min_before), m)
        in 60..(60 * 24) -> return String.format(App.context.getString(R.string.hour_before), m / 60)
        else -> return DateFormat.getDateTimeInstance().format(Date(dtCreated))
    }
}

fun makeMessageLastTimeText(dtCreated: Long): String {
    val m = (System.currentTimeMillis() - dtCreated) / (1000 * 60)
    when(m){
        in 0..0 -> return App.context.getString(R.string.just_before)
        in 1..59 -> return String.format(App.context.getString(R.string.min_before), m)
        in 60..(60 * 24) -> return String.format(App.context.getString(R.string.hour_before), m / 60)
        else -> return DateFormat.getDateTimeInstance().format(Date(dtCreated))
    }
}