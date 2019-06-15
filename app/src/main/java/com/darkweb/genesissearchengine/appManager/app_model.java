package com.darkweb.genesissearchengine.appManager;

import android.content.Context;

public class app_model
{
    /*Data Objects*/
    private static String currentURL = "http://boogle.store/";
    private static int port = 9150;
    private boolean isLoadingURL = false;
    private Context appContext;
    private application_controller appInstance;


    /*Initializations*/
    private static final app_model ourInstance = new app_model();
    public static app_model getInstance()
    {
        return ourInstance;
    }


    /*Getters Setters*/
    public boolean getIsLoadingURL()
    {
        return isLoadingURL;
    }

    public void setIsLoadingURL(boolean status)
    {
        isLoadingURL = status;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getCurrentURL()
    {
        return currentURL;
    }

    public void setCurrentURL(String currentURL)
    {
        this.currentURL = currentURL;
    }

    public Context getAppContext()
    {
        return appContext;
    }

    public void setAppContext(Context appContext)
    {
        this.appContext = appContext;
    }

    public application_controller getAppInstance()
    {
        return appInstance;
    }

    public void setAppInstance(application_controller appInstance)
    {
        this.appInstance = appInstance;
    }

}
