package com.darkweb.genesissearchengine.pluginManager;

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
        //Fabric.with(home_model.getInstance().getAppContext(), new Crashlytics());
        //analyticmanager.getInstance().initialize(home_model.getInstance().getAppContext());
        //analyticmanager.getInstance().logUser();
    }

    /*Helper Methods*/

    public void sendEvent(String value)
    {
        analyticmanager.getInstance().sendEvent(value);
    }
}
