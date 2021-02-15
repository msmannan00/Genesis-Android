package com.darkweb.genesissearchengine.helperManager;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.myapplication.R;

/**
 * Created by BrainWang on 05/01/2016.
 */
public class adBlocker {
    static String[] adUrls = null;
    public static boolean isAd(Context context, String url) {
        Resources res = context.getResources();
        if(adUrls==null)
        {
            adUrls = res.getStringArray(R.array.adBlockUrl);
        }

        for (String adUrl : adUrls) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }
}