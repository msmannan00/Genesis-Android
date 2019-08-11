package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.SharedPreferences;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.UUID;

public class analyticmanager
{
    /*Private Variables*/

    private static final analyticmanager ourInstance = new analyticmanager();
    private String uniqueID = null;

    public static analyticmanager getInstance() {
        return ourInstance;
    }

    /*Initializations*/

    private analyticmanager()
    {
    }

    public void initialize(Context context)
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

    /*Helper Methods*/

    public void logUser()
    {
        Crashlytics.setUserIdentifier(uniqueID);
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName(uniqueID);
    }

    public void sendEvent(String value)
    {
        //firebase.getInstance().logEvent(value,uniqueID);
        //Answers.getInstance().logCustom(new CustomEvent(value));

    }

}
