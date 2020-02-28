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
    public static String sRedirectStatus = strings.EMPTY_STR;
    public static boolean sJavaStatus = true;
    public static boolean sHistoryStatus = true;
    public static boolean sIsAppPaused = false;
    public static boolean sIsWelcomeEnabled = true;
    public static boolean sIsAppStarted = false;
    public static boolean sIsAppRated = false;
    public static boolean sFontAdjustable = true;
    public static boolean sFirstStart = true;
    public static boolean sDesktopSite = false;
    public static int sCookieStatus = ACCEPT_FIRST_PARTY;
    public static float sFontSize = 1;
    public static String sLanguage = "ru";

    /*Bridge Status*/

    public static boolean sGatewayAuto = false;
    public static boolean sGatewayManual = false;
    public static String sCustomBridge = strings.CUSTOM_BRIDGE;


    public static void initStatus()
    {
        status.sJavaStatus = dataController.getInstance().getBool(keys.JAVA_SCRIPT,true);
        status.sHistoryStatus = dataController.getInstance().getBool(keys.HISTORY_CLEAR,true);
        status.sSearchStatus = dataController.getInstance().getString(keys.SEARCH_ENGINE,constants.BACKEND_GENESIS_URL);
        status.sGatewayAuto = dataController.getInstance().getBool(keys.GATEWAY_AUTO,false);
        status.sGatewayManual = dataController.getInstance().getBool(keys.GATEWAY_MANUAL,false);
        status.sIsWelcomeEnabled = dataController.getInstance().getBool(keys.IS_WELCOME_ENABLED,true);
        status.sIsAppRated = dataController.getInstance().getBool(keys.IS_APP_RATED,false);
        status.sFontSize = dataController.getInstance().getFloat(keys.FONT_SIZE,100);
        status.sFontAdjustable = dataController.getInstance().getBool(keys.FONT_ADJUSTABLE,true);
        status.sCookieStatus = dataController.getInstance().getInt(keys.COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY);
        status.sCustomBridge = dataController.getInstance().getString(keys.CUSTOM_BRIDGE_1,strings.CUSTOM_BRIDGE);
        status.sFirstStart = dataController.getInstance().getBool(keys.FIRST_INSTALLED,true);
        status.sLanguage = dataController.getInstance().getString(keys.LANGUAGE,strings.DEFAULT_LANGUAGE);
    }

}
