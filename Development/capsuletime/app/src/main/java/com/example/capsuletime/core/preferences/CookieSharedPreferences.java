package com.example.capsuletime.core.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

public class CookieSharedPreferences {

    public static final String COOKIE_SHARED_PREFERENCES_KEY = "new.capsuletime.www.cookie";
    private static CookieSharedPreferences cookieSharedPreferences = null;

    public static CookieSharedPreferences getInstanceOf(Context c){
        if(cookieSharedPreferences == null){
            cookieSharedPreferences = new CookieSharedPreferences(c);
        }

        return cookieSharedPreferences;
    }

    private SharedPreferences sharedPreferences;

    public CookieSharedPreferences(Context context) {
        final String COOKIE_SHARED_PREFERENCE_NAME = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(COOKIE_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
    }

    public void putHashSet(String key, HashSet<String> hashSet){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, hashSet);
        editor.commit();
    }

    public void deleteHashSet(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }

    public HashSet<String> getHashSet(String key, HashSet<String> cookie){
        try {
            return (HashSet<String>) sharedPreferences.getStringSet(key, cookie);
        } catch (Exception e) {
            return cookie;
        }
    }
}
