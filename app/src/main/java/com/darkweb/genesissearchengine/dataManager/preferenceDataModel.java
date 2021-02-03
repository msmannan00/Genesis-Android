package com.darkweb.genesissearchengine.dataManager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class preferenceDataModel {

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEdit;

    public preferenceDataModel(AppCompatActivity pAppContext){
        mPrefs = PreferenceManager.getDefaultSharedPreferences(pAppContext);
        mEdit = mPrefs.edit();
    }

    void clearPrefs(){
        mEdit.clear();
        mEdit.apply();
    }

    void setString(String pValueKey, String pValue){
        mEdit.putString(pValueKey, pValue);
        mEdit.apply();
    }
    String getString(String pValueKey, String pValueDefault){
        return mPrefs.getString(pValueKey, pValueDefault);
    }
    void setBool(String valueKey, boolean value){
        mEdit.putBoolean(valueKey, value);
        mEdit.apply();
    }
    boolean getBool(String pValueKey, boolean pValueDefault){
        return mPrefs.getBoolean(pValueKey, pValueDefault);
    }
    void setInt(String valueKey, int value){
        mEdit.putInt(valueKey, value);
        mEdit.apply();
    }
    int getInt(String pValueKey, int pValueDefault){
        return mPrefs.getInt(pValueKey, pValueDefault);
    }
    void setFloat(String pValueKey, int pValue){
        mEdit.putInt(pValueKey, pValue);
        mEdit.apply();
    }
    int getFloat(String pValueKey, int pValueDefault){
        return mPrefs.getInt(pValueKey, pValueDefault);
    }

    public Object onTrigger(dataEnums.ePreferencesCommands pCommands, List<Object> pData){
        if(pCommands == dataEnums.ePreferencesCommands.M_GET_BOOL){
            return getBool((String)pData.get(0), (boolean)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_GET_INT){
            return getInt((String)pData.get(0), (int)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_GET_STRING){
            return getString((String)pData.get(0), (String)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_GET_FLOAT){
            return getFloat((String)pData.get(0), (int)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_SET_BOOL){
            setBool((String)pData.get(0), (boolean)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_SET_INT){
            setInt((String)pData.get(0), (int)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_SET_STRING){
            setString((String)pData.get(0), (String)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_SET_FLOAT){
            setFloat((String)pData.get(0), (int)pData.get(1));
        }
        else if(pCommands == dataEnums.ePreferencesCommands.M_CLEAR_PREFS){
            clearPrefs();
        }

        return null;
    }

}
