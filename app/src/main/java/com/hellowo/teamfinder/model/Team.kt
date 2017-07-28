package com.hellowo.teamfinder.model

import android.text.TextUtils

import com.google.firebase.database.Exclude
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R

import java.util.ArrayList
import java.util.HashMap

data class Team (
        val members:List<Member> = ArrayList(),
        val roles:Map<String, Int> = HashMap(),
        var id:String? = null,
        var dtCreated:Long = 0,
        var title:String? = null,
        var gameId:Int = 0,
        var dtActive:Long = 0,
        var status:Int = 0,
        var commentCount:Int = 0){

    @Exclude
    fun getOrganizer() : Member = members[0]

    @Exclude
    fun makeMemberText():String {
        var attendedMemberCount = 0
        for (member in members)
        {
            if (!TextUtils.isEmpty(member.userId))
            {
                attendedMemberCount++
            }
        }
        return String.format(App.context.getString(R.string.member_count),
                attendedMemberCount, members.size)
    }

    @Exclude
    fun makeActiveTimeText():String {
        if (dtActive == java.lang.Long.MAX_VALUE)
        {
            return App.context.getString(R.string.active_infinity_time)
        }
        else
        {
            val h = ((dtActive - System.currentTimeMillis()) / (1000 * 60 * 60)).toInt()
            if (h < 1)
            {
                return App.context.getString(R.string.active_time_finish_soon)
            }
            else
            {
                return "${h}h"
            }
        }
    }
}