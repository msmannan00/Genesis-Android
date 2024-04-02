package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses;

import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.eventObserver;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import java.util.Arrays;

public class searchHandler {
    GeckoResult<GeckoSession.FinderResult> mFinder = null;

    private eventObserver.eventListener mEvent;
    private geckoSession mGeckoSession;

    /*Initializations*/

    public searchHandler(eventObserver.eventListener pEvent, geckoSession pGeckoSession) {
        this.mEvent = pEvent;
        this.mGeckoSession = pGeckoSession;
    }

    public void findInPage(String pQuery, int pDirection) {
        mFinder = null;
        mFinder = mGeckoSession.getFinder().find(pQuery, pDirection);
        new Thread() {
            public void run() {

                int mCounter = 0;
                while (mFinder == null) {
                    try {
                        mCounter += 1;
                        sleep(100);
                        if (mCounter > 100) {
                            return;
                        }
                    } catch (InterruptedException ignored) {
                    }
                }

                try {
                    GeckoSession.FinderResult mResult = mFinder.poll(1000);
                    mEvent.invokeObserver(Arrays.asList(mResult.total, mResult.current), homeEnums.eGeckoCallback.FINDER_RESULT_CALLBACK);
                } catch (Throwable ignored) {
                }
            }
        }.start();

    }

}
