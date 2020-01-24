package com.darkweb.genesissearchengine.constants;

import com.darkweb.genesissearchengine.dataManager.dataController;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class status
{
    /*App Status*/

    public static String current_ABI = "7.0";
    public static boolean paid_status = false;

    /*Settings Status*/

    public static String sSearchStatus = constants.BACKEND_GENESIS_URL;
    public static boolean sJavaStatus = true;
    public static boolean sHistoryStatus = true;
    public static boolean sGateway = false;
    public static boolean sIsAppPaused = false;
    public static boolean sIsWelcomeEnabled = true;
    public static boolean sIsAppStarted = false;
    public static boolean sIsAppRated = false;
    public static boolean sFontAdjustable = true;
    public static int sCookieStatus = ACCEPT_FIRST_PARTY;
    public static float sFontSize = 1;

    public static void initStatus()
    {
        status.sJavaStatus = dataController.getInstance().getBool(keys.JAVA_SCRIPT,true);
        status.sHistoryStatus = dataController.getInstance().getBool(keys.HISTORY_CLEAR,true);
        status.sSearchStatus = dataController.getInstance().getString(keys.SEARCH_ENGINE,constants.BACKEND_GENESIS_URL);
        status.sGateway = dataController.getInstance().getBool(keys.GATEWAY,false);
        status.sIsWelcomeEnabled = dataController.getInstance().getBool(keys.IS_WELCOME_ENABLED,true);
        status.sIsAppRated = dataController.getInstance().getBool(keys.IS_APP_RATED,false);
        status.sFontSize = dataController.getInstance().getFloat(keys.FONT_SIZE,100);
        status.sFontAdjustable = dataController.getInstance().getBool(keys.FONT_ADJUSTABLE,true);
        status.sCookieStatus = dataController.getInstance().getInt(keys.COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY);
    }

}
