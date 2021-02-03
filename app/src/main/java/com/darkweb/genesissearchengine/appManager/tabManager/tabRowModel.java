package com.darkweb.genesissearchengine.appManager.tabManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.darkweb.genesissearchengine.appManager.homeManager.geckoManager.geckoSession;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

public class tabRowModel
{
    /*Private Variables*/

    private geckoSession mSession;
    private String mId;
    private Bitmap mBitmap = null;
    private String mDate;

    /*Initializations*/

    public tabRowModel(geckoSession mSession) {
        this.mSession = mSession;
        this.mId = mSession.getSessionID();
        this.mDate = helperMethod.getCurrentDate();
    }

    public tabRowModel(String pID, String pDate, byte[] pBlob) {
        this.mId = pID;
        this.mDate = pDate;
        if(pBlob!=null){
            mBitmap = BitmapFactory.decodeByteArray(pBlob,0,pBlob.length);
        }
    }

   /*Helper Method*/

    public geckoSession getSession()
    {
        return mSession;
    }

    public void setSession(geckoSession pSession, String pURL, String pTitle, String pTheme)
    {
        mSession = pSession;
        mSession.setTitle(pTitle);
        mSession.setURL(pURL);
        mSession.setTheme(pTheme);
    }

    public String getmId() {
        return mId;
    }

    public void setmBitmap(Bitmap pBitmap) {
        mBitmap = null;
        mBitmap = pBitmap;
    }
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getDate(){
        return mDate;
    }
}
