package com.hiddenservices.onionservices.pluginManager.adPluginManager;

import android.content.Context;
import android.os.Handler;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import java.lang.ref.WeakReference;

import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;

public class appLovinManager implements MaxAdViewAdListener {
    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<MaxAdView> mBannerAds;

    private int mRequestCount = 0;
    private boolean bannerAdRequested = false;

    /*Initializations*/

    public appLovinManager(eventObserver.eventListener pEvent, MaxAdView pBannerAds, Context pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);
        initializeBannerAds(pContext);
    }

    private void initializeBannerAds(Context pContext) {
        AdColonyAppOptions appOptions = new AdColonyAppOptions();
        AdColony.configure(activityContextManager.getInstance().getHomeController(), appOptions,"app3b56c67c45544c5c89");

        AppLovinSdk.getInstance(pContext).setMediationProvider("max");
        AppLovinSdk.initializeSdk(pContext, configuration -> {
        });
    }

    /*Local Helper Methods*/

    private void loadAds() {
        if (!bannerAdRequested) {
            bannerAdRequested = true;
            mBannerAds.get().loadAd();
            mBannerAds.get().setListener(this);
        }
    }

    private boolean isAdvertLoaded() {
        return false;
    }

    /* Overriden Methods */

    @Override
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        mEvent.invokeObserver(null, M_ON_AD_LOAD);
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        new Handler().postDelayed(() ->
        {
            if (mRequestCount <= 10) {
                mRequestCount += 1;
                bannerAdRequested = true;
                mBannerAds.get().loadAd();
                mBannerAds.get().setListener(this);
            }
        }, 10000);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }

    /*External Triggers*/

    public Object onTrigger(pluginEnums.eAdManager pEventType) {
        if (pEventType.equals(pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS)) {
            loadAds();
        } else if (pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED)) {
            return isAdvertLoaded();
        }
        return null;
    }

}

