package com.hiddenservices.onionservices.appManager.settingManager.searchEngineManager;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;

import java.util.List;


class settingSearchModel {
    /*Variable Declaration*/


    /*Initializations*/

    settingSearchModel(eventObserver.eventListener ignoredMEvent) {

    }

    protected void onInit(){

    }


    /*Helper Methods*/

    private void onSetSearchEngine(String pUrl) {
        status.sSettingDefaultSearchEngine = pUrl;
    }

    private void setSearchHistory(boolean pStatus) {
        status.sSettingSearchHistory = pStatus;
    }

    private void setSearchSuggestionStatus(boolean pStatus) {
        status.sSearchSuggestionStatus = pStatus;
        if (!pStatus) {
            setSearchHistory(false);
        }
    }

    public Object onTrigger(settingSearchEnums.eSearchModel pCommands, List<Object> pData) {
        if (pCommands.equals(settingSearchEnums.eSearchModel.M_SET_SEARCH_ENGINE)) {
            onSetSearchEngine((String) pData.get(0));
        } else if (pCommands.equals(settingSearchEnums.eSearchModel.M_SET_SEARCH_HISTORY)) {

            setSearchHistory((boolean) pData.get(0));
        } else if (pCommands.equals(settingSearchEnums.eSearchModel.M_SET_SEARCH_SUGGESTION_STATUS)) {

            setSearchSuggestionStatus((boolean) pData.get(0));
        }
        return null;
    }

}
