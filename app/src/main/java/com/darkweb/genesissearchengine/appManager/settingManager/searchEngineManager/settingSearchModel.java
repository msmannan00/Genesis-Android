package com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
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
    }

    private void setSearchHistory(boolean pStatus){
        status.sSettingSearchHistory = pStatus;
    }
    private void setSearchStatus(boolean pStatus){
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
