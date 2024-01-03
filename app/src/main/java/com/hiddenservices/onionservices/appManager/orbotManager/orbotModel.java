package com.hiddenservices.onionservices.appManager.orbotManager;

import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class orbotModel {
    /*Variable Declaration*/

    /*Initializations*/

    orbotModel(eventObserver.eventListener ignoredMEvent) {

    }

    protected void onInit(){

    }

    /*Helper Methods*/

    public void onBridgeSwitch(boolean pStatus) {
        status.sBridgeStatus = pStatus;
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList(status.sBridgeStatus), pluginEnums.eOrbotManager.M_UPDATE_BRIDGES);
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES, status.sBridgeStatus));
    }

    public void onVPNSwitch(boolean pStatus) {
        status.sVPNStatus = pStatus;
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList(status.sBridgeStatus), pluginEnums.eOrbotManager.M_UPDATE_VPN);
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED, status.sVPNStatus));
    }


    public void onTrigger(orbotEnums.eOrbotModelCommands pCommands, List<Object> pData) {
        if (pCommands == orbotEnums.eOrbotModelCommands.M_BRIDGE_SWITCH) {
            onBridgeSwitch((boolean) pData.get(0));
        } else if (pCommands == orbotEnums.eOrbotModelCommands.M_VPN_SWITCH) {
            onVPNSwitch((boolean) pData.get(0));
        }
    }


}
