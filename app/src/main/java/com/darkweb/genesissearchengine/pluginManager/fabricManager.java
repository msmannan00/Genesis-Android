package com.darkweb.genesissearchengine.pluginManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.darkweb.genesissearchengine.appManager.app_model;
import io.fabric.sdk.android.Fabric;

public class fabricManager
{
    private static final fabricManager ourInstance = new fabricManager();

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

    public void sendEvent(String value)
    {
        analyticmanager.getInstance().sendEvent(value);
    }
}
