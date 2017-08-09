package com.hellowo.teamfinder.model

import com.google.firebase.database.Exclude
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R

import java.text.DateFormat
import java.util.Date

class Comment (
        var text: String? = null,
        var userName: String? = null,
        var userId: String? = null,
        var dtCreated: Long = 0)
