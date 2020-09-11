package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

import java.util.UUID;

class analyticManager
{
    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private String mUniqueID = null;

    /*Initializations*/

    analyticManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.mAppContext = app_context;
        initialize();
    }

    private void initialize(){
        final String PREF_UNIQUE_ID = constants.UNIQUE_KEY_ID;

        if (mUniqueID == null)
        {
            SharedPreferences sharedPrefs = mAppContext.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            mUniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (mUniqueID == null) {

                new Thread(){
                    public void run(){
                        try{
                            mUniqueID = UUID.randomUUID().toString();
                        }catch (Exception ex){
                            mUniqueID = UUID.randomUUID().toString();
                        }

                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString(PREF_UNIQUE_ID, mUniqueID);
                        editor.apply();
                    }
                }.start();
            }
        }
    }

    /*Helper Methods*/

    void logUser(){
        Crashlytics.setUserIdentifier(mUniqueID);
        Crashlytics.setUserEmail(constants.USER_EMAIL);
        Crashlytics.setUserName(mUniqueID);
    }

}
