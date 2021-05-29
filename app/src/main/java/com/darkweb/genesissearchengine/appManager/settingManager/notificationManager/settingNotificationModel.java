package com.darkweb.genesissearchengine.appManager.settingManager.notificationManager;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;

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
        if(!pStatus){
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION);
        } else{
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION);
        }
    }

    public Object onTrigger(settingNotificationEnums.eNotificationModel pCommands, List<Object> pData){
        if(pCommands.equals(settingNotificationEnums.eNotificationModel.M_UPDATE_LOCAL_NOTIFICATION)){
            updateLocalNotification((boolean)pData.get(0));
        }
        return null;
    }

}
