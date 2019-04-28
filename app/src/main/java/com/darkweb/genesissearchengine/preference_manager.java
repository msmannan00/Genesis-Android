package com.darkweb.genesissearchengine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class preference_manager {
    private static final preference_manager ourInstance = new preference_manager();

    public static preference_manager getInstance() {
        return ourInstance;
    }

    private preference_manager() {
    }

    public void saveString(String valueKey, String value, Context applicationContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(valueKey, value);
        edit.commit();
    }

    public String getString(String valueKey, String valueDefault,Context applicationContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        return prefs.getString(valueKey, valueDefault);
    }

}
