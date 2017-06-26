package com.hellowo.teamfinder.data;

import android.support.annotation.MainThread;
import android.util.Log;

import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.Game;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class GameData {
    private static GameData sInstance;
    Map<String, Game> gameMap;
    int[] gameIconIds = new int[]{
            R.drawable.game_icon_0,
            R.drawable.game_icon_1,
            R.drawable.game_icon_2
    };

    @MainThread
    public static GameData get() {
        if (sInstance == null) {
            sInstance = new GameData();
        }
        return sInstance;
    }

    private GameData() {
        gameMap = new HashMap<>();
        String[] games = App.context.getResources().getStringArray(R.array.games);
        for (int i = 0; i < games.length; i++) {
            try {
                JSONArray gameJsonArray = new JSONArray(games[i]);

                Game game = new Game();
                game.setId(gameJsonArray.getString(0));
                game.setTitle(gameJsonArray.getString(1));

                JSONArray roleArray = gameJsonArray.getJSONArray(2);

                for (int j = 0; j < roleArray.length(); j++) {
                    game.getRoles().add(roleArray.getString(j));
                }

                game.setMaxMemberCount(gameJsonArray.getInt(3));
                game.setIconId(gameIconIds[i]);
                gameMap.put(game.getId(), game);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Game> getGameMap() {
        return gameMap;
    }

    public Game getGame(String id) {
        return gameMap.get(id);
    }

}
