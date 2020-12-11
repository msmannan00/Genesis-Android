package com.darkweb.genesissearchengine.appManager.orbotManager;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
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
        pluginController.getInstance().updateBridges(status.sBridgeStatus);
    }

    public void onVPNSwitch(boolean pStatus){
        status.sBridgeVPNStatus = pStatus;
        pluginController.getInstance().updateVPN(status.sBridgeVPNStatus);
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
