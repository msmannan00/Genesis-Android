package com.darkweb.genesissearchengine.constants;

import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;

import java.util.Arrays;

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
    public static String sLanguage = "en";

    /*Bridge Status*/

    public static boolean sGatewayAuto = false;
    public static boolean sGatewayManual = false;
    public static boolean sVPNStatus = false;
    public static boolean sBridgeStatus = false;
    public static String sCustomBridge = strings.CUSTOM_BRIDGE_OBFS4;


    public static void initStatus()
    {
        status.sJavaStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.JAVA_SCRIPT,true));
        status.sHistoryStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.HISTORY_CLEAR,true));
        status.sGatewayAuto = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.GATEWAY_AUTO,true));
        status.sGatewayManual = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.GATEWAY_MANUAL,false));
        status.sIsWelcomeEnabled = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.IS_WELCOME_ENABLED,true));
        status.sIsAppRated = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.IS_APP_RATED,false));
        status.sVPNStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.S_VPN_ENABLED,false));
        status.sBridgeStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.S_BRIDGE_ENABLES,true));
        status.sFontAdjustable = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.FONT_ADJUSTABLE,true));
        status.sFirstStart = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.FIRST_INSTALLED,true));

        status.sCookieStatus = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY));
        status.sFontSize = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_FLOAT, Arrays.asList(keys.FONT_SIZE,100));

        status.sLanguage = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.LANGUAGE,strings.DEFAULT_LANGUAGE));
        status.sSearchStatus = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SEARCH_ENGINE,constants.BACKEND_GENESIS_URL));
        status.sCustomBridge = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.CUSTOM_BRIDGE_1,strings.CUSTOM_BRIDGE_OBFS4));
    }

}
