package com.darkweb.genesissearchengine.appManager.settingManager.notificationManager;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;

import java.util.Arrays;
import java.util.Collections;

import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

class settingNotificationModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingNotificationModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }


    /*Helper Methods*/

    public void updateLocalNotification(boolean pStatus){

        int mStatus = pStatus ? 1 : 0;
        status.sBridgeNotificationManual = mStatus;

        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS, mStatus));
        pluginController.getInstance().setNotificationStatus(mStatus);
        int notificationStatus = pluginController.getInstance().getNotificationStatus();
        if(notificationStatus==0){
            pluginController.getInstance().disableTorNotification();
        } else if(notificationStatus==1){
            pluginController.getInstance().enableTorNotification();
        }else {
            pluginController.getInstance().enableTorNotificationNoBandwidth();
        }
    }


}
