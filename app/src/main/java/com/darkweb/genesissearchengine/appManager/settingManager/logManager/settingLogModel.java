package com.darkweb.genesissearchengine.appManager.settingManager.logManager;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

import java.util.Arrays;
import java.util.List;

class settingLogModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingLogModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    public void onUpdateLogView(boolean pIsListView){
        status.sLogListView = pIsListView;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW, pIsListView));
    }

    /*Helper Methods*/

    public Object onTrigger(settingLogEnums.eLogModel pCommands, List<Object> pData){
        if(settingLogEnums.eLogModel.M_SWITCH_LOG_VIEW.equals(pCommands)){
            onUpdateLogView((boolean)pData.get(0));
        }
        return null;
    }

}
