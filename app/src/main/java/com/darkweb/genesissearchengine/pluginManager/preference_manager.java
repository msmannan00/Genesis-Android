package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class preference_manager
{
    /*Private Declarations*/
    private static final preference_manager ourInstance = new preference_manager();
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public static preference_manager getInstance()
    {
        return ourInstance;
    }

    /*Initializations*/
    private preference_manager()
    {
    }

    public void initialize(Context applicationContext)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        edit = prefs.edit();
    }

    /*Saving Preferences*/
    public void saveString(String valueKey, String value)
    {
        edit.putString(valueKey, value);
        edit.commit();
    }

    public void saveBool(String valueKey, boolean value)
    {
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    /*Recieving Preferences*/
    public String getString(String valueKey, String valueDefault)
    {
        return prefs.getString(valueKey, valueDefault);
    }

    public boolean getBool(String valueKey, boolean valueDefault)
    {
        return prefs.getBoolean(valueKey, valueDefault);
    }
}
