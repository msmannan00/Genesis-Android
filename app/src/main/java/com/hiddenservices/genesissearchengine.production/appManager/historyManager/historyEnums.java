package com.hiddenservices.genesissearchengine.production.appManager.historyManager;

public class historyEnums
{
    /*History Manager*/
    public enum eHistoryViewCommands {
        M_UPDATE_LIST_IF_EMPTY, M_UPDATE_LIST, M_REMOVE_FROM_LIST, M_CLEAR_LIST,M_VERTIFY_SELECTION_MENU, M_CLOSE_MENU, M_HIDE_SEARCH, M_LONG_PRESS_MENU, ON_GENERATE_SWIPABLE_BACKGROUND
    }

    public enum eHistoryAdapterCommands {
        M_CLEAR_LONG_SELECTED_URL, GET_SELECTED_URL, GET_LONG_SELECTED_URL, GET_LONG_SELECTED_STATUS, ON_CLOSE
    }

    public enum eHistoryViewAdapterCommands {
        M_OPEN_MENU, M_CLEAR_LONG_SELECTED_VIEW, M_SELECT_VIEW, M_CLEAR_HIGHLIGHT
    }

}