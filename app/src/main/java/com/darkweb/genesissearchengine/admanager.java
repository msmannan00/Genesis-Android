package com.darkweb.genesissearchengine;

import android.content.Context;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class admanager {
    private static final admanager ourInstance = new admanager();

    public static admanager getInstance() {
        return ourInstance;
    }
    private InterstitialAd mInterstitialAd;


    private admanager() {
    }

    public void initialize(Context applicationContext)
    {
        MobileAds.initialize(applicationContext, "ca-app-pub-5074525529134731~2926711128 ");
        mInterstitialAd = new InterstitialAd(applicationContext);
        mInterstitialAd.setAdUnitId("ca-app-pub-5074525529134731/8478420705");
        implementListeners();
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void implementListeners()
    {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public void showAd()
    {
        mInterstitialAd.show();
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

}
