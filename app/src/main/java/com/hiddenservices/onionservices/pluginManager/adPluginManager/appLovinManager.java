//package com.hiddenservices.onionservices.pluginManager.adPluginManager;
//
//import android.view.View;
//import com.applovin.sdk.AppLovinSdk;
//import com.hiddenservices.onionservices.eventObserver;
//import com.hiddenservices.onionservices.pluginManager.pluginEnums;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class appLovinManager {
//
//    /*Private Variables */
//
//    private eventObserver.eventListener mEvent;
//    private applovinSupportManager mSupportManager;
//    private applovinBannerManager mBannerManager;
//    private  AppCompatActivity mContext;
//
//    private boolean mLowMemoryReached = false;
//
//    /*Initializations*/
//
//    public appLovinManager(eventObserver.eventListener pEvent, View pBannerAds, AppCompatActivity pContext) {
//        this.mEvent = pEvent;
//        this.mContext = pContext;
//    }
//
//    private void onInitializeAdvertisement() {
//        if(!mLowMemoryReached){
//            AppLovinSdk.getInstance(mContext).getSettings().setVerboseLogging(true);
//            AppLovinSdk.getInstance(mContext).setMediationProvider("max");
//            AppLovinSdk.initializeSdk(mContext, configuration -> {
//                if(!mLowMemoryReached){
//                    this.mSupportManager = new applovinSupportManager(mContext);
//                    this.mBannerManager = new applovinBannerManager(mContext, mEvent);
//                }
//            });
//        }
//    }
//
//    /*Local Helper Methods*/
//
//    private void onShowInterstitial() {
//        if(AppLovinSdk.getInstance(mContext).isInitialized() && this.mSupportManager!=null){
//            this.mSupportManager.onShow();
//        }
//    }
//
//    private void onToggleBannerShow(boolean pStatus) {
//        if(AppLovinSdk.getInstance(mContext).isInitialized() && this.mBannerManager!=null){
//            this.mBannerManager.onShowToggle(pStatus);
//        }
//    }
//
//    /*Helper Methods*/
//
//    private void onDestroy() {
//        mLowMemoryReached = true;
//        if(mBannerManager!=null){
//            mBannerManager.onDestroy();
//        }
//    }
//
//    /*External Triggers*/
//
//    public Object onTrigger(pluginEnums.eAdManager pEventType) {
//        if (pEventType.equals(pluginEnums.eAdManager.M_LOW_MEMORY_DESTROY)) {
//            onDestroy();
//        } else if (pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED)) {
//            return true;
//        } else if (pEventType.equals(pluginEnums.eAdManager.M_SHOW_BANNER)) {
//            onToggleBannerShow(true);
//        } else if (pEventType.equals(pluginEnums.eAdManager.M_HIDE_BANNER)) {
//            onToggleBannerShow(false);
//        } else if (pEventType.equals(pluginEnums.eAdManager.M_SHOW_INTERSTITIAL)) {
//            onShowInterstitial();
//        } else if (pEventType.equals(pluginEnums.eAdManager.M_INIT_ADS)) {
//            onInitializeAdvertisement();
//        }
//        return null;
//    }
//
//}
//
