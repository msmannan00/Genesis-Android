package com.hiddenservices.onionservices.appManager.settingManager.notificationManager;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import java.util.List;

class settingNotificationModel {
    /*Variable Declaration*/

    /*Initializations*/

    settingNotificationModel(eventObserver.eventListener ignoredMEvent) {

    }

    protected void onInit(){

    }

    /*Helper Methods*/

    private void updateLocalNotification(boolean pStatus) {

        int mStatus = pStatus ? 1 : 0;
        status.sNotificaionStatus = mStatus;
        if (!pStatus) {
            activityContextManager.getInstance().getHomeController().onHideDefaultNotification();
            pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION);
        } else {
            if(status.sTorBrowsing){
                pluginController.getInstance().onOrbotInvoke(null, pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION);
            }else {
                activityContextManager.getInstance().getHomeController().onShowDefaultNotification(true);
            }
        }
        activityContextManager.getInstance().getHomeController().onReloadProxy();
    }

    public Object onTrigger(settingNotificationEnums.eNotificationModel pCommands, List<Object> pData) {
        if (pCommands.equals(settingNotificationEnums.eNotificationModel.M_UPDATE_LOCAL_NOTIFICATION)) {
            updateLocalNotification((boolean) pData.get(0));
        }
        return null;
    }

}
