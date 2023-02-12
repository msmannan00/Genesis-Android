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
    private GeckoSession.HistoryDelegate.HistoryList mHistory = null;
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
    public void onHistoryStateChange(@NonNull GeckoSession var1, @NonNull GeckoSession.HistoryDelegate.HistoryList var2) {
        mHistory = var2;
        if(mHistory !=null){
            setURL(mHistory.get(mHistory.getCurrentIndex()).getUri());
            mEvent.invokeObserver(Arrays.asList(mHistory, mGeckoDataModel.mSessionID), homeEnums.eGeckoCallback.ON_URL_LOAD);
            if(mCurrentIndex != var2.getCurrentIndex()){
                mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mHistory.get(mHistory.getCurrentIndex()).getTitle(), mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme, mGeckoSession), homeEnums.eGeckoCallback.ON_UPDATE_SEARCH_BAR);
            }

            Object mID = mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mHistory.get(mHistory.getCurrentIndex()).getTitle(), -1, mGeckoDataModel.mTheme, mGeckoSession, false), homeEnums.eGeckoCallback.ON_UPDATE_HISTORY);
            if (mID != null) {
                mGeckoDataModel.mCurrentURL_ID = (int) mID;
            }
        }
        mCurrentIndex = var2.getCurrentIndex();
    }

    /*Local Triggers*/

    public void setURL(String pURL) {
        if (helperMethod.getHost(pURL).endsWith(".onion")) {
            pURL = pURL.replace("www.", "");
        }

        this.mGeckoDataModel.mCurrentURL = pURL;
        intentHandler.actionDial(pURL, mContext);
    }

    public boolean isHistoryEmpty(){
        if(mHistory!=null && mHistory.size()>0){
            return false;
        }else {
            return true;
        }
    }

}