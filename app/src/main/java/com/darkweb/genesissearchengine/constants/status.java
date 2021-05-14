package com.darkweb.genesissearchengine.constants;

import android.content.Context;

import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;

import org.mozilla.geckoview.ContentBlocking;

import java.util.Arrays;
import java.util.Locale;

import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class status
{
    /*App Status*/

    public static boolean sPaidStatus = false;
    public static boolean sDeveloperBuild = true;
    public static int mNotificationID = 1001;

    /*Settings Status*/
    public static Locale mSystemLocale = null;

    public static String sSettingSearchStatus = constants.CONST_BACKEND_GENESIS_URL;
    public static String sSettingRedirectStatus = strings.GENERIC_EMPTY_STR;
    public static String sSettingLanguage = "en";
    public static String sSettingLanguageRegion = "Us";
    public static String sReferenceWebsites;
    public static String sBridgeCustomBridge = strings.GENERIC_EMPTY_STR;
    public static String sBridgeCustomType = strings.GENERIC_EMPTY_STR;
    public static String sVersion = "";
    public static String sExternalWebsite = strings.GENERIC_EMPTY_STR;
    public static String sBridgesDefault = strings.BRIDGES_DEFAULT;

    public static boolean sUIInteracted = false;
    public static boolean sSettingEnableZoom = true;
    public static boolean sSettingEnableVoiceInput = true;
    public static boolean sSettingSearchHistory = false;
    public static boolean sSearchSuggestionStatus = false;
    public static boolean sSettingJavaStatus = true;
    public static boolean sSettingPopupStatus = false;
    public static boolean sClearOnExit = true;
    public static boolean sSettingIsAppPaused = false;
    public static boolean sSettingIsWelcomeEnabled = true;
    public static boolean sSettingIsAppStarted = false;
    public static boolean sSettingIsAppRunning = false;
    public static boolean sSettingIsAppRedirected = false;
    public static boolean sSettingIsAppRestarting = false;
    public static boolean sSettingIsAppRated = false;
    public static boolean sSettingFontAdjustable = true;
    public static boolean sSettingFirstStart = true;
    public static boolean mThemeApplying = false;
    public static boolean sTabGridLayoutEnabled = true;
    public static boolean sStatusDoNotTrack = true;
    public static boolean sRestoreTabs = false;
    public static boolean sCharacterEncoding = false;
    public static boolean sShowWebFonts = true;
    public static boolean sToolbarTheme = false;
    public static boolean sFullScreenBrowsing = false;
    public static boolean sOpenURLInNewTab = true;
    public static boolean sDefaultNightMode;
    public static boolean sLogThemeStyleAdvanced;
    public static boolean sBridgeGatewayAuto = false;
    public static boolean sBridgeGatewayManual = false;
    public static boolean sVPNStatus = false;
    public static boolean sVPNPermission = false;
    public static boolean sBridgeStatus = false;

    public static int sTheme = enums.Theme.THEME_DEFAULT;
    public static int sSettingCookieStatus = ContentBlocking.AntiTracking.DEFAULT;
    public static int sShowImages = -1;
    public static int sWidgetResponse = enums.WidgetResponse.NONE;
    public static int sBridgeNotificationManual = 0;
    public static int sSettingTrackingProtection = 0;
    public static int sRateCount = 0;

    public static float sSettingFontSize = 1;

    private static void versionVerifier(Context pContext){
        status.sVersion = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_VERSION,strings.GENERIC_EMPTY_STR));
        if(!status.sVersion.equals("1.0.0.1")){
            pContext.deleteDatabase(constants.CONST_DATABASE_NAME);
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_CLEAR_PREFS, null);
            status.sVersion = "1.0.0.1";
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_VERSION,strings.SETTING_DEFAULT_VERSION));
        }
    }

    public static void initStatus(Context pContext)
    {
        versionVerifier(pContext);

        status.sUIInteracted = false;
        status.sSettingSearchHistory = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY,true));
        status.sSearchSuggestionStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION,true));
        status.sSettingJavaStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT,true));
        status.sSettingPopupStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_POPUP,true));
        status.sClearOnExit = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR,true));
        status.sBridgeGatewayAuto = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_GATEWAY,true));
        status.sBridgeGatewayManual = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,false));
        status.sSettingIsWelcomeEnabled = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,true));
        status.sSettingIsAppRated = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,false));
        status.sVPNStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.VPN_ENABLED,false));
        status.sBridgeStatus = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES,false));
        status.sSettingFontAdjustable = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,true));
        status.sSettingFirstStart = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FIRST_INSTALLED,true));
        status.sSettingEnableZoom = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_ZOOM,true));
        status.sSettingEnableVoiceInput = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT,true));
        status.sSettingTrackingProtection = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_TRACKING_PROTECTION, ContentBlocking.AntiTracking.DEFAULT));
        status.sStatusDoNotTrack = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_DONOT_TRACK,true));
        status.sSettingCookieStatus = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY));
        status.sSettingFontSize = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_FLOAT, Arrays.asList(keys.SETTING_FONT_SIZE,100));
        status.sSettingLanguage = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_LANGUAGE,strings.SETTING_DEFAULT_LANGUAGE));
        status.sReferenceWebsites = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.HOME_REFERENCE_WEBSITES,strings.HOME_REFERENCE_WEBSITES_DEFAULT));
        status.sSettingLanguageRegion = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION,strings.SETTING_DEFAULT_LANGUAGE_REGION));
        status.sSettingSearchStatus = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE,constants.CONST_BACKEND_GENESIS_URL));
        status.sBridgeCustomBridge = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        status.sBridgeCustomType = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE,strings.GENERIC_EMPTY_SPACE));
        status.sBridgesDefault = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.BRIDGE_DEFAULT,strings.BRIDGES_DEFAULT));
        status.sBridgeNotificationManual = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,1));
        status.sRestoreTabs = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB,false));
        status.sCharacterEncoding = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_CHARACTER_ENCODING,false));
        status.sShowImages = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,0));
        status.sShowWebFonts = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,true));
        status.sFullScreenBrowsing = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,true));
        status.sToolbarTheme = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_TOOLBAR_THEME,true));
        status.sTheme = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_THEME,enums.Theme.THEME_DEFAULT));
        status.sOpenURLInNewTab = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,true));
        status.sLogThemeStyleAdvanced = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW,true));
        status.sTabGridLayoutEnabled = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SHOW_TAB_GRID,true));
        status.sRateCount = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_RATE_COUNT, 0));
    }

}
