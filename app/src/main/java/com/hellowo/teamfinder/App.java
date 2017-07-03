package com.hellowo.teamfinder;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContextWrapper;
import android.provider.Settings;

import com.pixplicity.easyprefs.library.Prefs;

public class App extends Application {
    public static App context;
    public static String androidId;

    @SuppressLint("HardwareIds")
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        initPrefs();
    }

    private void initPrefs() {
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
