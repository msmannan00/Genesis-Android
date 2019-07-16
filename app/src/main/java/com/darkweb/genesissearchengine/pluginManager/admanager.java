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
    private InterstitialAd mInterstitialHidden;
    private InterstitialAd mInterstitialInternal;
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
        initAd(mInterstitialHidden,"ca-app-pub-5074525529134731/4332539288");
        initAd(mInterstitialInternal,"ca-app-pub-5074525529134731/8478420705");
    }

    public void initAd(InterstitialAd adInstance,String id)
    {
        adInstance = new InterstitialAd(app_model.getInstance().getAppInstance());

        adInstance.setAdUnitId(id);
        adInstance.loadAd(new AdRequest.Builder().build());
    }

    /*Helper Methods*/

    public void showAd(enums.adID id)
    {
        if(id.equals(enums.adID.hidden))
        {
            mInterstitialHidden.show();
            mInterstitialHidden.loadAd(new AdRequest.Builder().build());
        }
        else
        {
            mInterstitialInternal.show();
            mInterstitialInternal.loadAd(new AdRequest.Builder().build());
        }
    }
}
