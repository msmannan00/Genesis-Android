package com.darkweb.genesissearchengine.appManager.tabManager;

public class tabEnums
{
    /*Settings Manager*/
    public enum eTabViewCommands {
        M_DISMISS_MENU, M_SHOW_MENU, INIT_TAB_COUNT, ON_HIDE_SELECTION, ON_SHOW_SELECTION, ON_SHOW_UNDO_DIALOG, ON_HIDE_UNDO_DIALOG, ON_GENERATE_SWIPABLE_BACKGROUND
    }

    public enum eTabModelCommands {
        M_SELECTED_LIST_SIZE, M_REMOVE_ALL_SELECTION, M_CLEAR_ALL_SELECTION
    }

    public enum eTabAdapterCallback {
        ON_HIDE_SELECTION, ON_SHOW_SELECTION, ON_CLEAR_TAB_BACKUP, ON_REMOVE_TAB, ON_INIT_TAB_COUNT, ON_BACK_PRESSED, ON_LOAD_TAB, ON_REMOVE_TAB_VIEW
    }

}