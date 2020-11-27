package com.darkweb.genesissearchengine.appManager.tabManager;

import android.graphics.Bitmap;

import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import org.mozilla.geckoview.GeckoView;

public class tabRowModel
{
    /*Private Variables*/

    private geckoSession mSession;
    private int mId;
    private GeckoView mGeckoView = null;
    private Bitmap mBitmap = null;
    private String mDate;

    /*Initializations*/

    public tabRowModel(geckoSession mSession,int mId) {
        this.mSession = mSession;
        this.mId = mId;
        this.mDate = helperMethod.getCurrentDate();
    }

    /*Helper Method*/

    public geckoSession getSession()
    {
        return mSession;
    }

    public int getmId() {
        return mId;
    }
    public void setId(int id) {
        mId = id;
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
