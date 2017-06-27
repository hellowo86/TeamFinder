package com.hellowo.teamfinder.data;

import android.support.annotation.MainThread;

import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.Option;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OptionData {
    private static OptionData sInstance;
    ArrayList<Option> options;
    int[] iconIds = new int[]{
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp,
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp,
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp
    };

    @MainThread
    public static OptionData get() {
        if (sInstance == null) {
            sInstance = new OptionData();
        }
        return sInstance;
    }

    private OptionData() {
        options = new ArrayList<>();
        String[] optionArray = App.context.getResources().getStringArray(R.array.options);
        for (int i = 0; i < optionArray.length; i++) {
            Option option = new Option();
            option.setId(i);
            option.setName(optionArray[i]);
            option.setIconId(iconIds[i]);
            options.add(option);
        }
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public Option getOption(int id) {
        return options.get(id);
    }

}
