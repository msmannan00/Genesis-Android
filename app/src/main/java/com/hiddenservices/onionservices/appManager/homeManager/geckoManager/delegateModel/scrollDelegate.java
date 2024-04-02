package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;


import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.dataModel.geckoDataModel;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.mozilla.geckoview.GeckoSession;
import java.util.Arrays;

public class scrollDelegate implements GeckoSession.ScrollDelegate {

    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private geckoDataModel mGeckoDataModel;

    private int mScrollOffset = 0;

    /*Initializations*/

    public scrollDelegate(eventObserver.eventListener pEvent, geckoDataModel pGeckoDataModel) {
        this.mEvent = pEvent;
        this.mGeckoDataModel = pGeckoDataModel;
    }

    /*Scroll Delegate*/
    @UiThread
    public void onScrollChanged(@NonNull GeckoSession session, int scrollX, int scrollY) {
        mScrollOffset = scrollY;
        mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_UPDATE_PIXEL_BACKGROUND);
        if (scrollY <= 3) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_ON_SCROLL_TOP_BOUNDARIES);
        } else if (scrollY <= helperMethod.pxFromDp(30)) {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_ON_SCROLL_BOUNDARIES);
        } else {
            mEvent.invokeObserver(Arrays.asList(mGeckoDataModel.mCurrentURL, mGeckoDataModel.mSessionID, mGeckoDataModel.mCurrentTitle, mGeckoDataModel.mCurrentURL_ID, mGeckoDataModel.mTheme), homeEnums.eGeckoCallback.M_ON_SCROLL_NO_BOUNDARIES);
        }

    }

    public int getScrollOffset() {
        return mScrollOffset;
    }

}