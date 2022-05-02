package com.hiddenservices.onionservices.appManager.settingManager.advertSetttings;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hiddenservices.onionservices.appManager.settingManager.advertSetttings.advertResources.advert_constants;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.sharedUIMethod;

import java.util.ArrayList;
import java.util.List;

class advertSettingViewController {
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;
    private ArrayList<SwitchMaterial> mSwitchButton;

    /*Initializations*/

    advertSettingViewController(advertSettingController pContext, eventObserver.eventListener pEvent, ArrayList<com.google.android.material.switchmaterial.SwitchMaterial> pSwitchButton) {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mSwitchButton = pSwitchButton;

        initPostUI();
        initViews();
    }

    private void initViews() {
        if (advert_constants.S_INSENSITIVE_CATEGORIES) {
            mSwitchButton.get(0).toggle();
        }
        if (advert_constants.S_APP_REDIRECTION) {
            mSwitchButton.get(1).toggle();
        }
        if (advert_constants.S_SEARCH_RESULTS) {
            mSwitchButton.get(2).toggle();
        }
        if (advert_constants.S_SENSOR_ADVERTISEMENT) {
            mSwitchButton.get(3).toggle();
        }
        if (advert_constants.S_NON_TRACKABLE_ADVERTISEMENT) {
            mSwitchButton.get(4).toggle();
        }
        if (advert_constants.S_VIDEO_ADVERTISEMENT) {
            mSwitchButton.get(5).toggle();
        }
        if (advert_constants.S_AUTHORIZED_ADVERTISERS) {
            mSwitchButton.get(6).toggle();
        }
        if (advert_constants.S_ERROR_REPORTING) {
            mSwitchButton.get(7).toggle();
        }
        if (advert_constants.S_NATIVE_ADVERTISERS) {
            mSwitchButton.get(8).toggle();
        }
    }

    /*Helper Methods*/

    private void initPostUI() {
        sharedUIMethod.updateStatusBar(mContext);
    }

    private void onToggleSwitch(int m_id) {
        mSwitchButton.get(m_id).toggle();
    }
    /*Triggers*/

    public Object onTrigger(advertSettingEnums.eAdvertSettingViewController pCommands, List<Object> pData) {
        if (pCommands.equals(advertSettingEnums.eAdvertSettingViewController.M_TOGGLE_SWITCH)) {
            onToggleSwitch((int) pData.get(0));
        }
        return null;
    }

    public Object onTrigger(advertSettingEnums.eAdvertSettingViewController pCommands) {
        return onTrigger(pCommands, null);
    }
}
