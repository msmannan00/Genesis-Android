package com.darkweb.genesissearchengine;

import android.content.Context;
import android.content.SharedPreferences;
import com.crashlytics.android.Crashlytics;

import java.util.UUID;

public class analyticmanager {
    private static final analyticmanager ourInstance = new analyticmanager();

    public static analyticmanager getInstance() {
        return ourInstance;
    }
    String uniqueID = null;

    private analyticmanager()
    {
    }

    public void logUser()
    {
        Crashlytics.setUserIdentifier(uniqueID);
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName(uniqueID);
    }

    public void setDeviceID(Context context)
    {
        final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

        if (uniqueID == null)
        {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
    }


}
