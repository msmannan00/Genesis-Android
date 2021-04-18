package com.darkweb.genesissearchengine.pluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.flurry.android.FlurryAgent;
import java.lang.ref.WeakReference;
import java.util.List;

class analyticManager
{
    /*Private Variables*/

    private WeakReference<AppCompatActivity> mAppContext;

    /*Initializations*/

    analyticManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent){
        this.mAppContext = pAppContext;
        initialize();
    }

    private void initialize()
    {
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(mAppContext.get().getApplicationContext(), "BKFSCH4CRS6RB9HSCM9H");
    }

    /*External Triggers*/

    private void logEvent(String pValue)
    {
        if(FlurryAgent.isSessionActive()){
            FlurryAgent.logEvent(pValue);
        }
    }

    void onTrigger(List<Object> pData, pluginEnums.eAnalyticManager pEventType) {
        if(pEventType.equals(pluginEnums.eAnalyticManager.M_LOG_EVENT))
        {
        }
    }
}
