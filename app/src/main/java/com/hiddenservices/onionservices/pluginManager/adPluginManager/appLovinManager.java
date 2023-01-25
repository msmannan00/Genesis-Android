package com.hiddenservices.onionservices.pluginManager.adPluginManager;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.example.myapplication.R;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import java.lang.ref.WeakReference;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;
import androidx.appcompat.app.AppCompatActivity;
import org.torproject.android.service.wrapper.orbotLocalConstants;

public class appLovinManager implements MaxAdViewAdListener {
    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<MaxAdView> mBannerAds;

    private int mRequestCount = 0;
    private boolean bannerAdRequested = false;
    private boolean bannerAdsLoaded = true;
    private boolean bannerAdsLoadedRepeat = false;
    private boolean lowmemory = false;
    private AppCompatActivity mContext = null;
    private MaxAdView adView;

    /*Initializations*/

    public appLovinManager(eventObserver.eventListener pEvent, View pBannerAds, AppCompatActivity pContext) {
        this.mEvent = pEvent;
        //this.mBannerAds = new WeakReference(pBannerAds);
        mContext = pContext;

        try {
            if(!lowmemory){
                initializeBannerAds(pContext);
            }
        }catch (OutOfMemoryError | Exception ex){
            Log.i("","");
        }

        mContext = pContext;
    }
    public void onInitAdvert(){

    }

    private void initializeBannerAds(AppCompatActivity pContext) {
        AppLovinSdk.getInstance(pContext).getSettings().setVerboseLogging(true);
        AppLovinSdk.getInstance(pContext).setMediationProvider("max");
        AppLovinSdk.initializeSdk(pContext, configuration -> {
            bannerAdRequested = true;
            showAd();
        });
    }

    /*Local Helper Methods*/

    private void loadAds() {
        onAdvertStatus(true);
    }

    private void showAd(){
        if(!lowmemory){
            try {
                adView = new MaxAdView( "642ec6302c7cecd2",mContext);
                onAdvertStatus(false);
                adView.setListener(this);
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int heightPx = helperMethod.pxFromDp(50);
                adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );
                adView.setAlpha(0);
                adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                adView.animate().alpha(1).setDuration(400);
                adView.setBackgroundColor(mContext.getResources().getColor(R.color.c_background));
                adView.setExtraParameter( "disable_precache", "true" );
                ViewGroup rootView = mContext.findViewById(android.R.id.content);
                rootView.addView( adView );
                adView.loadAd();
            }catch (Exception ex){}
        }
    }

    private boolean isAdvertLoaded() {
        return true;
    }

    private void onLowMemoryDestroy() {
        lowmemory = true;
        if(adView!=null){
            adView.destroy();
            adView = null;
        }
    }

    private void onAdvertStatus(boolean pStatus) {
        if(adView!=null && bannerAdRequested){
            if(pStatus){
                new Handler().postDelayed(() ->
                {
                    adView.setVisibility(View.VISIBLE);
                }, 500);
            }else {
              adView.setVisibility(View.GONE);
            }
        }
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
        bannerAdsLoadedRepeat = true;
        mEvent.invokeObserver(null, M_ON_AD_LOAD);
        orbotLocalConstants.sAdLoaded = true;
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        status.sAdLoaded = true;
        bannerAdsLoadedRepeat = true;
        orbotLocalConstants.sAdLoaded = true;
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        orbotLocalConstants.sAdLoaded = true;
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
        status.sAdLoaded = true;
        orbotLocalConstants.sAdLoaded = true;
        if(!bannerAdsLoadedRepeat){
            showAd();
        }
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        status.sAdLoaded = true;
        orbotLocalConstants.sAdLoaded = true;
    }

    /*External Triggers*/

    public Object onTrigger(pluginEnums.eAdManager pEventType) {
        if (pEventType.equals(pluginEnums.eAdManager.M_LOW_MEMORY_DESTROY)) {
            onLowMemoryDestroy();
        } else if (pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED)) {
            return isAdvertLoaded();
        } else if (pEventType.equals(pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS)) {
            loadAds();
        } else if (pEventType.equals(pluginEnums.eAdManager.M_SHOW)) {
            onAdvertStatus(true);
        } else if (pEventType.equals(pluginEnums.eAdManager.M_HIDE)) {
            onAdvertStatus(false);
        }
        return null;
    }

}

