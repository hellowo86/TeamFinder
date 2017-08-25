package com.hellowo.teamfinder.data

import com.beust.klaxon.JsonArray
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Category
import org.json.JSONArray

object CategoryData {
    val CATEGORIES: MutableList<Category> = mutableListOf()
    val gameIconIds = intArrayOf(
            R.drawable.ic_account_circle_black_48dp, R.drawable.ic_assignment_late_black_48dp, R.drawable.ic_dashboard_black_24dp)
    val gameBackgroundIds = intArrayOf(
            R.drawable.game_background_0, R.drawable.game_background_0, R.drawable.game_background_0)

    init {
        val gamesJson = App.context.resources.getStringArray(R.array.games).toList()
        var id = 0
        gamesJson.map { JSONArray(it) }
                .forEach {
                    CATEGORIES.add(Category(
                            id,
                            it.getString(0),
                            JsonArray(it.getString(1)).map { it as String },
                            it.getInt(2),
                            gameIconIds[id],
                            gameBackgroundIds[id++]))
                }
    }
}
