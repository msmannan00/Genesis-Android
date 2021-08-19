package com.darkweb.genesissearchengine.pluginManager.adPluginManager;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;
import static com.mopub.common.logging.MoPubLog.LogLevel.INFO;

public class mopubManager implements MoPubView.BannerAdListener
{
    //70f6dfc0cde14baaaa25083653700416
    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<MoPubView> mBannerAds;
    private int mRequestCount = 0;

    private boolean bannerAdsLoaded = false;
    private boolean bannerAdRequested = false;

    /*Initializations*/

    public mopubManager(eventObserver.eventListener pEvent, MoPubView pBannerAds, Context pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);
        initializeBannerAds(pContext);
    }

    private void initializeBannerAds(Context pContext){
        final SdkConfiguration.Builder configBuilder = new SdkConfiguration.Builder(keys.ADMANAGER_APPID_KEY);
        configBuilder.withLogLevel(INFO);
        MoPub.initializeSdk(pContext, configBuilder.build(), initSdkListener());
    }

    private SdkInitializationListener initSdkListener() {
        return () -> {
        };
    }

    /* Local Overrides */

    @Override
    public void onBannerLoaded(@NonNull MoPubView moPubView) {
        bannerAdsLoaded = true;
        mEvent.invokeObserver(null, M_ON_AD_LOAD);
    }

    @Override
    public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
        new Handler().postDelayed(() ->
        {
            if(mRequestCount<=10){
                mRequestCount +=1;
                mBannerAds.get().setAdUnitId(keys.ADMANAGER_APPID_KEY);
                mBannerAds.get().loadAd();
                mBannerAds.get().setBannerAdListener(this);
            }
        }, 10000);
    }

    @Override
    public void onBannerClicked(MoPubView moPubView) {

    }

    @Override
    public void onBannerExpanded(MoPubView moPubView) {

    }

    @Override
    public void onBannerCollapsed(MoPubView moPubView) {

    }

    /*Local Helper Methods*/

    private void loadAds(){
        if(!bannerAdRequested){
            bannerAdRequested = true;
            mBannerAds.get().setAdUnitId(keys.ADMANAGER_APPID_KEY);
            mBannerAds.get().loadAd();
            mBannerAds.get().setBannerAdListener(this);
        }
    }

    private boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

   /*External Triggers*/

    public Object onTrigger(pluginEnums.eAdManager pEventType) {
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

