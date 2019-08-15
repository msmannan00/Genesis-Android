package com.darkweb.genesissearchengine.pluginManager;

import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.constants.enums;
import com.example.myapplication.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
    private AdView bannerAds = null;

    /*Initializations*/

    public static admanager getInstance() {
        return ourInstance;
    }

    private admanager() {
    }

    public void initialize()
    {
        MobileAds.initialize(home_model.getInstance().getHomeInstance(), "ca-app-pub-5074525529134731~2926711128");
        mInterstitialHidden_base = initAd("ca-app-pub-5074525529134731/1637043432");
        // initBannerAds();
        // mInterstitialHidden_onion = initAd("ca-app-pub-5074525529134731/4332539288");
        // mInterstitialInternal = initAd("ca-app-pub-5074525529134731/8478420705");
    }

    public InterstitialAd initAd(String id)
    {
        InterstitialAd adInstance = new InterstitialAd(home_model.getInstance().getHomeInstance());
        adInstance.setAdUnitId(id);
        adInstance.loadAd(new AdRequest.Builder().addTestDevice("5AAFC2DFAE5C3906292EB576F0822FD7").build());

        return adInstance;
    }

    private void initBannerAds()
    {
        // bannerAds = home_model.getInstance().getHomeInstance().findViewById(R.id.adView);
        // AdRequest request = new AdRequest.Builder()
        //         .addTestDevice("5AAFC2DFAE5C3906292EB576F0822FD7")
        //         .build();
        // bannerAds.loadAd(request );
    }

    /*Helper Methods*/

    public void showAd(enums.adID id)
    {
        if(id.equals(enums.adID.hidden_onion_start))
        {
            mInterstitialHidden_base.show();
            mInterstitialHidden_base.loadAd(new AdRequest.Builder().addTestDevice("5AAFC2DFAE5C3906292EB576F0822FD7").build());
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
