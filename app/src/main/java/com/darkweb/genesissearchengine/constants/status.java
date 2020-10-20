package com.darkweb.genesissearchengine.constants;

import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;

import java.util.Arrays;

import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class status
{
    /*App Status*/

    public static String sAppCurrentABI = "7.0";
    public static boolean sPaidStatus = false;

    /*Settings Status*/

    public static String sSettingSearchStatus = constants.CONST_BACKEND_GENESIS_URL;
    public static String sSettingRedirectStatus = strings.GENERIC_EMPTY_STR;
    public static boolean sSettingJavaStatus = true;
    public static boolean sSettingHistoryStatus = true;
    public static boolean sSettingIsAppPaused = false;
    public static boolean sSettingIsWelcomeEnabled = true;
    public static boolean sSettingIsAppStarted = false;
    public static boolean sSettingIsAppRated = false;
    public static boolean sSettingFontAdjustable = true;
    public static boolean sSettingFirstStart = true;
    public static boolean sSettingDesktopSite = false;
    public static int sSettingCookieStatus = ACCEPT_FIRST_PARTY;
    public static float sSettingFontSize = 1;
    public static String sSettingLanguage = "en";

    /*Bridge Status*/

    public static boolean sBridgeGatewayAuto = false;
    public static boolean sBridgeGatewayManual = false;
    public static boolean sBridgeVPNStatus = false;
    public static boolean sBridgeStatus = false;
    public static String sBridgeCustomBridge = strings.BRIDGE_CUSTOM_BRIDGE_OBFS4;


    public static void initStatus()
    {
        status.sSettingJavaStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT,true));
        status.sSettingHistoryStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR,true));
        status.sBridgeGatewayAuto = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_AUTO,true));
        status.sBridgeGatewayManual = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,false));
        status.sSettingIsWelcomeEnabled = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,true));
        status.sSettingIsAppRated = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,false));
        status.sBridgeVPNStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED,false));
        status.sBridgeStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.BRIDGE_BRIDGE_ENABLES,true));
        status.sSettingFontAdjustable = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,true));
        status.sSettingFirstStart = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FIRST_INSTALLED,true));

        status.sSettingCookieStatus = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY));
        status.sSettingFontSize = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_FLOAT, Arrays.asList(keys.SETTING_FONT_SIZE,100));

        status.sSettingLanguage = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_LANGUAGE,strings.SETTING_DEFAULT_LANGUAGE));
        status.sSettingSearchStatus = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE,constants.CONST_BACKEND_GENESIS_URL));
        status.sBridgeCustomBridge = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
    }

}
