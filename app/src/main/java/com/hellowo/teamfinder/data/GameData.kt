package com.hellowo.teamfinder.data

import android.util.Log
import com.beust.klaxon.JsonArray
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Game
import org.json.JSONArray

object GameData {
    val games: MutableList<Game> = mutableListOf()
    val gameIconIds = intArrayOf(
            R.drawable.game_icon_0, R.drawable.game_icon_1, R.drawable.game_icon_2)

    init {
        val gamesJson = App.context.resources.getStringArray(R.array.games).toList()
        var id = 0
        gamesJson.map { JSONArray(it) }
                .forEach {
                    games.add(Game(
                            id,
                            it.getString(0),
                            JsonArray(it.getString(1)).map { it as String },
                            it.getInt(2),
                            gameIconIds[id++]))
                }
    }
}
