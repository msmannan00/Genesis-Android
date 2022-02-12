package com.hiddenservices.genesissearchengine.production.appManager.orbotManager;

import com.hiddenservices.genesissearchengine.production.constants.keys;
import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.dataManager.dataController;
import com.hiddenservices.genesissearchengine.production.dataManager.dataEnums;
import com.hiddenservices.genesissearchengine.production.eventObserver;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginController;
import com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class orbotModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    orbotModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    public void onBridgeSwitch(boolean pStatus){
        status.sBridgeStatus = pStatus;
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList(status.sBridgeStatus), pluginEnums.eOrbotManager.M_UPDATE_BRIDGES);
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES,status.sBridgeStatus));
    }

    public void onVPNSwitch(boolean pStatus){
        status.sVPNStatus = pStatus;
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList(status.sBridgeStatus), pluginEnums.eOrbotManager.M_UPDATE_VPN);
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED,status.sVPNStatus));
    }


    public void onTrigger(com.hiddenservices.genesissearchengine.production.appManager.orbotManager.orbotEnums.eOrbotModelCommands pCommands, List<Object> pData){
        if(pCommands == com.hiddenservices.genesissearchengine.production.appManager.orbotManager.orbotEnums.eOrbotModelCommands.M_BRIDGE_SWITCH){
            onBridgeSwitch((boolean) pData.get(0));
        }
        else if(pCommands == com.hiddenservices.genesissearchengine.production.appManager.orbotManager.orbotEnums.eOrbotModelCommands.M_VPN_SWITCH){
            onVPNSwitch((boolean) pData.get(0));
        }
    }


}
