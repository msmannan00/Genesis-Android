package com.darkweb.genesissearchengine.constants;

public class enums
{
    /*Settings Manager*/
    public enum etype{
        on_update_favicon,ON_UPDATE_TAB_TITLE, ON_LOAD_REQUEST,GECKO_SCROLL_CHANGED,ON_UPDATE_SEARCH_BAR,M_ON_MAIL,
        on_verify_selected_url_menu,FINDER_RESULT_CALLBACK,M_ADMOB_BANNER_RECHECK,
        welcome, reload,download_folder,M_ON_BANNER_UPDATE,M_INIT_TAB_COUNT,
        url_triggered, url_triggered_new_tab,url_clear,fetch_favicon, fetch_thumbnail,url_clear_at,remove_from_database,is_empty,M_HOME_PAGE,M_PRELOAD_URL,ON_KEYBOARD_CLOSE,
        on_close_sesson,on_long_press, on_full_screen,on_handle_external_intent,on_update_suggestion_url,progress_update,progress_update_forced, ON_EXPAND_TOP_BAR,recheck_orbot,on_url_load,on_playstore_load,back_list_empty,start_proxy, ON_UPDATE_THEME, M_INITIALIZE_TAB_SINGLE, M_INITIALIZE_TAB_LINK,on_request_completed, on_update_history,on_update_suggestion,M_WELCOME_MESSAGE,ON_UPDATE_TITLE_BAR,ON_FIRST_PAINT, ON_LOAD_TAB_ON_RESUME, ON_SESSION_REINIT,on_page_loaded,on_load_error,download_file_popup,on_init_ads,search_update, open_new_tab
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
}