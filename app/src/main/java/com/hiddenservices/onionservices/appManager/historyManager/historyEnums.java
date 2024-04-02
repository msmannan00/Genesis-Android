package com.hiddenservices.onionservices.appManager.historyManager;

public class historyEnums {
    /*History Manager*/
    public enum eHistoryViewCommands {
        M_UPDATE_LIST_IF_EMPTY, M_UPDATE_LIST, M_REMOVE_FROM_LIST, M_CLEAR_LIST, M_VERIFY_SELECTION_MENU, M_CLOSE_MENU, M_HIDE_SEARCH, M_LONG_PRESS_MENU, ON_GENERATE_SWAPPABLE_BACKGROUND
    }

    public enum eHistoryAdapterCommands {
        M_CLEAR_LONG_SELECTED_URL, GET_SELECTED_URL, GET_LONG_SELECTED_URL, GET_LONG_SELECTED_STATUS, ON_CLOSE
    }

    public enum eHistoryAdapterCallback {
        ON_URL_TRIGGER, ON_URL_TRIGGER_NEW_TAB, ON_FETCH_FAVICON, ON_URL_CLEAR, ON_URL_CLEAR_AT, IS_EMPTY, ON_VERIFY_SELECTED_URL_MENU, M_OPEN_BOOKMARK_SETTING
    }

    public enum eHistoryViewAdapterCommands {
        M_OPEN_MENU, M_CLEAR_LONG_SELECTED_VIEW, M_SELECT_VIEW, M_CLEAR_HIGHLIGHT
    }

}