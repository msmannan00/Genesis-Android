package com.hiddenservices.onionservices.appManager.settingManager.logManager;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

class settingLogViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;

    private SwitchMaterial mSettingLogStatusSwitch;

    /*Initializations*/

    settingLogViewController(settingLogController pContext, eventObserver.eventListener ignoredPEvent, SwitchMaterial pSettingLogStatusSwitch) {
        this.mSettingLogStatusSwitch = pSettingLogStatusSwitch;
        this.mContext = pContext;
    }

    protected void onInit(){
        initPostUI();
    }

    private void initViews(boolean pLogThemeStyle) {
        mSettingLogStatusSwitch.setChecked(pLogThemeStyle);
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
        if (settingLogEnums.eLogViewController.M_toggle_LOG_VIEW.equals(pCommands)) {
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
