package com.hiddenservices.onionservices.appManager.helpManager;

class helpEnums
{
    /*History Manager*/
    public enum eHelpModel {
        M_LOAD_HELP_DATA, M_IS_LOADED
    }

    public enum eHelpModelCallback {
        M_LOAD_JSON_RESPONSE_SUCCESS, M_LOAD_JSON_RESPONSE_FAILURE
    }

    public enum eHelpViewController {
        M_INIT_VIEWS, M_DATA_LOADED, M_LOAD_ERROR, M_RELOAD_DATA
    }

    public enum eHelpAdapter {
        M_INIT_FILTER
    }

    public enum eHelpViewCallback {
    }
}