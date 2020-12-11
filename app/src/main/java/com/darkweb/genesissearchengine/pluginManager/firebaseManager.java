package com.darkweb.genesissearchengine.pluginManager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.flurry.android.FlurryAgent;

class firebaseManager
{
    /*Private Variables*/

    private AppCompatActivity mAppContext;

    /*Initializations*/

    firebaseManager(AppCompatActivity app_context, eventObserver.eventListener event){
        this.mAppContext = app_context;
        initialize();
    }

    public void initialize()
    {
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(mAppContext, "BKFSCH4CRS6RB9HSCM9H");
    }

    /*Helper Methods*/

    void logEvent(String value)
    {
        if(FlurryAgent.isSessionActive()){
            FlurryAgent.logEvent(value);
        }
    }
}
