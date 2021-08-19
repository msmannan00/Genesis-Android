package com.darkweb.genesissearchengine.pluginManager.adPluginManager;

import android.content.Context;
import android.util.Log;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_CLICK;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_HIDE;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;

public class admobManager extends AdListener {

    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<AdView> mBannerAds;

    private boolean bannerAdsLoaded = false;
    private boolean mAdvertClicked = false;
    private static boolean mBannerAdvertLoadReqest = false;

    /*Initializations*/

    public admobManager(eventObserver.eventListener pEvent, AdView pBannerAds, Context pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);
        loadAds(pContext);
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
        onDestroy();
        mAdvertClicked = true;
    }

    @Override
    public void onAdOpened(){
        super.onAdClicked();
        mEvent.invokeObserver(null, M_ON_AD_CLICK);
        onDestroy();
        mAdvertClicked = true;
    }

    @Override
    public void onAdFailedToLoad(LoadAdError var1){
        Log.i("asd","asd");
    }

    /*Local Helper Methods*/

    private void loadAds(Context pContext){
        if(!mBannerAdvertLoadReqest){
            mBannerAdvertLoadReqest = true;


            AdRequest adRequest = new AdRequest.Builder().build();
            mBannerAds.get().setAdListener(this);
            mBannerAds.get().loadAd(adRequest);


        }else {
            onDestroy();
            mEvent.invokeObserver(null, M_ON_AD_HIDE);
        }
    }


    private boolean isAdvertLoaded(){
        if(mAdvertClicked){
            return false;
        }else {
            return bannerAdsLoaded;
        }
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
