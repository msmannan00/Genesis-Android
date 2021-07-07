package com.darkweb.genesissearchengine.pluginManager.adPluginManager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import java.lang.ref.WeakReference;
import java.util.List;
import static com.mopub.common.logging.MoPubLog.LogLevel.INFO;

public class adManager
{

    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<MoPubView> mBannerAds;

    private int mRequestCount = 0;
    private boolean mPaidStatus;
    private boolean bannerAdsLoading = false;
    private boolean bannerAdsLoaded = false;

    /*Initializations*/

    public adManager(eventObserver.eventListener pEvent, MoPubView pBannerAds, boolean pPaidStatus,Context pContext) {
        this.mEvent = pEvent;
        this.mPaidStatus = pPaidStatus;
        this.mBannerAds = new WeakReference(pBannerAds);
        initializeBannerAds(pContext);
    }

    private void initializeBannerAds(Context pContext){
        if(!mPaidStatus){
            final SdkConfiguration.Builder configBuilder = new SdkConfiguration.Builder("c122efbe224f46678800d2f73389d258");
            configBuilder.withLogLevel(INFO);
            MoPub.initializeSdk(pContext, configBuilder.build(), initSdkListener());
            admobListeners();
        }
    }

    private SdkInitializationListener initSdkListener() {
        return () -> {
        };
    }
    /*Local Helper Methods*/

    private void loadAds(){
        if(!mPaidStatus)
        {
            if (!bannerAdsLoading)
            {
                bannerAdsLoading = true;
                mBannerAds.get().setAdUnitId("c122efbe224f46678800d2f73389d258");
                mBannerAds.get().loadAd();
            }
        }
    }

    private boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

    /*Local Listeners*/

    private void admobListeners(){
        if(!mPaidStatus){
            mBannerAds.get().setBannerAdListener(new MoPubView.BannerAdListener() {
                @Override
                public void onBannerLoaded(@NonNull MoPubView moPubView) {
                    bannerAdsLoaded = true;
                }

                @Override
                public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {

                    new Handler().postDelayed(() ->
                    {
                        if(mRequestCount<10){
                            mRequestCount+=1;
                            mBannerAds.get().loadAd();
                        }

                    }, 30000);
                }

                @Override
                public void onBannerClicked(MoPubView moPubView) {
                    Log.i("asd","asd");
                }

                @Override
                public void onBannerExpanded(MoPubView moPubView) {
                    Log.i("asd","asd");
                }

                @Override
                public void onBannerCollapsed(MoPubView moPubView) {
                    Log.i("asd","asd");
                }
            });
        }
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, pluginEnums.eAdManager pEventType) {
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
