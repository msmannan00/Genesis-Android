package com.darkweb.genesissearchengine.constants;

import android.content.res.Configuration;

import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;

import java.util.Arrays;

import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class status
{
    /*App Status*/

    public static boolean sPaidStatus = false;
    public static String sAppCurrentABI = "7.0";

    /*Settings Status*/

    public static String sSettingSearchStatus = constants.CONST_BACKEND_GENESIS_URL;
    public static String sSettingRedirectStatus = strings.GENERIC_EMPTY_STR;


    public static boolean sThemeChanged = false;
    public static boolean sSettingEnableZoom = true;
    public static boolean sSettingEnableVoiceInput = true;
    public static boolean sSettingSearchHistory = false;
    public static boolean getsSettingSearchSuggestion = false;
    public static boolean sSettingJavaStatus = true;
    public static boolean sClearOnExit = true;
    public static boolean sSettingIsAppPaused = false;
    public static boolean sSettingIsWelcomeEnabled = true;
    public static boolean sSettingIsAppStarted = false;
    public static boolean sSettingIsAppRated = false;
    public static boolean sSettingFontAdjustable = true;
    public static boolean sSettingFirstStart = true;
    public static boolean sSettingTrackingProtection = true;
    public static boolean sStatusDoNotTrack = true;
    public static boolean sRestoreTabs = false;
    public static boolean sCharacterEncoding = false;
    public static boolean sShowWebFonts = false;
    public static boolean sAutoPlay = false;
    public static boolean sFullScreenBrowsing = false;
    public static boolean sOpenURLInNewTab = false;
    public static int sTheme = enums.Theme.THEME_DEFAULT;
    public static int sSettingCookieStatus = ACCEPT_FIRST_PARTY;
    public static int sShowImages = -1;
    public static float sSettingFontSize = 1;
    public static String sSettingLanguage = "en";
    public static boolean sDefaultNightMode;

    /*Bridge Status*/

    public static boolean sBridgeGatewayAuto = false;
    public static boolean sBridgeGatewayManual = false;
    public static boolean sBridgeVPNStatus = false;
    public static boolean sBridgeStatus = false;
    public static int sBridgeNotificationManual = 0;
    public static String sBridgeCustomBridge = strings.BRIDGE_CUSTOM_BRIDGE_OBFS4;


    public static void initStatus()
    {
        status.sSettingSearchHistory = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY,true));
        status.getsSettingSearchSuggestion = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION,true));
        status.sSettingJavaStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT,true));
        status.sClearOnExit = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR,true));
        status.sBridgeGatewayAuto = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_AUTO,true));
        status.sBridgeGatewayManual = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,false));
        status.sSettingIsWelcomeEnabled = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,true));
        status.sSettingIsAppRated = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,false));
        status.sBridgeVPNStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED,false));
        status.sBridgeStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.BRIDGE_BRIDGE_ENABLES,true));
        status.sSettingFontAdjustable = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,true));
        status.sSettingFirstStart = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FIRST_INSTALLED,true));
        status.sSettingEnableZoom = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_ZOOM,true));
        status.sSettingEnableVoiceInput = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT,true));
        status.sSettingTrackingProtection = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_TRACKING_PROTECTION,true));
        status.sStatusDoNotTrack = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_DONOT_TRACK,true));
        status.sSettingCookieStatus = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY));
        status.sSettingFontSize = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_FLOAT, Arrays.asList(keys.SETTING_FONT_SIZE,100));

        status.sSettingLanguage = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_LANGUAGE,strings.SETTING_DEFAULT_LANGUAGE));
        status.sSettingSearchStatus = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE,constants.CONST_BACKEND_GENESIS_URL));
        status.sBridgeCustomBridge = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        status.sBridgeNotificationManual = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,0));

        status.sRestoreTabs = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB,false));
        status.sCharacterEncoding = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_CHARACTER_ENCODING,false));
        status.sShowImages = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,0));
        status.sShowWebFonts = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,false));
        status.sAutoPlay = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_AUTO_PLAY,false));
        status.sFullScreenBrowsing = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,false));
        status.sTheme = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_THEME,enums.Theme.THEME_DEFAULT));
        status.sOpenURLInNewTab = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,false));
    }

}
