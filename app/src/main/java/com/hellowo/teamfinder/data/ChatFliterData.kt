package com.hellowo.teamfinder.data

import com.pixplicity.easyprefs.library.Prefs
import java.util.*

object ChatFliterData {
    val category: Int = 0
    val hashTagSet: MutableSet<String> = HashSet()

    init {
        Prefs.getStringSet("chat_filter_data", null)?.let {  hashTagSet.addAll(it) }
    }
}
