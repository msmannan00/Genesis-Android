package com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager;

import android.view.View;

import com.darkweb.genesissearchengine.appManager.historyManager.historyEnums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import java.util.Arrays;
import java.util.List;


class settingSearchModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingSearchModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }


    /*Helper Methods*/

    private void onSetSearchEngine(String pUrl){
        status.sSettingSearchStatus = pUrl;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, pUrl));
    }

    private void setSearchHistory(boolean pStatus){
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY, pStatus));
        status.sSettingSearchHistory = pStatus;
    }
    private void setSearchStatus(boolean pStatus){
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION, pStatus));
        status.getsSettingSearchSuggestion = pStatus;
    }

    public Object onTrigger(settingSearchEnums.eSearchModel pCommands, List<Object> pData){
        if(pCommands.equals(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE)){
            onSetSearchEngine((String) pData.get(0));
        }
        else if(pCommands.equals(settingSearchEnums.eSearchModel.M_SET_SEARCH_HISTORY)){

            setSearchHistory((boolean) pData.get(0));
        }
        else if(pCommands.equals(settingSearchEnums.eSearchModel.M_SET_SEARCH_STATUS)){

            setSearchStatus((boolean) pData.get(0));
        }
        return null;
    }

}
