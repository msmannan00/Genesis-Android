package com.darkweb.genesissearchengine.pluginManager.adPluginManager;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.google.android.gms.ads.*;
import java.lang.ref.WeakReference;
import java.util.List;

public class adManager
{

    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<AdView> mBannerAds;
    private boolean mPaidStatus;

    private boolean bannerAdsLoading = false;
    private boolean bannerAdsLoaded = false;

    /*Initializations*/

    public adManager(eventObserver.eventListener pEvent, AdView pBannerAds, boolean pPaidStatus) {
        this.mEvent = pEvent;
        this.mPaidStatus = pPaidStatus;
        this.mBannerAds = new WeakReference(pBannerAds);
    }

    private void initializeBannerAds(){
        if(!mPaidStatus){
            AdRequest request = new AdRequest.Builder().build();
            mBannerAds.get().loadAd(request);
            admobListeners();
        }
    }

    /*Local Helper Methods*/

    private void loadAds(AppCompatActivity pAppContext){
        if(!mPaidStatus)
        {
            if (!bannerAdsLoading)
            {
                bannerAdsLoading = true;
                MobileAds.initialize(pAppContext.getApplicationContext(), initializationStatus -> { });

                initializeBannerAds();
            }
        }
    }

    private boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

    /*Local Listeners*/

    private void admobListeners(){
        if(!mPaidStatus){
            mBannerAds.get().setAdListener(new AdListener() {
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
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError var1) {
                }
            });
        }
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, pluginEnums.eAdManager pEventType) {
        if(pEventType.equals(pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS))
        {
            loadAds((AppCompatActivity)pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED))
        {
            return isAdvertLoaded();
        }
        return null;
    }
}
