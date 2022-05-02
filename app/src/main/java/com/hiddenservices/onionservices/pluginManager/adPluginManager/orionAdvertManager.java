package com.hiddenservices.onionservices.pluginManager.adPluginManager;

import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;
import static org.mozilla.gecko.util.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.applovin.mediation.ads.MaxAdView;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import org.torproject.android.service.wrapper.orbotLocalConstants;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class orionAdvertManager {
    /*Private Variables */

    private eventObserver.eventListener mEvent;
    private WeakReference<MaxAdView> mBannerAds;

    private boolean bannerAdsLoaded = false;
    private boolean bannerAdRequested = false;

    /*Initializations*/

    public orionAdvertManager(eventObserver.eventListener pEvent, MaxAdView pBannerAds, Context pContext) {
        this.mEvent = pEvent;
        this.mBannerAds = new WeakReference(pBannerAds);

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        sleep(10000);
                        initializeBannerAds(pContext);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


        initializeBannerAds(pContext);
    }

    private void initializeBannerAds(Context pContext) {
        try {
            if (orbotLocalConstants.mSOCKSPort != -1) {
                URL url = new URL("http://trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion/fetch_advert/");
                Proxy proxy;
                HttpURLConnection mHttpConnection;
                proxy = new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved("127.0.0.1", orbotLocalConstants.mSOCKSPort));
                mHttpConnection = (HttpURLConnection) url.openConnection(proxy);
                mHttpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:60.0) Gecko/20100101 Firefox/60.0");
                mHttpConnection.setRequestProperty("Accept", "*/*");
                mHttpConnection.connect();
                InputStream input = mHttpConnection.getInputStream();
                Bitmap mBitmap = BitmapFactory.decodeStream(input);
                BitmapDrawable mDrawable = new BitmapDrawable(pContext.getResources(), mBitmap);
                runOnUiThread(() -> {
                    this.mBannerAds.get().setAlpha(0);
                    this.mBannerAds.get().setVisibility(View.GONE);
                    this.mBannerAds.get().setBackground(mDrawable);
                    this.bannerAdsLoaded = true;
                    mEvent.invokeObserver(null, M_ON_AD_LOAD);
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*Local Helper Methods*/

    private void loadAds() {
        if (!bannerAdRequested) {
            bannerAdRequested = true;
        }
    }

    private boolean isAdvertLoaded() {
        return bannerAdsLoaded;
    }


    /*External Triggers*/

    public Object onTrigger(pluginEnums.eAdManager pEventType) {
        if (pEventType.equals(pluginEnums.eAdManager.M_INITIALIZE_BANNER_ADS)) {
            loadAds();
        } else if (pEventType.equals(pluginEnums.eAdManager.M_IS_ADVERT_LOADED)) {
            return isAdvertLoaded();
        }
        return null;
    }

}

