package com.darkweb.genesissearchengine.pluginManager;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.google.android.gms.ads.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.darkweb.genesissearchengine.constants.status.sPaidStatus;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_SHOW_LOADED_ADS;

class adManager
{

    /*Private Variables */

    private AppCompatActivity mAppContext;
    private eventObserver.eventListener mEvent;
    private AdView mBannerAds;

    private boolean bannerAdsLoading = false;
    private boolean bannerAdsLoaded = false;

    /*Initializations*/

    adManager(AppCompatActivity pAppContext, eventObserver.eventListener pEvent, AdView pBannerAds) {
        this.mAppContext = pAppContext;
        this.mEvent = pEvent;
        mBannerAds = pBannerAds;
    }

    private void initializeBannerAds(){
        if(!sPaidStatus){
            AdRequest request = new AdRequest.Builder().build();
            new Timer().schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    mAppContext.runOnUiThread(() -> {
                        mBannerAds.loadAd(request);
                        admobListeners();
                    });
                }
            }, 1500);
        }
    }

    /*Local Helper Methods*/

    private void loadAds(){
        if(!sPaidStatus)
        {
            if (!bannerAdsLoading)
            {
                bannerAdsLoading = true;
                MobileAds.initialize(mAppContext, initializationStatus -> { });

                initializeBannerAds();
            }
        }
    }

    private boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

    /*Local Listeners*/

    private void admobListeners(){
        if(!sPaidStatus){
            mBannerAds.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    bannerAdsLoaded = true;
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdClosed() {
                }
            });
        }
    }

    /*External Triggers*/

    Object onTrigger(List<Object> pData, pluginEnums.eAdManager pEventType) {
        if(pEventType.equals(pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS))
        {
            loadAds();
        }
        else if(pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED))
        {
            return isAdvertLoaded();
        }
        return null;
    }
}
