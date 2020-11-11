package com.darkweb.genesissearchengine.appManager.settingManager.notificationManager;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import java.util.Arrays;
import java.util.List;

class settingNotificationModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingNotificationModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }


    /*Helper Methods*/

    private void updateLocalNotification(boolean pStatus){

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

    public Object onTrigger(settingNotificationEnums.eNotificationModel pCommands, List<Object> pData){
        if(pCommands.equals(settingNotificationEnums.eNotificationModel.M_UPDATE_LOCAL_NOTIFICATION)){
            updateLocalNotification((boolean)pData.get(0));
        }
        return null;
    }

}
