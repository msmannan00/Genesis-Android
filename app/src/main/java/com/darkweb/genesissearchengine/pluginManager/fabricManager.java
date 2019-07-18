package com.darkweb.genesissearchengine.pluginManager;

import com.crashlytics.android.Crashlytics;
import com.darkweb.genesissearchengine.appManager.home_activity.app_model;
import io.fabric.sdk.android.Fabric;

public class fabricManager
{
    /*Private Variables*/

    private static final fabricManager ourInstance = new fabricManager();

    /*Initializations*/

    public static fabricManager getInstance()
    {
        return ourInstance;
    }

    private fabricManager()
    {
    }

    public void init()
    {
        Fabric.with(app_model.getInstance().getAppContext(), new Crashlytics());
        analyticmanager.getInstance().initialize(app_model.getInstance().getAppContext());
        analyticmanager.getInstance().logUser();
    }

    /*Helper Methods*/

    public void sendEvent(String value)
    {
        analyticmanager.getInstance().sendEvent(value);
    }
}
