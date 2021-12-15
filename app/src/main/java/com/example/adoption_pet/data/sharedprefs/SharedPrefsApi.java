package com.example.adoption_pet.data.sharedprefs;

public interface SharedPrefsApi {
    <T> void put(String key, T data);

    <T> T get(String key, Class<T> sClass);

    <T> T get(String key, Class<T> sClass, T defaultValue);

    void clear();
}
