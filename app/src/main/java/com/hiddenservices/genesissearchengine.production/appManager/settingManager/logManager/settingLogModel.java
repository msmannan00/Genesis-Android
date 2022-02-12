package com.hiddenservices.genesissearchengine.production.appManager.settingManager.logManager;

import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.eventObserver;

import java.util.List;

class settingLogModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingLogModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    private void onUpdateLogView(boolean pLogThemeStyle){
        status.sLogThemeStyleAdvanced = pLogThemeStyle;
    }

    /*Helper Methods*/

    public Object onTrigger(settingLogEnums.eLogModel pCommands, List<Object> pData){
        if(settingLogEnums.eLogModel.M_SWITCH_LOG_VIEW.equals(pCommands)){
            onUpdateLogView((boolean)pData.get(0));
        }
        return null;
    }

}
