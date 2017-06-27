package com.hellowo.teamfinder.data;

import android.support.annotation.MainThread;

import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.Game;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameData {
    private static GameData sInstance;
    ArrayList<Game> games;
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
        games = new ArrayList<>();
        String[] gamearray = App.context.getResources().getStringArray(R.array.games);
        for (int i = 0; i < gamearray.length; i++) {
            try {
                JSONArray gameJsonArray = new JSONArray(gamearray[i]);

                Game game = new Game();
                game.setId(i);
                game.setTitle(gameJsonArray.getString(0));

                JSONArray roleArray = gameJsonArray.getJSONArray(1);

                for (int j = 0; j < roleArray.length(); j++) {
                    game.getRoles().add(roleArray.getString(j));
                }

                game.setMaxMemberCount(gameJsonArray.getInt(2));
                game.setIconId(gameIconIds[i]);
                games.add(game);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public Game getGame(int id) {
        return games.get(id);
    }

}
