package com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome;

public class bookmarkEnums {
    /*History Manager*/
    public enum eBookmarkViewCommands {
        M_UPDATE_LIST_IF_EMPTY, M_UPDATE_LIST, M_REMOVE_FROM_LIST, M_CLEAR_LIST, M_VERIFY_SELECTION_MENU, M_CLOSE_MENU, M_HIDE_SEARCH, M_LONG_PRESS_MENU, ON_GENERATE_SWEEPABLE_BACKGROUND
    }

    public enum eBookmarkAdapterCommands {
        M_ON_LOADING, M_LOADING_CLEAR, M_CLEAR_LONG_SELECTED_URL, GET_SELECTED_URL, GET_LONG_SELECTED_URL, GET_LONG_SELECTED_STATUS, ON_CLOSE
    }

    public enum eBookmarkAdapterCallback {
        ON_URL_TRIGGER, ON_URL_TRIGGER_NEW_TAB, ON_FETCH_FAVICON, ON_URL_CLEAR, ON_URL_CLEAR_AT, IS_EMPTY, ON_VERIFY_SELECTED_URL_MENU, M_OPEN_BOOKMARK_SETTING
    }

    public enum eBookmarkViewAdapterCommands {
        M_OPEN_MENU, M_CLEAR_LONG_SELECTED_VIEW, M_SELECT_VIEW, M_CLEAR_HIGHLIGHT
    }

}