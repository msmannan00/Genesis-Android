package com.darkweb.genesissearchengine.pluginManager.adPluginManager;

import android.content.Context;
import android.util.Log;

import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.lang.ref.WeakReference;
import java.util.List;

import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_CLICK;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;

public class admobManager extends AdListener {

    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<AdView> mBannerAds;

    private boolean bannerAdsLoaded = false;

    /*Initializations*/

    public admobManager(eventObserver.eventListener pEvent, AdView pBannerAds, Context pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);
        loadAds();
    }

    /*Local Overrides*/

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        bannerAdsLoaded = true;
        mEvent.invokeObserver(null, M_ON_AD_LOAD);
    }

    @Override
    public void onAdClicked(){
        super.onAdClicked();
        mEvent.invokeObserver(null, M_ON_AD_CLICK);
    }

    /*Local Helper Methods*/

    private void loadAds(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAds.get().loadAd(adRequest);
        mBannerAds.get().setAdListener(this);
    }


    private boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

    private void onDestroy(){
        mBannerAds.get().destroy();
    }

    /*External Triggers*/

    public Object onTrigger(pluginEnums.eAdManager pEventType) {
        if(pEventType.equals(pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS))
        {
        }
        else if(pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED))
        {
            return isAdvertLoaded();
        }
        else if(pEventType.equals(pluginEnums.eAdManager.M_DESTROY))
        {
            onDestroy();
        }
        return null;
    }
}
