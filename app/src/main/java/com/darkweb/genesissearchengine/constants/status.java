package com.darkweb.genesissearchengine.constants;

import android.util.Log;
import com.darkweb.genesissearchengine.dataManager.preference_manager;

public class status
{
    /*App Level*/

    public static boolean isApplicationLoaded = false;
    public static boolean isPlayStoreInstalled = true;
    public static boolean isTorInitialized = false;
    public static String version_code = "7.0";

    /*Settings Level*/

    public static String search_status = strings.emptyStr;
    public static boolean java_status = false;
    public static boolean history_status = true;

    public static void initStatus()
    {
        status.java_status = preference_manager.getInstance().getBool(keys.java_script,true);
        status.history_status = preference_manager.getInstance().getBool(keys.history_clear,true);
        status.search_status = preference_manager.getInstance().getString(keys.search_engine,"Hidden Web");
    }

}
