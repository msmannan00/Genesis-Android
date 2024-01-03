package com.hiddenservices.onionservices.appManager.settingManager.generalManager;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;

import java.util.List;

class settingGeneralModel {
    /*Variable Declaration*/


    /*Initializations*/

    settingGeneralModel(eventObserver.eventListener ignoredMEvent) {

    }

    protected void onInit(){
    }

    /*Helper Methods*/
    private void onFullScreenBrowsing(boolean pStatus) {
        status.sFullScreenBrowsing = pStatus;
    }

    private void onSelectThemeLight(int pStatus) {
        status.sTheme = pStatus;
    }

    private void onURLInNewTab(boolean pStatus) {
        status.sOpenURLInNewTab = pStatus;
    }

    public Object onTrigger(settingGeneralEnums.eGeneralModel pCommands, List<Object> pData) {
        if (pCommands.equals(settingGeneralEnums.eGeneralModel.M_FULL_SCREEN_BROWSING)) {
            onFullScreenBrowsing((boolean) pData.get(0));
        } else if (pCommands.equals(settingGeneralEnums.eGeneralModel.M_SELECT_THEME)) {
            onSelectThemeLight((int) pData.get(0));
        } else if (pCommands.equals(settingGeneralEnums.eGeneralModel.M_URL_NEW_TAB)) {
            onURLInNewTab((boolean) pData.get(0));
        }
        return null;
    }

}
