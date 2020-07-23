package com.example.capsuletime.core.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

public class NickNameSharedPreferences {

    public static final String NICKNAME_SHARED_PREFERENCES_KEY = "new.capsuletime.www.nick";
    private static NickNameSharedPreferences nickNameSharedPreferences = null;

    public static NickNameSharedPreferences getInstanceOf(Context c){
        if(nickNameSharedPreferences == null){
            nickNameSharedPreferences = new NickNameSharedPreferences(c);
        }

        return nickNameSharedPreferences;
    }

    private SharedPreferences sharedPreferences;

    public NickNameSharedPreferences(Context context) {
        final String NICKNAME_SHARED_PREFERENCE_NAME = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(NICKNAME_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
    }

    public void putHashSet(String key, HashSet<String> hashSet){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, hashSet);
        editor.commit();
    }

    public void deleteHashSet(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key).commit();
    }

    public HashSet<String> getHashSet(String key, HashSet<String> nickName){
        try {
            return (HashSet<String>) sharedPreferences.getStringSet(key, nickName);
        } catch (Exception e) {
            return nickName;
        }
    }

}
