package com.darkweb.genesissearchengine.pluginManager;

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
        //Fabric.with(app_model.getInstance().getAppContext(), new Crashlytics());
        //analyticmanager.getInstance().initialize(app_model.getInstance().getAppContext());
        //analyticmanager.getInstance().logUser();
    }

    public void sendEvent(String value)
    {
        //analyticmanager.getInstance().sendEvent(value);
    }
}
