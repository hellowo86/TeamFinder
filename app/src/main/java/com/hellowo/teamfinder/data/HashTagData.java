package com.hellowo.teamfinder.data;

import android.support.annotation.MainThread;

import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.HashTag;

import java.util.ArrayList;

public class HashTagData {
    private static HashTagData sInstance;
    ArrayList<HashTag> hashTags;
    int[] iconIds = new int[]{
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp,
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp,
            R.drawable.ic_face_black_48dp,
            R.drawable.ic_format_quote_black_48dp
    };

    @MainThread
    public static HashTagData get() {
        if (sInstance == null) {
            sInstance = new HashTagData();
        }
        return sInstance;
    }

    private HashTagData() {
        hashTags = new ArrayList<>();
        String[] optionArray = App.context.getResources().getStringArray(R.array.options);
        for (int i = 0; i < optionArray.length; i++) {
            HashTag hashTag = new HashTag(i, optionArray[i], iconIds[i]);
            hashTags.add(hashTag);
        }
    }

    public ArrayList<HashTag> getHashTags() {
        return hashTags;
    }

    public HashTag getOption(int id) {
        return hashTags.get(id);
    }

}
