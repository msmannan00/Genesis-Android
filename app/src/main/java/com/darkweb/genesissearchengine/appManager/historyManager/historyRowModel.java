package com.darkweb.genesissearchengine.appManager.historyManager;

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
        return mDescription;
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
