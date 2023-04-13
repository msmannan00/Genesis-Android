//package com.hiddenservices.onionservices.pluginManager.adPluginManager;
//
//import static com.hiddenservices.onionservices.constants.keys.APPLOVIN_SUPPORT_KEY;
//
//import android.os.Handler;
//import androidx.appcompat.app.AppCompatActivity;
//import com.applovin.mediation.MaxAd;
//import com.applovin.mediation.MaxAdViewAdListener;
//import com.applovin.mediation.MaxError;
//import com.applovin.mediation.ads.MaxInterstitialAd;
//import java.util.concurrent.TimeUnit;
//
//public class applovinSupportManager implements MaxAdViewAdListener {
//
//    /* Private Variabes */
//
//    private static final String S_UNIT_ID = APPLOVIN_SUPPORT_KEY;
//    private MaxInterstitialAd mInterstitialAd;
//    private int mRetryAttemt;
//
//
//    /* Initializations */
//
//    public applovinSupportManager(AppCompatActivity pContext){
//        mInterstitialAd = new MaxInterstitialAd( S_UNIT_ID, pContext);
//        mInterstitialAd.setListener(this);
//        mInterstitialAd.loadAd();
//    }
//
//    /* Helper Methods */
//
//    public void onShow() {
//        if(mInterstitialAd!=null){
//            if(mInterstitialAd.isReady()){
//                mInterstitialAd.showAd();
//            }
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
//    public void onAdLoaded(final MaxAd maxAd)
//    {
//        mRetryAttemt = 0;
//    }
//
//    @Override
//    public void onAdLoadFailed(final String adUnitId, final MaxError error)
//    {
//        mRetryAttemt++;
//        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, mRetryAttemt) ) );
//        new Handler().postDelayed(() -> mInterstitialAd.loadAd(), delayMillis );
//    }
//
//    @Override
//    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
//    {
//        mInterstitialAd.loadAd();
//    }
//
//    @Override
//    public void onAdDisplayed(final MaxAd maxAd) {}
//
//    @Override
//    public void onAdClicked(final MaxAd maxAd) {}
//
//    @Override
//    public void onAdHidden(final MaxAd maxAd)
//    {
//        mInterstitialAd.loadAd();
//    }
//}
