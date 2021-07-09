package com.darkweb.genesissearchengine.appManager.landingManager.langingPageManager;


public class landingPageDataModel {

    private String mHeader;
    private String mSubHeader;
    private String mNextButtonText;
    private int mLandingPageType;

    public landingPageDataModel(String pHeader, String pSubHeader, String pNextButtonText, int pLandingPageType){
        this.mHeader = pHeader;
        this.mSubHeader = pSubHeader;
        this.mNextButtonText = pNextButtonText;
        this.mLandingPageType = pLandingPageType;
    }

    public String getHeader(){
        return mHeader;
    }
    public String getSubHeader(){
        return mSubHeader;
    }
    public String getNextButtonText(){
        return mNextButtonText;
    }
    public int getPageType(){
        return mLandingPageType;
    }

}
