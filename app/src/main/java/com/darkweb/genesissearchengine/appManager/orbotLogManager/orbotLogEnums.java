package com.darkweb.genesissearchengine.appManager.orbotLogManager;

public class orbotLogEnums
{
    /*Orbot Log View Manager*/

    public enum eOrbotLogViewCommands {
        M_UPDATE_LOGS, M_INIT_VIEWS, M_FLOAT_BUTTON_UPDATE, M_SCROLL_TOP, M_SCROLL_BOTTOM, M_SCROLL_TO_POSITION, M_SHOW_FLOATING_TOOLBAR
    }

    /*Orbot Log Model Manager*/

    public enum eOrbotLogModelCommands {
        M_GET_LIST, M_GET_LIST_SIZE
    }

    /*Orbot Log Model Callback*/

    public enum eOrbotLogModelCallbackCommands {
        M_UPDATE_FLOATING_BUTTON, M_UPDATE_LOGS, M_UPDATE_RECYCLE_VIEW
    }
}