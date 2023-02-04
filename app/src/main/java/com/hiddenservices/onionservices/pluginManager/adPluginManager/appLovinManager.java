package com.hiddenservices.onionservices.pluginManager.adPluginManager;

import android.view.View;
import com.applovin.sdk.AppLovinSdk;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import androidx.appcompat.app.AppCompatActivity;

public class appLovinManager {

    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private applovinSupportManager mSupportManager;
    private applovinBannerManager mBannerManager;

    private boolean mLowMemoryReached = false;

    /*Initializations*/

    public appLovinManager(eventObserver.eventListener pEvent, View pBannerAds, AppCompatActivity pContext) {
        this.mEvent = pEvent;
        onInitializeAdvertisement(pContext);
    }

    private void onInitializeAdvertisement(AppCompatActivity pContext) {
        AppLovinSdk.getInstance(pContext).getSettings().setVerboseLogging(true);
        AppLovinSdk.getInstance(pContext).setMediationProvider("max");
        AppLovinSdk.initializeSdk(pContext, configuration -> {
            if(!mLowMemoryReached){
                this.mSupportManager = new applovinSupportManager(pContext);
                this.mBannerManager = new applovinBannerManager(pContext, mEvent);
            }
        });
    }

    /*Local Helper Methods*/

    private void onShowInterstitial() {
        this.mSupportManager.onShow();
    }

    private void onToggleBannerShow(boolean pStatus) {
        this.mBannerManager.onShow(pStatus);
    }

    /*Helper Methods*/

    private void onDestroy() {
        mLowMemoryReached = true;
        mBannerManager.onDestroy();
    }

    /*External Triggers*/

    public Object onTrigger(pluginEnums.eAdManager pEventType) {
        if (pEventType.equals(pluginEnums.eAdManager.M_LOW_MEMORY_DESTROY)) {
            onDestroy();
        } else if (pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED)) {
            return true;
        } else if (pEventType.equals(pluginEnums.eAdManager.M_SHOW_BANNER)) {
            onToggleBannerShow(true);
        } else if (pEventType.equals(pluginEnums.eAdManager.M_HIDE_BANNER)) {
            onToggleBannerShow(false);
        } else if (pEventType.equals(pluginEnums.eAdManager.M_SHOW_INTERSTITIAL)) {
            onShowInterstitial();
        }
        return null;
    }

}

