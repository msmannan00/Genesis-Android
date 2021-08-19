package com.darkweb.genesissearchengine.appManager.orbotManager;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;

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


    public void onTrigger(com.darkweb.genesissearchengine.appManager.orbotManager.orbotEnums.eOrbotModelCommands pCommands, List<Object> pData){
        if(pCommands == com.darkweb.genesissearchengine.appManager.orbotManager.orbotEnums.eOrbotModelCommands.M_BRIDGE_SWITCH){
            onBridgeSwitch((boolean) pData.get(0));
        }
        else if(pCommands == com.darkweb.genesissearchengine.appManager.orbotManager.orbotEnums.eOrbotModelCommands.M_VPN_SWITCH){
            onVPNSwitch((boolean) pData.get(0));
        }
    }


}
