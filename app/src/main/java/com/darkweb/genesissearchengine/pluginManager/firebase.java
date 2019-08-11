package com.darkweb.genesissearchengine.pluginManager;

//import com.google.firebase.analytics.FirebaseAnalytics;

public class firebase
{
    /*Private Variables*/

    private static final firebase ourInstance = new firebase();

    /*Initializations*/

    public static firebase getInstance()
    {
        return ourInstance;
    }

    //private FirebaseAnalytics mFirebaseAnalytics;

    public void initialize()
    {
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(home_model.getInstance().getAppContext());
    }

    /*Helper Methods*/

    public void logEvent(String value,String id)
    {
        /*
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, value);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Custom");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/
    }

    private firebase()
    {
    }
}
