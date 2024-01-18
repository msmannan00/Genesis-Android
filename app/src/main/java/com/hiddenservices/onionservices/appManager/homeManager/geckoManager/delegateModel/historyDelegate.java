package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;


import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.intentHandler;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;

import org.mozilla.geckoview.GeckoSession;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class historyDelegate implements GeckoSession.HistoryDelegate {

    /*Private Variables*/

    private WeakReference<AppCompatActivity> mContext;
    private HistoryList mHistory = null;
    private eventObserver.eventListener mEvent;
    private geckoDataModel mGeckoDataModel;
    private geckoSession mGeckoSession;
    private int mCurrentIndex = 0;

    /*Initializations*/

    public historyDelegate(WeakReference<AppCompatActivity> pContext, eventObserver.eventListener pEvent, geckoDataModel pGeckoDataModel, geckoSession pGeckoSession) {
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mGeckoDataModel = pGeckoDataModel;
        this.mGeckoSession = pGeckoSession;
    }

    @UiThread
    public void onHistoryStateChange(@NonNull GeckoSession var1, @NonNull HistoryList var2) {
        boolean mHistoryChanged = false;
        if(mHistory!=null){
            mHistoryChanged = mHistory.size()!=var2.size() || mHistory.size()!=var2.getCurrentIndex();
        }
        mHistory = var2;
        if(mHistory !=null){
            try {
                if(mHistoryChanged){
                    if(!mHistory.get(mHistory.getCurrentIndex()).getUri().equals("about:blank")){
                        setURL(mHistory.get(mHistory.getCurrentIndex()).getUri());
                    }
                }
                mEvent.invokeObserver(Arrays.asList(mHistory, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.ON_URL_LOAD);
                if(mCurrentIndex != var2.getCurrentIndex() && mHistoryChanged){
                    mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mHistory.get(mHistory.getCurrentIndex()).getTitle(), mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, mGeckoSession), homeEnums.eGeckoCallback.ON_UPDATE_SEARCH_BAR);
                }
                Object mID = mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mHistory.get(mHistory.getCurrentIndex()).getTitle(), -1, mGeckoDataModel.mTheme, mGeckoSession, false), homeEnums.eGeckoCallback.ON_UPDATE_HISTORY);
                if (mID != null) {
                    mGeckoDataModel.mCurrentURL_ID = (int) mID;
                }
            }catch (Exception ex){}
        }
        mCurrentIndex = var2.getCurrentIndex();
    }

    /*Local Triggers*/

    public void setURL(String pURL) {
        if (helperMethod.getHost(pURL).endsWith(".onion")) {
            pURL = pURL.replace("www.", "");
        }

        boolean status = intentHandler.actionIntent(pURL, mContext);
        if(status){
            this.mGeckoDataModel.mCurrentURL = pURL;
        }
    }

}