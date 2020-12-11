package com.darkweb.genesissearchengine.appManager.bridgeManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class bridgeModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    /*Initializations*/

    bridgeModel(eventObserver.eventListener mEvent, AppCompatActivity pContext){
        this.mEvent = mEvent;
        mContext = pContext;
    }

    /*Helper Methods*/

    public void requestBridges(){
        pluginController.getInstance().MessageManagerHandler(mContext, Collections.singletonList(constants.CONST_BACKEND_GOOGLE_URL), enums.eMessageEnums.M_BRIDGE_MAIL);
    }

    public void onCustomChecked(){
        status.sBridgeCustomBridge = strings.BRIDGE_CUSTOM_BRIDGE_CUSTOM;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,status.sBridgeCustomBridge));

    }
    public void onMeekChecked(){
        status.sBridgeCustomBridge = strings.BRIDGE_CUSTOM_BRIDGE_MEEK;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,status.sBridgeCustomBridge));
    }
    public void onObfsChecked(){
        status.sBridgeCustomBridge = strings.BRIDGE_CUSTOM_BRIDGE_OBFS4;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,status.sBridgeCustomBridge));
    }

    public void onTrigger(bridgeEnums.eBridgeModelCommands pCommands, List<Object> pData){
        if(pCommands == bridgeEnums.eBridgeModelCommands.M_REQUEST_BRIDGE){
            requestBridges();
        }
        else if(pCommands == bridgeEnums.eBridgeModelCommands.M_CUSTOM_BRIDGE){
            onCustomChecked();
        }
        else if(pCommands == bridgeEnums.eBridgeModelCommands.M_MEEK_BRIDGE){
            onMeekChecked();
        }
        else if(pCommands == bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK){
            onObfsChecked();
        }
    }


}
