package com.darkweb.genesissearchengine.pluginManager;

import com.darkweb.genesissearchengine.appManager.app_model;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class admanager
{

    /*Private Variables*/
    private static final admanager ourInstance = new admanager();
    private InterstitialAd mInterstitialAd;
    private int adCount = 0;

    /*Initializations*/

    public static admanager getInstance() {
        return ourInstance;
    }

    private admanager() {
    }

    public void initialize()
    {
        /*
        MobileAds.initialize(app_model.getInstance().getAppContext(), "ca-app-pub-5074525529134731~2926711128");
        mInterstitialAd = new InterstitialAd(app_model.getInstance().getAppContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-5074525529134731/8478420705");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());*/
    }

    /*Helper Methods*/

    public void showAd(boolean isAdForced)
    {
        /*
        if(!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded())
        {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            if(isAdForced || adCount==0 || adCount%5==0)
            {
                adCount = 0;
            }
            else
            {
                adCount+=1;
            }
        }
        else
        {
            if(mInterstitialAd.isLoaded())
            {
                if(isAdForced)
                {
                    mInterstitialAd.show();
                    adCount = 1;
                }
                else
                {
                    if(adCount%3==0)
                    {
                        mInterstitialAd.show();
                    }
                    adCount += 1;
                }
            }
        }*/
    }
}
