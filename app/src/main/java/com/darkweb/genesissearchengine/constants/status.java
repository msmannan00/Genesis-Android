package com.darkweb.genesissearchengine.constants;

import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;

import org.mozilla.geckoview.ContentBlocking;

import java.util.Arrays;

import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

public class status
{
    /*App Status*/

    public static boolean sPaidStatus = false;
    public static String mCurrentReloadURL = "";
    public static int mNotificationID = 1001;

    /*Settings Status*/

    public static String sSettingSearchStatus = constants.CONST_BACKEND_GENESIS_URL;
    public static String sSettingRedirectStatus = strings.GENERIC_EMPTY_STR;
    public static String sSettingLanguage = "en";
    public static String sSettingLanguageRegion = "Us";
    public static String mReferenceWebsites;

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
    public static boolean sSettingIsAppRedirected = false;
    public static boolean sSettingIsAppRestarting = false;
    public static boolean sSettingIsAppRated = false;
    public static boolean sSettingFontAdjustable = true;
    public static boolean sSettingFirstStart = true;
    public static int sSettingTrackingProtection = 0;
    public static boolean mThemeApplying = false;

    public static boolean sStatusDoNotTrack = true;
    public static boolean sRestoreTabs = false;
    public static boolean sCharacterEncoding = false;
    public static boolean sShowWebFonts = true;
    public static boolean sToolbarTheme = false;
    public static boolean sFullScreenBrowsing = false;
    public static boolean sOpenURLInNewTab = true;
    public static boolean sDefaultNightMode;
    public static boolean sLogListView;

    public static float sSettingFontSize = 1;

    public static int sTheme = enums.Theme.THEME_DEFAULT;
    public static int sSettingCookieStatus = ContentBlocking.AntiTracking.DEFAULT;
    public static int sShowImages = -1;
    public static int sWidgetResponse = enums.WidgetResponse.NONE;

    /*Bridge Status*/

    public static String sBridgeCustomBridge = strings.GENERIC_EMPTY_STR;
    public static boolean sBridgeGatewayAuto = false;
    public static boolean sBridgeGatewayManual = false;
    public static boolean sVPNStatus = false;
    public static boolean sBridgeStatus = false;
    public static int sBridgeNotificationManual = 0;


    public static void initStatus()
    {
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

        status.mReferenceWebsites = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.HOME_REFERENCE_WEBSITES,strings.HOME_REFERENCE_WEBSITES_DEFAULT));
        status.sSettingLanguageRegion = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION,strings.SETTING_DEFAULT_LANGUAGE_REGION));
        status.sSettingSearchStatus = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE,constants.CONST_BACKEND_GENESIS_URL));
        status.sBridgeCustomBridge = (String)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        status.sBridgeNotificationManual = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,0));

        status.sRestoreTabs = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB,false));
        status.sCharacterEncoding = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_CHARACTER_ENCODING,false));
        status.sShowImages = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,0));
        status.sShowWebFonts = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,true));
        status.sFullScreenBrowsing = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,true));
        status.sToolbarTheme = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_TOOLBAR_THEME,true));
        status.sTheme = (int)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_THEME,enums.Theme.THEME_DEFAULT));
        status.sOpenURLInNewTab = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,true));
        status.sLogListView = (boolean)dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW,true));
    }

}
