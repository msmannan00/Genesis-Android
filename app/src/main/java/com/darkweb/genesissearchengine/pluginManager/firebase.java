package com.darkweb.genesissearchengine.pluginManager;

import android.os.Bundle;
import com.darkweb.genesissearchengine.appManager.app_model;
import com.google.firebase.analytics.FirebaseAnalytics;

public class firebase
{
    private static final firebase ourInstance = new firebase();

    public static firebase getInstance()
    {
        return ourInstance;
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    public void initialize()
    {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(app_model.getInstance().getAppContext());
    }

    public void logEvent(String value,String id)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, value);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Custom");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private firebase()
    {
    }
}
