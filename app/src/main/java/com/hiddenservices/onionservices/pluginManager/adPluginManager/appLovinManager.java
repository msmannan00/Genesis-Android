package com.hiddenservices.onionservices.pluginManager.adPluginManager;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.example.myapplication.R;
import com.facebook.ads.AdSettings;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.advertManager.advertController;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import java.lang.ref.WeakReference;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;

import androidx.appcompat.app.AppCompatActivity;

public class appLovinManager implements MaxAdViewAdListener {
    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<MaxAdView> mBannerAds;

    private int mRequestCount = 0;
    private boolean bannerAdRequested = false;
    private boolean bannerAdsLoaded = false;
    private Context mContext = null;

    /*Initializations*/

    public appLovinManager(eventObserver.eventListener pEvent, MaxAdView pBannerAds, Context pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);
        initializeBannerAds(pContext);
        mContext = pContext;
    }

    private void initializeBannerAds(Context pContext) {
        AppLovinSdk.getInstance(pContext).setMediationProvider("max");
        AppLovinSdk.initializeSdk(pContext,(AppLovinSdk.SdkInitializationListener) configuration -> {
            mBannerAds.get().loadAd();
        });
    }

    /*Local Helper Methods*/

    private void loadAds() {
        if (!bannerAdRequested) {
            bannerAdRequested = true;
            mBannerAds.get().setListener(this);
        }
    }

    private boolean isAdvertLoaded() {
        return bannerAdsLoaded;
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
        bannerAdsLoaded = true;
        mEvent.invokeObserver(null, M_ON_AD_LOAD);
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        Log.i("ads load", "ad has been loaded");
    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {
        status.sIsBackgroundAdvertCheck = true;
        new Handler().postDelayed(() ->
        {
            status.sIsBackgroundAdvertCheck = false;
        }, 5000);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        new Handler().postDelayed(() ->
        {
            if (mRequestCount <= 50) {
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

