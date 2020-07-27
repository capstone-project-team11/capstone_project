package com.example.capsuletime.core.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockNickNameSharedPreferences {

    public static final String NICKNAME_SHARED_PREFERENCES_KEY2 = "new.capsuletime.www.lock";
    private static LockNickNameSharedPreferences lockNickNameSharedPreferences = null;

    public static LockNickNameSharedPreferences getInstanceOf(Context c){
        if(lockNickNameSharedPreferences == null){
            lockNickNameSharedPreferences = new LockNickNameSharedPreferences(c);
        }

        return lockNickNameSharedPreferences;
    }

    private SharedPreferences sharedPreferences;

    public LockNickNameSharedPreferences(Context context) {
        final String Lock_NICKNAME_SHARED_PREFERENCE_NAME = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(Lock_NICKNAME_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
    }

    public void putHashSet(String key, ArrayList<String> hashSet){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        /*editor.putStringSet(key, hashSet);*/
        JSONArray a = new JSONArray();
        for (int i = 0; i < hashSet.size(); i++) {
            a.put(hashSet.get(i));
        }
        if (!hashSet.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public void deleteHashSet(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key).commit();
    }

    public ArrayList<String> getHashSet(String key, ArrayList<String> nickName){
        try {
            return (ArrayList<String>) sharedPreferences.getStringSet(key, (Set<String>) nickName);
        } catch (Exception e) {
            return nickName;
        }
    }

}