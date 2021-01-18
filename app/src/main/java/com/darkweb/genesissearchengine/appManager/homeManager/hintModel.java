package com.darkweb.genesissearchengine.appManager.homeManager;

import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;

import java.util.ArrayList;

public class hintModel
{
    /*Private Variables*/

    private String mHeader;
    private String mURL;

    public hintModel(String pHeader, String pURL){
        mHeader = pHeader;
        mURL = pURL;
    }

    public String  getHeader(){
        return mHeader;
    }

    public String getURL(){
        return mURL;
    }

}