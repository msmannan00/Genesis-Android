package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
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

    public void init(Context context)
    {
        Fabric.with(context, new Crashlytics());
        analyticmanager.getInstance().initialize(home_model.getInstance().getAppContext());
        analyticmanager.getInstance().logUser();
    }

    /*Helper Methods*/

    public void sendEvent(String value)
    {
        //analyticmanager.getInstance().sendEvent(value);
    }
}
