package com.darkweb.genesissearchengine.appManager.settingManager.trackingManager;

import android.view.View;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;

import org.mozilla.geckoview.ContentBlocking;

import java.util.Arrays;
import java.util.List;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_ALL;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_NONE;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS;

class settingTrackingModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingTrackingModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    private void onTracking(View pView){
        if(pView.getId() == R.id.pTrackingOption1){
            status.sSettingTrackingProtection = ContentBlocking.AntiTracking.NONE;
        }
        if(pView.getId() == R.id.pTrackingOption2){
            status.sSettingTrackingProtection = ContentBlocking.AntiTracking.DEFAULT;
        }
        if(pView.getId() == R.id.pTrackingOption3){
            status.sSettingTrackingProtection = ContentBlocking.AntiTracking.STRICT;
        }
    }

    public Object onTrigger(settingTrackingEnums.eTrackingModel pCommands, List<Object> pData){
        if(pCommands.equals(settingTrackingEnums.eTrackingModel.M_SET_TRACKING_PROTECTION)){
            onTracking((View) pData.get(0));
        }
        return null;
    }

}
