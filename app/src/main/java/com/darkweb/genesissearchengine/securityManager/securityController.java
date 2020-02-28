package com.darkweb.genesissearchengine.securityManager;

public class securityController
{
    private static final securityController ourInstance = new securityController();

    public static securityController getInstance()
    {
        return ourInstance;
    }

    private securityController()
    {

    }
}
