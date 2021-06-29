package com.darkweb.genesissearchengine.pluginManager.analyticPluginManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.flurry.android.FlurryAgent;
import java.lang.ref.WeakReference;
import java.util.List;

public class analyticManager
{
    /*Private Variables*/

    private WeakReference<AppCompatActivity> mAppContext;
    private boolean mIsDeveloperBuild;

    /*Initializations*/

    public analyticManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent, boolean pIsDeveloperBuild){
        this.mAppContext = pAppContext;
        this.mIsDeveloperBuild = pIsDeveloperBuild;

        initialize();
    }

    private void initialize()
    {
        new FlurryAgent.Builder() .withLogEnabled(false) .build(mAppContext.get().getApplicationContext(), "4C4K4T5ND9RJKT4H47GQ");
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
