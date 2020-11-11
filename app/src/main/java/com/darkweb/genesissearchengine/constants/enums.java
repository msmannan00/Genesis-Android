package com.darkweb.genesissearchengine.constants;

public class enums
{
    /*Settings Manager*/
    public enum etype{
        on_update_favicon,M_RESET_THEME_INVOKED_BACK,
        onion,on_verify_selected_url_menu,
        welcome,abi_error, rate_failure,reported_success,bookmark, clear_tab,clear_history,clear_bookmark,report_url,default_home,rate_app,version_warning,start_orbot,download_file,download_file_long_press,on_long_press_url,
        cancel_welcome,ignore_abi,reload,connect_vpn,app_rated,download_file_manual,download_folder, update_searcn, update_javascript,update_notification, update_history,update_cookies, update_font_size,update_font_adjustable,close_view,open_link_new_tab,open_link_current_tab,copy_link,
        url_triggered, url_triggered_new_tab,url_clear,clear_recycler,url_clear_at,remove_from_database,is_empty,
        on_close_sesson,on_long_press,on_long_press_with_link,on_reset_app,on_bridge_mail,on_not_support, data_cleared,on_full_screen,on_handle_external_intent,on_update_suggestion_url,progress_update,recheck_orbot,on_url_load,on_playstore_load,back_list_empty,start_proxy,on_request_completed, on_update_history,on_update_suggestion,on_page_loaded,on_load_error,download_file_popup,on_init_ads,rate_application,search_update, open_new_tab
    }

    public static class Theme {
        public static final int THEME_LIGHT = 0;
        public static final int THEME_DARK = 1;
        public static final int THEME_DEFAULT = 2;
    }
}