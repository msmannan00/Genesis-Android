//package com.hiddenservices.onionservices.pluginManager.adPluginManager;
//
//import static com.hiddenservices.onionservices.constants.keys.APPLOVIN_BANNER_KEY;
//import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_CLICKED;
//import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import androidx.appcompat.app.AppCompatActivity;
//import com.applovin.mediation.MaxAd;
//import com.applovin.mediation.MaxAdViewAdListener;
//import com.applovin.mediation.MaxError;
//import com.applovin.mediation.ads.MaxAdView;
//import com.hiddenservices.onionservices.eventObserver;
//import com.hiddenservices.onionservices.helperManager.helperMethod;
//
//
//public class applovinBannerManager implements MaxAdViewAdListener{
//
//    /* Private Variabes */
//
//    private static final String S_UNIT_ID = APPLOVIN_BANNER_KEY;
//    private MaxAdView mBanner;
//    private eventObserver.eventListener mEvent;
//
//    /* Initializations */
//
//    public applovinBannerManager(AppCompatActivity pContext, eventObserver.eventListener pEvent){
//        mEvent = pEvent;
//        mBanner = new MaxAdView( S_UNIT_ID,pContext);
//        mBanner.setListener(this);
//
//        int width = ViewGroup.LayoutParams.MATCH_PARENT;
//        int heightPx = helperMethod.pxFromDp(50);
//
//        mBanner.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );
//        mBanner.setVisibility(View.GONE);
//        mBanner.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        mBanner.setExtraParameter( "disable_precache", "true" );
//
//        ViewGroup rootView = pContext.findViewById(android.R.id.content);
//        rootView.addView(mBanner);
//
//        mBanner.setExtraParameter( "allow_pause_auto_refresh_immediately", "true" );
//        mBanner.loadAd();
//    }
//
//    /* Helper Methods */
//
//    public void onShowToggle(boolean pStatus) {
//        if(mBanner!=null){
//            if(pStatus){
//                mBanner.startAutoRefresh();
//                mBanner.clearAnimation();
//                mBanner.setAlpha(0);
//                mBanner.setVisibility(View.VISIBLE);
//                mBanner.animate().setDuration(250).alpha(1).setStartDelay(500);
//            }else if (mBanner!=null){
//                mBanner.stopAutoRefresh();
//                mBanner.clearAnimation();
//                mBanner.setAlpha(1);
//                mBanner.setVisibility(View.VISIBLE);
//                mBanner.animate().setDuration(250).alpha(0).setStartDelay(0).withEndAction(() -> {
//                    if(mBanner!=null){
//                        mBanner.setVisibility(View.GONE);
//                    }
//                });
//            }
//        }
//    }
//
//    public void onDestroy() {
//        if(mBanner !=null){
//            mBanner.destroy();
//            mBanner = null;
//        }
//    }
//
//    /* Local Listeners */
//
//    @Override
//    public void onAdExpanded(MaxAd ad) {
//    }
//
//    @Override
//    public void onAdCollapsed(MaxAd ad) {
//    }
//
//    @Override
//    public void onAdLoaded(MaxAd ad) {
//        mEvent.invokeObserver(null, M_ON_AD_LOAD);
//    }
//
//    @Override
//    public void onAdDisplayed(MaxAd ad) {
//    }
//
//    @Override
//    public void onAdHidden(MaxAd ad) {
//    }
//
//    @Override
//    public void onAdClicked(MaxAd ad) {
//        mEvent.invokeObserver(null, M_ON_AD_CLICKED);
//    }
//
//    @Override
//    public void onAdLoadFailed(String adUnitId, MaxError error) {
//    }
//
//    @Override
//    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
//    }
//
//}
