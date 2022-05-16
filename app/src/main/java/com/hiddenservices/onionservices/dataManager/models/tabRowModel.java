package com.hiddenservices.onionservices.dataManager.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.helperManager.helperMethod;

import org.mozilla.geckoview.GeckoSession;

import java.io.ByteArrayInputStream;

public class tabRowModel {
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
        if (pBlob != null) {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
                mBitmap = null;
            }
            if (pBlob.length > 1000000) {
                mBitmap = null;
            } else {
                mBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(pBlob));
            }
        }
    }

    /*Helper Method*/

    public geckoSession getSession() {
        return mSession;
    }

    public void setSession(geckoSession pSession, String pURL, String pTitle, String pTheme, GeckoSession.SessionState pSessionState) {
        mSession = pSession;
        mSession.setTitle(pTitle);
        mSession.setURL(pURL);
        mSession.setTheme(pTheme);

        if (pSessionState != null) {
            mSession.mSessionState = pSessionState;
            mSession.restoreState(pSessionState);
        }

        if (!status.sSettingIsAppStarted) {
            if (pTitle.equals("$TITLE") || pTitle.startsWith("http://loading") || pTitle.startsWith("loading") || pURL.equals("$TITLE") || pURL.startsWith("http://loading") || pURL.startsWith("loading")) {
                mSession.setTitle("about:blank");
                mSession.setURL("about:blank");
            }
        }
    }

    public String getmId() {
        return mId;
    }

    public void decodeByteArraysetmBitmap(Bitmap pBitmap) {
        mBitmap = null;
        mBitmap = pBitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
    public void resetBitmap() {
        mBitmap = null;
    }

    public String getDate() {
        return mDate;
    }
}
