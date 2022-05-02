package com.hiddenservices.onionservices.appManager.settingManager.logManager;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

class settingLogViewController {
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    private SwitchMaterial mSettingLogStatusSwitch;

    /*Initializations*/

    settingLogViewController(settingLogController pContext, eventObserver.eventListener pEvent, SwitchMaterial pSettingLogStatusSwitch) {
        this.mSettingLogStatusSwitch = pSettingLogStatusSwitch;
        this.mEvent = pEvent;
        this.mContext = pContext;

        initPostUI();
    }

    private void initViews(boolean pLogThemeStyle) {
        if (pLogThemeStyle) {
            mSettingLogStatusSwitch.setChecked(true);
        } else {
            mSettingLogStatusSwitch.setChecked(false);
        }
    }

    /*Helper Methods*/

    private void toggleLogThemeStyle() {
        mSettingLogStatusSwitch.toggle();
    }

    private void initPostUI() {
        sharedUIMethod.updateStatusBar(mContext);
    }

    /*Triggers*/

    public Object onTrigger(settingLogEnums.eLogViewController pCommands, List<Object> pData) {
        if (settingLogEnums.eLogViewController.M_TOOGLE_LOG_VIEW.equals(pCommands)) {
            toggleLogThemeStyle();
        } else if (settingLogEnums.eLogViewController.M_INIT_VIEW.equals(pCommands)) {
            initViews((boolean) pData.get(0));
        }
        return null;
    }

    public Object onTrigger(settingLogEnums.eLogViewController pCommands) {
        return onTrigger(pCommands, null);
    }
}
