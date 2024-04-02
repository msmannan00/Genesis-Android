package com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkSettings;

public class bookmarkSettingEnums {
    public enum eBookmarkSettingViewCommands {
        M_INITIALIZE, M_GET_BOOKMARK_NAME, M_GET_BOOKMARK_URL, M_BOOKMARK_NAME_VALIDATION_RESPONSE, M_CLEAR_FORM, M_CLEAR_FORM_FOCUS
    }

    public enum eBookmarkSettingModelCommands {
        M_UPDATE_BOOKMARK, M_VALIDATE_FORM, M_SET_BOOKMARK_CHANGED_STATUS, M_GET_UPDATE_STATUS, M_DELETE_BOOKMARK
    }

    public enum eActivityResponseCommands {
        M_OPEN_UPDATE_ALERT, M_OPEN_DELETE_ALERT
    }
}