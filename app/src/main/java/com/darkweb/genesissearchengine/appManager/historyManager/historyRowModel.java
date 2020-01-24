package com.darkweb.genesissearchengine.appManager.historyManager;

import com.darkweb.genesissearchengine.constants.strings;

public class historyRowModel
{
    /*Private Variables*/

    private int mId;
    private String mTitle;
    private String mHeader;
    private String mDescription;

    /*Initializations*/

    public historyRowModel(String mHeader, String mDescription,int mId) {
        this.mId = mId;
        this.mHeader = mHeader;
        this.mDescription = mDescription;
        this.mTitle = strings.EMPTY_STR;
    }

    /*Variable Getters*/

    public void updateTitle(String mTitle){
        this.mTitle = mTitle;
    }
    public void updateURL(String url){
        this.mHeader = url;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getmHeader() {
        return mHeader;
    }
    public String getmDescription() {
        return mDescription;
    }
    public int getmId() {
        return mId;
    }
}
