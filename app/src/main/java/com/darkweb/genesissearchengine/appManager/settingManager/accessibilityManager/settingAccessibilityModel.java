package com.darkweb.genesissearchengine.appManager.settingManager.accessibilityManager;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import java.util.List;

class settingAccessibilityModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingAccessibilityModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    private void onZoomSettingUpdate(boolean pStatus){
        status.sSettingEnableZoom = pStatus;
    }

    private void onVoiceInputSettingUpdate(boolean pStatus){
        status.sSettingEnableVoiceInput = pStatus;
    }

    public Object onTrigger(settingAccessibilityEnums.eAccessibilityViewController pCommands, List<Object> pData){
        if(pCommands.equals(settingAccessibilityEnums.eAccessibilityViewController.M_ZOOM_SETTING)){
            onZoomSettingUpdate((boolean)pData.get(0));
        }
        else if(pCommands.equals(settingAccessibilityEnums.eAccessibilityViewController.M_VOICE_INPUT_SETTING)){
            onVoiceInputSettingUpdate((boolean)pData.get(0));
        }
        return null;
    }
}
