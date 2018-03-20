package com.hellowo.teamfinder.data

import com.beust.klaxon.JsonArray
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Category
import org.json.JSONArray

object CategoryData {
    val CATEGORIES: MutableList<Category> = mutableListOf()
    val icons = intArrayOf(
            R.drawable.ic_account_circle_black_48dp, R.drawable.ic_assignment_late_black_48dp, R.drawable.ic_dashboard_black_24dp)
    val backgrounds = intArrayOf(
            R.drawable.search_love, R.drawable.search_love, R.drawable.search_love)

    init {
        val gamesJson = App.context.resources.getStringArray(R.array.categories).toList()
        var id = 0
        gamesJson.forEach { CATEGORIES.add(Category(id, it, icons[id], backgrounds[id++])) }
    }
}
