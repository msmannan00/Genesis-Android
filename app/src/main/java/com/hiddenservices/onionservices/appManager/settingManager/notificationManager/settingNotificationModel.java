package com.hiddenservices.onionservices.appManager.settingManager.notificationManager;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import java.util.List;

class settingNotificationModel {
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingNotificationModel(eventObserver.eventListener mEvent) {
        this.mEvent = mEvent;
    }


    /*Helper Methods*/

    private void updateLocalNotification(boolean pStatus) {

        int mStatus = pStatus ? 1 : 0;
        status.sBridgeNotificationManual = mStatus;
        if (!pStatus) {
            activityContextManager.getInstance().getHomeController().onShowDefaultNotification();
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION);
        } else {
            if(status.sTorBrowsing){
                activityContextManager.getInstance().getHomeController().onHideDefaultNotification();
            }
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION);
        }
    }

    public Object onTrigger(settingNotificationEnums.eNotificationModel pCommands, List<Object> pData) {
        if (pCommands.equals(settingNotificationEnums.eNotificationModel.M_UPDATE_LOCAL_NOTIFICATION)) {
            updateLocalNotification((boolean) pData.get(0));
        }
        return null;
    }

}
