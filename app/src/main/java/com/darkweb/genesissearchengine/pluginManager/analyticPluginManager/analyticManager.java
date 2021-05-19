package com.darkweb.genesissearchengine.pluginManager.analyticPluginManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.flurry.android.FlurryAgent;
import java.lang.ref.WeakReference;
import java.util.List;

public class analyticManager
{
    /*Private Variables*/

    private WeakReference<AppCompatActivity> mAppContext;

    /*Initializations*/

    public analyticManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent){
        this.mAppContext = pAppContext;
        initialize();
    }

    private void initialize()
    {
        if(status.sDeveloperBuild){
            new FlurryAgent.Builder() .withLogEnabled(true) .build(mAppContext.get().getApplicationContext(), "4C4K4T5ND9RJKT4H47GQ");
        }else {
            new FlurryAgent.Builder() .withLogEnabled(true) .build(mAppContext.get().getApplicationContext(), "5RQYRV23928K6DXH8VWV");
        }
    }

    /*External Triggers*/

    private void logEvent(String pValue)
    {
        if(FlurryAgent.isSessionActive()){
            FlurryAgent.logEvent(pValue);
        }
    }

    public void onTrigger(List<Object> pData, pluginEnums.eAnalyticManager pEventType) {
        if(pEventType.equals(pluginEnums.eAnalyticManager.M_LOG_EVENT))
        {
        }
    }
}
