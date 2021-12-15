package com.example.adoption_pet.data.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPrefs implements SharedPrefsApi {
    private static final String PREFS_NAME = "";
    private final SharedPreferences mSharedPreferences;
    private final Gson mGson;

    public SharedPrefs(Context context, Gson gson) {
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mGson = gson;
    }

    @Override
    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            editor.putString(key, mGson.toJson(data));
        }
        editor.apply();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> sClass) {
        if (sClass == String.class) {
            return (T) mSharedPreferences.getString(key, "");
        } else if (sClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false));
        } else if (sClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, 0));
        } else if (sClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, 0));
        } else if (sClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, 0));
        } else {
            return mGson.fromJson(mSharedPreferences.getString(key, ""), sClass);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> sClass, T defaultValue) {
        if (sClass == String.class) {
            return (T) mSharedPreferences.getString(key, (String) defaultValue);
        } else if (sClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, (Boolean) defaultValue));
        } else if (sClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, (Float) defaultValue));
        } else if (sClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, (Integer) defaultValue));
        } else if (sClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, (Long) defaultValue));
        } else {
            return mGson.fromJson(mSharedPreferences.getString(key, ""), sClass);
        }
    }

    @Override
    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }
}
