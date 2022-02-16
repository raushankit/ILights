package com.raushankit.ILghts.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.raushankit.ILghts.entity.SharedRefKeys;

import java.util.Map;

public class SharedRepo {
    private static SharedRepo INSTANCE;
    private final SharedPreferences prefs;

    private SharedRepo(Context context) {
        prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
    }

    public static SharedRepo newInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SharedRepo(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public void insert(SharedRefKeys key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key.name(), value);
        editor.apply();
    }

    public void insertLong(SharedRefKeys key, long value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key.name(), value);
        editor.apply();
    }

    public void insertBoolean(SharedRefKeys key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key.name(), value);
        editor.apply();
    }

    public void insert(Map<SharedRefKeys, String> mp) {
        SharedPreferences.Editor editor = prefs.edit();
        mp.forEach((k, v) -> editor.putString(k.name(), v));
        editor.apply();
    }

    public String getValue(SharedRefKeys key) {
        return prefs.getString(key.name(), SharedRefKeys.DEFAULT_VALUE.name());
    }

    public long getLongValue(SharedRefKeys keys, long defaultValue) {
        return prefs.getLong(keys.name(), defaultValue);
    }

    public boolean getBooleanValue(SharedRefKeys keys, boolean defaultValue) {
        return prefs.getBoolean(keys.name(), defaultValue);
    }
}
