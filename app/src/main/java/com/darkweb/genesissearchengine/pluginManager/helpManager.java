package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

class helpManager
{

    /*Private Variables */

    private AppCompatActivity mAppContext;
    private eventObserver.eventListener mEvent;

    helpManager(AppCompatActivity app_context, eventObserver.eventListener event) {
        this.mAppContext = app_context;
        this.mEvent = event;
    }

}
