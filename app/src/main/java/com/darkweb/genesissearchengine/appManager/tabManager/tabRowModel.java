package com.darkweb.genesissearchengine.appManager.tabManager;

import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;

import org.mozilla.geckoview.GeckoView;

public class tabRowModel
{
    /*Private Variables*/

    private geckoSession mSession;
    private int mId;
    private GeckoView mGeckoView = null;

    /*Initializations*/

    public tabRowModel(geckoSession mSession,int mId) {
        this.mSession = mSession;
        this.mId = mId;
    }

    public geckoSession getSession()
    {
        return mSession;
    }

    public void setGeckoView(GeckoView pGeckoView){
        mGeckoView = pGeckoView;
    }

    public void releaseGeckoView(){
        if(mGeckoView!=null){
            mGeckoView.releaseSession();
        }
    }

    public int getmId() {
        return mId;
    }
    public void setId(int id) {
        mId = id;
    }

}
