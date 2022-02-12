package com.hiddenservices.genesissearchengine.production.pluginManager.adPluginManager;

import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_CLICK;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.genesissearchengine.production.eventObserver;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums;
import com.facebook.ads.*;
import org.mozilla.thirdparty.com.google.android.exoplayer2.util.Log;
import java.lang.ref.WeakReference;

public class facebookAdsManager implements AdListener  {

    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<LinearLayout> mBannerAds;
    private AdView adView;

    private boolean bannerAdsLoaded = false;

    /*Initializations*/

    public facebookAdsManager(eventObserver.eventListener pEvent, LinearLayout pBannerAds, AppCompatActivity pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);
        loadAds(pContext, pBannerAds);
    }

    /*Local Overrides*/
    @Override
    public void onError(Ad ad, AdError adError) {
        Log.i("asd","asd");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        bannerAdsLoaded = true;
        mEvent.invokeObserver(null, M_ON_AD_LOAD);
    }

    @Override
    public void onAdClicked(Ad ad) {
        mEvent.invokeObserver(null, M_ON_AD_CLICK);
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }


    /*Local Helper Methods*/

    private void loadAds(AppCompatActivity pContext, LinearLayout pLinearLayout){
        AudienceNetworkAds.initialize(pContext);
        adView = new AdView(pContext, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
        pLinearLayout.addView(adView);
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(this).build());
    }


    private boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

    private void onDestroy(){
        adView.destroy();
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
