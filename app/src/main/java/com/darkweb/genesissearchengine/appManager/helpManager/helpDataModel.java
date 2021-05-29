package com.darkweb.genesissearchengine.appManager.helpManager;

public class helpDataModel {

    private String mHeader;
    private String mDescription;
    private String mIcon;

    public helpDataModel(String pHeader, String pDescription, String pIcon) {
        this.mHeader = pHeader;
        this.mDescription = pDescription;
        this.mIcon = pIcon;
    }

    public String getHeader() {
        return mHeader;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getIconID() {
        return mIcon;
    }

}

