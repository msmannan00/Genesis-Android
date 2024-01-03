package com.hiddenservices.onionservices.appManager.bridgeManager;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.constants.constants;
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

import static com.hiddenservices.onionservices.constants.strings.BRIDGE_CUSTOM_BRIDGE_MEEK;
import static com.hiddenservices.onionservices.constants.strings.BRIDGE_CUSTOM_BRIDGE_OBFS4;
import static com.hiddenservices.onionservices.constants.strings.BRIDGE_CUSTOM_BRIDGE_SNOWFLAKES;
import static com.hiddenservices.onionservices.constants.strings.GENERIC_EMPTY_STR;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_BRIDGE_MAIL;

class bridgeModel {
    /*Variable Declaration*/

    private AppCompatActivity mContext;

    /*Initializations*/

    bridgeModel(eventObserver.eventListener ignoredMEvent, AppCompatActivity pContext) {
        mContext = pContext;
    }

    protected void onInit(){
    }

    /*Helper Methods*/

    public void requestBridges() {
        pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(constants.CONST_BACKEND_GOOGLE_URL, mContext), M_BRIDGE_MAIL);
    }

    public void onCustomChecked(String pBridge, String pType) {
        status.sBridgeCustomBridge = pBridge;
        status.sBridgeCustomType = pType;
        status.sBridgeGatewayManual = true;

        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1, status.sBridgeCustomBridge));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE, status.sBridgeCustomType));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL, status.sBridgeGatewayManual));
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList("custom"), pluginEnums.eOrbotManager.M_UPDATE_BRIDGE_LIST);
    }

    public void onMeekChecked() {
        status.sBridgeCustomBridge = BRIDGE_CUSTOM_BRIDGE_MEEK;
        status.sBridgeCustomType = GENERIC_EMPTY_STR;
        status.sBridgeGatewayManual = false;

        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1, status.sBridgeCustomBridge));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE, status.sBridgeCustomType));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL, status.sBridgeGatewayManual));
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList("meek"), pluginEnums.eOrbotManager.M_UPDATE_BRIDGE_LIST);
    }

    public void onObfsChecked() {
        status.sBridgeCustomBridge = BRIDGE_CUSTOM_BRIDGE_OBFS4;
        status.sBridgeCustomType = GENERIC_EMPTY_STR;
        status.sBridgeGatewayManual = false;

        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1, status.sBridgeCustomBridge));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE, status.sBridgeCustomType));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL, status.sBridgeGatewayManual));
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList("obfs4"), pluginEnums.eOrbotManager.M_UPDATE_BRIDGE_LIST);
    }

    public void onSnowChecked() {
        status.sBridgeCustomBridge = BRIDGE_CUSTOM_BRIDGE_SNOWFLAKES;
        status.sBridgeCustomType = GENERIC_EMPTY_STR;
        status.sBridgeGatewayManual = false;

        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1, status.sBridgeCustomBridge));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE, status.sBridgeCustomType));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL, status.sBridgeGatewayManual));
        pluginController.getInstance().onOrbotInvoke(Collections.singletonList("snowflake"), pluginEnums.eOrbotManager.M_UPDATE_BRIDGE_LIST);
    }

    public void onTrigger(bridgeEnums.eBridgeModelCommands pCommands, List<Object> pData) {
        if (pCommands == bridgeEnums.eBridgeModelCommands.M_REQUEST_BRIDGE) {
            requestBridges();
        } else if (pCommands == bridgeEnums.eBridgeModelCommands.M_CUSTOM_BRIDGE) {
            onCustomChecked((String) pData.get(0), (String) pData.get(1));
        } else if (pCommands == bridgeEnums.eBridgeModelCommands.M_MEEK_BRIDGE) {
            onMeekChecked();
        } else if (pCommands == bridgeEnums.eBridgeModelCommands.M_OBFS_CHECK) {
            onObfsChecked();
        } else if (pCommands == bridgeEnums.eBridgeModelCommands.M_SNOWFLAKES_BRIDGE) {
            onSnowChecked();
        }
    }


}
