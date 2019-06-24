package com.darkweb.genesissearchengine.dataManager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.darkweb.genesissearchengine.appManager.app_model;

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

    public void initialize()
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(app_model.getInstance().getAppContext());
        edit = prefs.edit();
    }

    /*Saving Preferences*/
    public void setString(String valueKey, String value)
    {
        edit.putString(valueKey, value);
        edit.commit();
    }

    public void setBool(String valueKey, boolean value)
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
