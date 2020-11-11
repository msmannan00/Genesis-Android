package com.darkweb.genesissearchengine.appManager.settingManager.generalManager;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import java.util.Arrays;
import java.util.List;

class settingGeneralModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingGeneralModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/
    private void onFullScreenBrowsing(boolean pStatus){
        status.sFullScreenBrowsing = pStatus;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,pStatus));
    }

    private void onSelectThemeLight(int pStatus){
        status.sTheme = pStatus;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME,pStatus));
    }

    private void onURLInNewTab(boolean pStatus) {
        status.sOpenURLInNewTab = pStatus;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,pStatus));
    }

    public Object onTrigger(settingGeneralEnums.eGeneralModel pCommands, List<Object> pData){
        if(pCommands.equals(settingGeneralEnums.eGeneralModel.M_FULL_SCREEN_BROWSING)){
            onFullScreenBrowsing((boolean)pData.get(0));
        }
        else if(pCommands.equals(settingGeneralEnums.eGeneralModel.M_SELECT_THEME)){
            onSelectThemeLight((int)pData.get(0));
        }
        else if(pCommands.equals(settingGeneralEnums.eGeneralModel.M_URL_NEW_TAB)){
            onURLInNewTab((boolean)pData.get(0));
        }
        return null;
    }

}
