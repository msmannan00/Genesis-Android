package com.hiddenservices.genesissearchengine.production.dataManager.models;

import android.widget.ImageView;
import java.util.Calendar;
import java.util.Date;

public class historyRowModel
{
    /*Private Variables*/

    private int mID;
    private String mHeader;
    private String mDescription;
    private Date mDate;
    private ImageView mLogo;

    /*Initializations*/

    public historyRowModel(String pHeader, String pDescription,int pID) {
        this.mID = pID;
        this.mHeader = pHeader;
        this.mDescription = pDescription;
        mDate = Calendar.getInstance().getTime();
    }

    /*Variable Setters*/

    public void setHeader(String pHeader){
        this.mHeader = pHeader;
    }
    public void setURL(String pURL){
        this.mDescription = pURL;
    }
    public void setLogo(ImageView pLogo){
        this.mLogo = pLogo;
    }
    public void setDate(Date pDate) {
        mDate = pDate;
    }

    /*Variable Getters*/

    public String getHeader() {
        return mHeader;
    }
    public String getDescription() {
        if(mDescription!=null && mDescription.equals("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion")){
            return "genesis.onion";
        }
        return mDescription;
    }
    public String getDescriptionParsed() {
        return mDescription.substring(8);
    }
    public int getID() {
        return mID;
    }
    public ImageView getLogo() {
        return mLogo;
    }
    public Date getDate() {
        return mDate;
    }
}
