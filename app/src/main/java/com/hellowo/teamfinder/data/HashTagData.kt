package com.hellowo.teamfinder.data

import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.HashTag
import java.util.*

object HashTagData {
    val hashTags: ArrayList<HashTag> = ArrayList()
    internal var iconIds = intArrayOf(
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp,
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp,
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp)

    init {
        val optionArray = App.context.resources.getStringArray(R.array.options)
        for (i in optionArray.indices) {
            val hashTag = HashTag(i, optionArray[i], iconIds[i])
            hashTags.add(hashTag)
        }
    }
}
