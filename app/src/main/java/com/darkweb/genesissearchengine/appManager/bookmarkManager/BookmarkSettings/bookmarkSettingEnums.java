package com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings;

public class bookmarkSettingEnums
{
    public enum eBookmarkSettingViewCommands {
        M_INITIALIZE, M_GET_BOOKMARK_NAME, M_GET_BOOKMARK_URL, M_BOOKMARK_NAME_VALIDATION_ERROR, M_BOOKMARK_URL_VALIDATION_ERROR, M_CLEAR_FORM
    }

    public enum eBookmarkSettingModelCommands {
        M_GET_BOOKMARK_ID, M_UPDATE_BOOKMARK, M_CLOSE, M_VALIDATE_FORM
    }

    public enum eBookmarkSettingViewAdapterCommands {
    }

    public enum eBookmarkSettingModelCallbackCommands {
        M_CLEAR_FORM, M_BOOKMARK_NAME_VALIDATION_ERROR, M_BOOKMARK_URL_VALIDATION_ERROR,M_CLOSE
    }

}