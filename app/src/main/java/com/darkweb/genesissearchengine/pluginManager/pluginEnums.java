package com.darkweb.genesissearchengine.pluginManager;

public class pluginEnums
{
    /*Advert Manager*/
    public enum eAdManager {
        M_INITIALIZE_BANNER_ADS, M_IS_ADVERT_LOADED
    }
    public enum eAdManagerCallbacks {
        M_SHOW_LOADED_ADS
    }


    /*Analytics Manager*/
    public enum eAnalyticManager {
        M_LOG_EVENT
    }


    /*Lanuage Manager*/
    public enum eLangManager{
        M_SET_LANGUAGE, M_ACTIVITY_CREATED, M_RESUME, M_SUPPORTED_SYSTEM_LANGUAGE_INFO
    }

    public enum eLangManagerCallbacks{

    }

    /*Message Manager*/
    public enum eMessageManager{
        M_RESET, M_DATA_CLEARED, M_SECURE_CONNECTION, M_UPDATE_BRIDGES, M_NOT_SUPPORTED, M_BRIDGE_MAIL, M_LONG_PRESS_WITH_LINK, M_LONG_PRESS_URL, M_LONG_PRESS_DOWNLOAD, M_START_ORBOT, M_DOWNLOAD_FILE, M_RATE_APP, M_REPORT_URL, M_CLEAR_BOOKMARK, M_CLEAR_HISTORY, M_BOOKMARK, M_RATE_SUCCESS, M_RATE_FAILURE, M_LANGUAGE_SUPPORT_FAILURE, M_WELCOME
    }
    public enum eMessageManagerCallbacks{
        M_CANCEL_WELCOME, M_APP_RATED, M_DOWNLOAD_FILE_MANUAL, M_OPEN_LINK_CURRENT_TAB, M_COPY_LINK, M_REQUEST_BRIDGES, M_SET_BRIDGES, M_OPEN_LINK_NEW_TAB, M_CLEAR_TAB, M_RATE_APPLICATION
    }

    /*Notification Manager*/
    public enum eNotificationManager{
        M_CREATE_NOTIFICATION, M_CLEAR_NOTIFICATION
    }

    /*Orbot Manager*/
    public enum eOrbotManager{
        M_GET_NOTIFICATION_STATUS, M_ENABLE_NOTIFICATION, M_DISABLE_NOTIFICATION, M_DISABLE_NOTIFICATION_NO_BANDWIDTH, M_GET_LOGS, M_UPDATE_PRIVACY,M_START_ORBOT,M_IS_ORBOT_RUNNING, M_GET_ORBOT_STATUS, M_UPDATE_BRIDGES, M_UPDATE_VPN, M_SET_PROXY, M_SHOW_NOTIFICATION_STATUS, M_ORBOT_RUNNING
    }
}