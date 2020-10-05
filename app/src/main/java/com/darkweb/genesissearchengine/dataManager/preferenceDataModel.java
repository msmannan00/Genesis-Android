package com.darkweb.genesissearchengine.dataManager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class preferenceDataModel {

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEdit;

    public preferenceDataModel(AppCompatActivity app_context){
        mPrefs = PreferenceManager.getDefaultSharedPreferences(app_context);
        mEdit = mPrefs.edit();
    }

    void clearPrefs(){
        mEdit.clear();
        mEdit.apply();
    }

    void setString(String valueKey, String value){
        mEdit.putString(valueKey, value);
        mEdit.apply();
    }
    String getString(String valueKey, String valueDefault){
        return mPrefs.getString(valueKey, valueDefault);
    }
    void setBool(String valueKey, boolean value){
        mEdit.putBoolean(valueKey, value);
        mEdit.apply();
    }
    boolean getBool(String valueKey, boolean valueDefault){
        return mPrefs.getBoolean(valueKey, valueDefault);
    }
    void setInt(String valueKey, int value){
        mEdit.putInt(valueKey, value);
        mEdit.apply();
    }
    int getInt(String valueKey, int valueDefault){
        return mPrefs.getInt(valueKey, valueDefault);
    }
    void setFloat(String valueKey, int value){
        mEdit.putInt(valueKey, value);
        mEdit.apply();
    }
    int getFloat(String valueKey, int valueDefault){
        return mPrefs.getInt(valueKey, valueDefault);
    }

    public Object onTrigger(dataEnums.ePreferencesCommands p_commands, List<Object> p_data){
        if(p_commands == dataEnums.ePreferencesCommands.M_GET_BOOL){
            return getBool((String)p_data.get(0), (boolean)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_GET_INT){
            return getInt((String)p_data.get(0), (int)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_GET_STRING){
            return getString((String)p_data.get(0), (String)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_GET_FLOAT){
            return getFloat((String)p_data.get(0), (int)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_SET_BOOL){
            setBool((String)p_data.get(0), (boolean)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_SET_INT){
            setInt((String)p_data.get(0), (int)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_SET_STRING){
            setString((String)p_data.get(0), (String)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_SET_FLOAT){
            setFloat((String)p_data.get(0), (int)p_data.get(1));
        }
        else if(p_commands == dataEnums.ePreferencesCommands.M_CLEAR_PREFS){
            clearPrefs();
        }

        return null;
    }

}
