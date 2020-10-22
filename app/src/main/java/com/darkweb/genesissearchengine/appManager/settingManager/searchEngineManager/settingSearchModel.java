package com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

import java.util.Arrays;


class settingSearchModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingSearchModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }


    /*Helper Methods*/

    public void onSetSearchEngine(String pUrl){
        status.sSettingSearchStatus = pUrl;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, pUrl));
    }

    public void setSearchHistory(boolean pStatus){
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY, pStatus));
        status.sSettingSearchHistory = pStatus;
    }
    public void setSearchStatus(boolean pStatus){
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION, pStatus));
        status.getsSettingSearchSuggestion = pStatus;
    }

}
