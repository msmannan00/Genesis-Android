package com.darkweb.genesissearchengine.pluginManager.adPluginManager;

/*
import android.content.Context;
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

public class mopubManager implements MoPubView.BannerAdListener
{

    /*Private Variables */

    /*private eventObserver.eventListener mEvent;
    private WeakReference<MoPubView> mBannerAds;

    private boolean bannerAdsLoaded = false;

    /*Initializations*/

    /*public mopubManager(eventObserver.eventListener pEvent, MoPubView pBannerAds, boolean pPaidStatus, Context pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);
        initializeBannerAds(pContext);
    }

    private void initializeBannerAds(Context pContext){
        final SdkConfiguration.Builder configBuilder = new SdkConfiguration.Builder("c122efbe224f46678800d2f73389d258");
        configBuilder.withLogLevel(INFO);
        MoPub.initializeSdk(pContext, configBuilder.build(), initSdkListener());
    }

    private SdkInitializationListener initSdkListener() {
        return () -> {
        };
    }

    /* Local Overrides */

    /*@Override
    public void onBannerLoaded(@NonNull MoPubView moPubView) {
        bannerAdsLoaded = true;
    }

    @Override
    public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {

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

    /*private void loadAds(){
        mBannerAds.get().setAdUnitId("c122efbe224f46678800d2f73389d258");
        mBannerAds.get().loadAd();
    }

    private boolean isAdvertLoaded(){
        return bannerAdsLoaded;
    }

   /*External Triggers*/

    /*public Object onTrigger(List<Object> pData, pluginEnums.eAdManager pEventType) {
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
*/
