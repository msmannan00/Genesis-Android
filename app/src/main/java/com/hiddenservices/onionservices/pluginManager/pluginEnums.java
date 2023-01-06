package com.hiddenservices.onionservices.pluginManager;

public class pluginEnums {
    /*Advert Manager*/
    public enum eAdManager {
        M_INITIALIZE_BANNER_ADS, M_IS_ADVERT_LOADED, M_DESTROY
    }

    public enum eAdManagerCallbacks {
        M_ON_AD_CLICK, M_ON_AD_LOAD, M_ON_AD_HIDE
    }

    /*Analytics Manager*/
    public enum eAnalyticManager {
        M_LOG_EVENT
    }

    /*Lanuage Manager*/
    public enum eLangManager {
        M_SET_LANGUAGE, M_ACTIVITY_CREATED, M_RESUME, M_SUPPORTED_SYSTEM_LANGUAGE_INFO, M_INIT_LOCALE, M_UPDATE_LOCAL
    }

    public enum eLangManagerCallbacks {

    }

    /*Message Manager*/
    public enum eMessageManager {
        M_RESET, M_DATA_CLEARED, M_COPY, M_APPLICATION_CRASH, M_DELETE_BOOKMARK, M_UPDATE_BOOKMARK, M_IMAGE_UPDATE, M_OPEN_ACTIVITY_FAILED, M_OPEN_CICADA, M_TOR_SWITCH, M_SECURE_CONNECTION, M_SECURITY_INFO, M_POPUP_BLOCKED, M_PANIC, M_MAX_TAB_REACHED, M_ORBOT_LOADING, M_GENESIS_SEARCH_DISABLED, M_LOAD_NEW_TAB, M_UNDO, M_DOWNLOAD_SINGLE, M_UPDATE_BRIDGES, M_LOW_MEMORY, M_NEW_IDENTITY, M_NOT_SUPPORTED, M_BRIDGE_MAIL, M_LONG_PRESS_WITH_LINK, M_LONG_PRESS_URL, M_LONG_PRESS_DOWNLOAD, M_START_ORBOT, M_DOWNLOAD_FAILURE, M_DOWNLOAD_FILE, M_RATE_APP, M_REPORT_URL, M_CLEAR_BOOKMARK, M_CLEAR_HISTORY, M_BOOKMARK, M_PANIC_RESET, M_DEFAULT_BROWSER, M_TOR_SWITCH_RESTART, M_RATE_SUCCESS, M_RATE_FAILURE, M_CLOSE, M_LANGUAGE_SUPPORT_FAILURE, M_WELCOME
    }

    public enum eMessageManagerCallbacks {
        M_CANCEL_WELCOME, M_APP_RATED, M_DOWNLOAD_FILE_MANUAL, M_OPEN_LINK_CURRENT_TAB, M_COPY_LINK, M_REQUEST_BRIDGES, M_SET_BRIDGES, M_OPEN_LINK_NEW_TAB, M_CLEAR_TAB, M_RATE_APPLICATION, M_OPEN_PRIVACY, M_CLEAR_HISTORY, M_CLEAR_BOOKMARK, M_ADJUST_INPUT_RESIZE, M_UNDO_SESSION, M_OPEN_LOGS, M_LOAD_NEW_TAB, M_UNDO_TAB, M_CUSTOM_BRIDGE, M_BRIDGE_TYPE, M_DATA_CLEARED_EXTERNAL, M_IMAGE_UPDATE_RESTART
    }

    /*Download Manager*/
    public enum eDownloadManager {
        M_START_DOWNLOAD, M_CANCEL_DOWNLOAD, M_URL_DOWNLOAD_REQUEST, M_WEB_DOWNLOAD_REQUEST, M_BLOB_DOWNLOAD_REQUEST, M_SWIPE
    }

    /*Notification Manager*/
    public enum eNotificationManager {
        M_CREATE_NOTIFICATION, M_CLEAR_NOTIFICATION
    }

    /*Orbot Manager*/
    public enum eOrbotManager {
        M_GET_NOTIFICATION_STATUS, M_NEW_CIRCUIT, M_DESTROY, M_RESTART_PROXY, M_ENABLE_NOTIFICATION, M_DISABLE_NOTIFICATION, M_DISABLE_NOTIFICATION_NO_BANDWIDTH, M_GET_LOGS, M_UPDATE_PRIVACY, M_START_ORBOT, M_IS_ORBOT_RUNNING, M_GET_ORBOT_STATUS, M_UPDATE_BRIDGES, M_UPDATE_BRIDGE_LIST, M_UPDATE_VPN, M_SET_PROXY, M_SHOW_NOTIFICATION_STATUS, M_ORBOT_RUNNING
    }
}