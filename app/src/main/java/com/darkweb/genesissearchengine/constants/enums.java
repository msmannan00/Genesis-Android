package com.darkweb.genesissearchengine.constants;

public class enums
{
    /*Settings Manager*/
    public enum etype{
        on_update_favicon,ON_UPDATE_TAB_TITLE, ON_LOAD_REQUEST,GECKO_SCROLL_CHANGED,ON_UPDATE_SEARCH_BAR,
        on_verify_selected_url_menu,FINDER_RESULT_CALLBACK,
        welcome, reload,download_folder,
        url_triggered, url_triggered_new_tab,url_clear,fetch_favicon,url_clear_at,remove_from_database,is_empty,M_HOME_PAGE,M_PRELOAD_URL,ON_KEYBOARD_CLOSE,
        on_close_sesson,on_long_press, on_full_screen,on_handle_external_intent,on_update_suggestion_url,progress_update,recheck_orbot,on_url_load,on_playstore_load,back_list_empty,start_proxy, ON_UPDATE_THEME,on_request_completed, on_update_history,on_update_suggestion,ON_UPDATE_TITLE_BAR,ON_FIRST_PAINT, ON_LOAD_TAB_ON_RESUME, ON_SESSION_REINIT,on_page_loaded,on_load_error,download_file_popup,on_init_ads,search_update, open_new_tab
    }

    /*General Enums*/

    public static class Theme {
        public static final int THEME_LIGHT = 0;
        public static final int THEME_DARK = 1;
        public static final int THEME_DEFAULT = 2;
    }

    public static class ImageQueueStatus {
        public static final int M_IMAGE_LOADING = 0;
        public static final int M_IMAGE_LOADED_SUCCESSFULLY = 1;
        public static final int M_IMAGE_LOADING_FAILED = 2;
    }

    public enum eMessageEnums {
        M_DATA_CLEARED, M_SECURE_CONNECTION, M_NOT_SUPPORTED, M_BRIDGE_MAIL, M_LONG_PRESS_WITH_LINK, M_LONG_PRESS_URL, M_LONG_PRESS_DOWNLOAD, M_START_ORBOT, M_DOWNLOAD_FILE, M_RATE_APP, M_REPORT_URL, M_CLEAR_BOOKMARK, M_CLEAR_HISTORY, M_BOOKMARK, M_RATE_SUCCESS, M_RATE_FAILURE, M_ABI_ERROR, M_WELCOME, M_CANCEL_WELCOME, M_APP_RATED, M_DOWNLOAD_FILE_MANUAL, M_OPEN_LINK_CURRENT_TAB, M_COPY_LINK, M_OPEN_LINK_NEW_TAB, M_CLEAR_TAB, M_CONNECT_VPN, M_IGNORE_ABI, M_RATE_APPLICATION
    }
}