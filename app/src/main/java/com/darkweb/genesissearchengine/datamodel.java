package com.darkweb.genesissearchengine;

public class datamodel
{
    private static final datamodel ourInstance = new datamodel();
    private boolean isLoadingURL = false;

    public static datamodel getInstance()
    {
        return ourInstance;
    }

    private datamodel()
    {
    }

    public boolean getIsLoadingURL()
    {
        return isLoadingURL;
    }

    public void setIsLoadingURL(boolean status)
    {
        isLoadingURL = status;
    }

}
