package com.hiddenservices.onionservices.appManager.settingManager.logManager;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;

import java.util.List;

class settingLogModel {
    /*Variable Declaration*/

    /*Initializations*/

    settingLogModel(eventObserver.eventListener ignoredMEvent) {

    }

    protected void onInit(){
    }

    private void onUpdateLogView(boolean pLogThemeStyle) {
        status.sLogThemeStyleAdvanced = pLogThemeStyle;
    }

    /*Helper Methods*/

    public Object onTrigger(settingLogEnums.eLogModel pCommands, List<Object> pData) {
        if (settingLogEnums.eLogModel.M_SWITCH_LOG_VIEW.equals(pCommands)) {
            onUpdateLogView((boolean) pData.get(0));
        }
        return null;
    }

}
