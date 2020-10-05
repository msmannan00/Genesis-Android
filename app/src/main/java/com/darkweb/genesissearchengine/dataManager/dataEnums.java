package com.darkweb.genesissearchengine.dataManager;

public class dataEnums
{
    /*Settings Manager*/
    public enum eHistoryCommands {
        M_GET_HISTORY, M_ADD_HISTORY, M_REMOVE_HISTORY, M_CLEAR_HISTORY, M_LOAD_MORE_HISTORY, M_INITIALIZE_HISTORY, M_HISTORY_SIZE
    }

    public enum ePreferencesCommands{
        M_SET_STRING, M_SET_BOOL, M_SET_INT, M_SET_FLOAT, M_GET_STRING, M_GET_BOOL, M_GET_INT, M_GET_FLOAT, M_CLEAR_PREFS
    }

    public enum eImageCacheCommands{
        M_SET_IMAGE, M_GET_IMAGE, M_CLEAR_IMAGE, M_CLEAR_OLD_IMAGES
    }
}