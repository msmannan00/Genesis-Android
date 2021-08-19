package com.darkweb.genesissearchengine.dataManager.models;

public class crawlerRowModel
{
    /*Private Variables*/

    private String mURL;
    private String mHTML;

    /*Initializations*/

    public crawlerRowModel(String pHeader, String pDescription) {
        mURL = pHeader;
        mHTML = pDescription;
    }

    /*Variable Getters*/

    public String getURL(){
        return mURL;
    }

    public String getHTML(){
        return mHTML;
    }

}
