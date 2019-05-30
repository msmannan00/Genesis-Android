package com.darkweb.genesissearchengine;

import android.content.Context;
import android.util.Log;
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
    int adCount = 0;

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

    public void showAd(boolean isAdForced)
    {

        Log.i("SHITSS","SHITSS 1" + " --- " + mInterstitialAd.isLoading() + " --- " + mInterstitialAd.isLoaded());
        if(!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded())
        {
            Log.i("SHITSS","SHITSS 2");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            if(isAdForced || adCount==0 || adCount%3==0)
            {
                Log.i("SHITSS","SHITSS 3");
                adCount = 0;
            }
            else
            {
                Log.i("SHITSS","SHITSS 4");
                adCount+=1;
            }
        }
        else
        {
            Log.i("SHITSS","SHITSS 5");
            if(mInterstitialAd.isLoaded())
            {
                Log.i("SHITSS","SHITSS 6");
                if(isAdForced)
                {
                    Log.i("SHITSS","SHITSS 7");
                    mInterstitialAd.show();
                    adCount = 1;
                }
                else
                {
                    Log.i("SHITSS","SHITSS 8");
                    if(adCount%3==0)
                    {
                        Log.i("SHITSS","SHITSS 9");
                        mInterstitialAd.show();
                    }
                    adCount += 1;
                }
            }
        }

    }

}
