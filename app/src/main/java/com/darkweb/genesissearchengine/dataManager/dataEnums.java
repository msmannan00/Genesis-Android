package com.darkweb.genesissearchengine.dataManager;

public class dataEnums
{
    /*Settings Manager*/
    public enum eHistoryCommands {
        M_GET_HISTORY, M_ADD_HISTORY, M_REMOVE_HISTORY, M_CLEAR_HISTORY, M_LOAD_MORE_HISTORY, M_INITIALIZE_HISTORY, M_HISTORY_SIZE
    }

    public enum eBookmarkCommands {
        M_ADD_BOOKMARK, M_GET_BOOKMARK, M_DELETE_BOOKMARK, M_CLEAR_BOOKMARK;
    }

    public enum ePreferencesCommands{
        M_SET_STRING, M_SET_BOOL, M_SET_INT, M_SET_FLOAT, M_GET_STRING, M_GET_BOOL, M_GET_INT, M_GET_FLOAT, M_CLEAR_PREFS
    }

    public enum eImageCacheCommands{
        M_SET_IMAGE, M_GET_IMAGE, M_CLEAR_IMAGE, M_CLEAR_OLD_IMAGES
    }

    public enum eSuggestionCommands{
        M_UPDATE_SUGGESTION, M_CLEAR_SUGGESTION, M_INIT_SUGGESTION, M_ADD_SUGGESTION, M_GET_SUGGESTION
    }

    public enum eTabCommands{
        M_ADD_TAB, M_CLEAR_TAB, M_CLOSE_TAB_PARAMETERIZED, CLOSE_TAB, GET_TAB, GET_CURRENT_TAB, GET_TOTAL_TAB, MOVE_TAB_TO_TOP
    }
}