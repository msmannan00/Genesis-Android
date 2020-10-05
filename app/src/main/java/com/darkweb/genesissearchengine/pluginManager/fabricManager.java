package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

class fabricManager
{
    /*Private Variables*/

    private AppCompatActivity mAppContext;
    private eventObserver.eventListener mEvent;

    /*Initializations*/

    fabricManager(AppCompatActivity mAppContext, eventObserver.eventListener mEvent){
        this.mAppContext = mAppContext;
        this.mEvent = mEvent;
        initialize();
    }

    private void initialize(){
    }
}
