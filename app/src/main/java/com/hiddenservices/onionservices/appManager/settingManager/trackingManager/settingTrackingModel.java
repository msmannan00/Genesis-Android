package com.hiddenservices.onionservices.appManager.settingManager.trackingManager;

import android.view.View;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;

import org.mozilla.geckoview.ContentBlocking;

import java.util.List;

class settingTrackingModel {
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingTrackingModel(eventObserver.eventListener mEvent) {
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    private void onTracking(View pView) {
        if (pView.getId() == R.id.pTrackingOption1) {
            status.sSettingTrackingProtection = ContentBlocking.AntiTracking.NONE;
        }
        if (pView.getId() == R.id.pTrackingOption2) {
            status.sSettingTrackingProtection = ContentBlocking.AntiTracking.DEFAULT;
        }
        if (pView.getId() == R.id.pTrackingOption3) {
            status.sSettingTrackingProtection = ContentBlocking.AntiTracking.STRICT;
        }
    }

    public Object onTrigger(settingTrackingEnums.eTrackingModel pCommands, List<Object> pData) {
        if (pCommands.equals(settingTrackingEnums.eTrackingModel.M_SET_TRACKING_PROTECTION)) {
            onTracking((View) pData.get(0));
        }
        return null;
    }

}
