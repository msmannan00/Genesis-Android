package com.darkweb.genesissearchengine.appManager.tabManager;

public class tabEnums
{
    /*Settings Manager*/
    public enum eTabViewCommands {
        M_DISMISS_MENU, M_SHOW_MENU, INIT_TAB_COUNT, ON_HIDE_SELECTION, ON_SHOW_SELECTION, ON_SHOW_SELECTION_MENU, ON_SHOW_UNDO_DIALOG, ON_HIDE_UNDO_DIALOG, ON_INIT_UI, ON_GENERATE_SWIPABLE_BACKGROUND, ON_EXIT, ON_HIDE_UNDO_DIALOG_INIT, ON_HOLD_BLOCKER, ON_RELEASE_BLOCKER, ON_HIDE_UNDO_DIALOG_FORCED
    }

    public enum eTabAdapterCommands {
        M_SELECTION_MENU_SHOWING, M_REMOVE_ALL_SELECTION, M_CLEAR_ALL_SELECTION, ENABLE_LONG_CLICK_MENU, INIT_FIRST_ROW, REINIT_DATA, NOTIFY_SWIPE, GET_SELECTION_SIZE, REMOVE_ALL, REMOVE_ROW_CROSSED, M_INITIALIZE
    }

    public enum eTabAdapterCallback {
        ON_HIDE_SELECTION, ON_SHOW_SELECTION, ON_CLEAR_TAB_BACKUP, ON_REMOVE_TAB, ON_INIT_TAB_COUNT, ON_BACK_PRESSED, ON_LOAD_TAB, ON_REMOVE_TAB_VIEW,ON_REMOVE_TAB_VIEW_RETAIN_BACKUP, ON_SHOW_UNDO_POPUP, M_CLEAR_BACKUP, ON_SHOW_SELECTION_MENU
    }

    public enum eModelCallback {
        M_SET_LIST, M_GET_LIST, M_REMOVE_TAB, M_GET_BACKUP, M_CLEAR_BACKUP_WITHOUT_CLOSE, M_CLEAR_BACKUP_RETAIN_DATABASE, M_LOAD_BACKUP
    }

}