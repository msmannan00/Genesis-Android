package com.hiddenservices.onionservices.appManager.languageManager;

public class languageDataModel {

    private String mHeader;
    private String mDescription;
    private String mTag;
    private String mCountry;

    protected languageDataModel(String pHeader, String pDescription, String pTag, String pCountry) {
        this.mHeader = pHeader;
        this.mDescription = pDescription;
        this.mTag = pTag;
        this.mCountry = pCountry;
    }

    public String getHeader() {
        return mHeader;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getTag() {
        return mTag;
    }
    public String getCountry() {
        return mCountry;
    }

}

