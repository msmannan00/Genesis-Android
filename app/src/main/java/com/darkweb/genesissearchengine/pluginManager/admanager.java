package com.darkweb.genesissearchengine.pluginManager;

import com.darkweb.genesissearchengine.appManager.home_activity.app_model;
import com.darkweb.genesissearchengine.constants.enums;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class admanager
{

    /*Private Variables*/
    private static final admanager ourInstance = new admanager();
    private InterstitialAd mInterstitialHidden_onion;
    private InterstitialAd mInterstitialInternal;
    private InterstitialAd mInterstitialHidden_base;
    private int adCount = 0;
    boolean isAdShown = false;

    /*Initializations*/

    public static admanager getInstance() {
        return ourInstance;
    }

    private admanager() {
    }

    public void initialize()
    {
        MobileAds.initialize(app_model.getInstance().getAppInstance(), "ca-app-pub-5074525529134731~2926711128");
        mInterstitialHidden_base = initAd("ca-app-pub-5074525529134731/1637043432");
        //mInterstitialHidden_onion = initAd("ca-app-pub-5074525529134731/4332539288");
        //mInterstitialInternal = initAd("ca-app-pub-5074525529134731/8478420705");
    }

    public InterstitialAd initAd(String id)
    {
        InterstitialAd adInstance = new InterstitialAd(app_model.getInstance().getAppInstance());
        adInstance.setAdUnitId(id);
        adInstance.loadAd(new AdRequest.Builder().build());

        return adInstance;
    }

    /*Helper Methods*/

    public void showAd(enums.adID id)
    {
        if(id.equals(enums.adID.hidden_onion_start))
        {
            mInterstitialHidden_base.show();
            mInterstitialHidden_base.loadAd(new AdRequest.Builder().build());
        }
        /*else if(id.equals(enums.adID.hidden_onion))
        {
            mInterstitialHidden_onion.show();
            mInterstitialHidden_onion.loadAd(new AdRequest.Builder().build());
        }
        else
        {
            mInterstitialInternal.show();
            mInterstitialInternal.loadAd(new AdRequest.Builder().build());
        }*/
    }
}
