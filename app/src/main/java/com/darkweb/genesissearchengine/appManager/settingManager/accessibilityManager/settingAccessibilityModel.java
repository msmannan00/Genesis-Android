package com.darkweb.genesissearchengine.appManager.settingManager.accessibilityManager;

import android.view.View;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;

import java.util.Arrays;

class settingAccessibilityModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingAccessibilityModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    public void onZoomSettingUpdate(boolean pStatus){
        status.sSettingEnableZoom = pStatus;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_ZOOM,pStatus));
    }

    public void onVoiceInputSettingUpdate(boolean pStatus){
        status.sSettingEnableVoiceInput = pStatus;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT,pStatus));
    }

    public void onFontSizeAdjustableUpdate(boolean pStatus){
        status.sSettingFontAdjustable = pStatus;
        status.sSettingFontSize = 5;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,pStatus));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_FONT_SIZE,(int)status.sSettingFontSize));
    }

}
