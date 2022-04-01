package com.hiddenservices.onionservices.pluginManager.orbotPluginManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import org.orbotproject.android.service.OrbotService;
import org.orbotproject.android.service.util.Prefs;
import org.orbotproject.android.service.wrapper.orbotLocalConstants;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import static android.content.Context.MODE_PRIVATE;
import static com.hiddenservices.onionservices.pluginManager.orbotPluginManager.orbotPluginEnums.eLogManager.M_GET_CLEANED_LOGS;
import static org.orbotproject.android.service.TorServiceConstants.ACTION_START;
import static org.orbotproject.android.service.TorServiceConstants.ACTION_STOP;

// https://github.com/guardianproject/orbot/blob/8fca5f8ecddb4da9565ac3fd8936e4f28acdd352/BUILD.md

public class orbotManager
{

    /*Private Variables*/

    private WeakReference<AppCompatActivity> mAppContext;
    private orbotLogManager mLogManger;

    /*Initialization*/

    private static orbotManager sOurInstance = new orbotManager();
    public static orbotManager getInstance()
    {
        return sOurInstance;
    }

    public void initialize(AppCompatActivity pAppContext, eventObserver.eventListener pEvent, int pNotificationStatus){
        this.mAppContext = new WeakReference(pAppContext);
        this.mLogManger = new orbotLogManager();

        orbotLocalConstants.mNotificationStatus = pNotificationStatus;
    }

    private void onInitlizeOrbot(String pBridgeCustomBridge, boolean pBridgeGatewayManual, String pBridgeCustomType, boolean pBridgeStatus, String pBridgesDefault){
        orbotLocalConstants.mBridges = pBridgeCustomBridge;
        orbotLocalConstants.mIsManualBridge = pBridgeGatewayManual;
        orbotLocalConstants.mManualBridgeType = pBridgeCustomType;
        orbotLocalConstants.mBridgesDefault = pBridgesDefault;
        Prefs.putBridgesEnabled(pBridgeStatus);

        onInitailizeService();
    }

    private void onInitailizeService(){

        Intent startTorIntent = new Intent(mAppContext.get(), OrbotService.class);
        startTorIntent.setAction(ACTION_START);
        if (mAppContext.get().getPackageName() != null) {
            startTorIntent.putExtra(OrbotService.EXTRA_PACKAGE_NAME, mAppContext.get().getPackageName());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Prefs.persistNotifications()) {
            mAppContext.get().startService(startTorIntent);
        } else {
            mAppContext.get().startService(startTorIntent);
        }

        SharedPreferences settings = mAppContext.get().getSharedPreferences("se", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(keys.PROXY_TYPE, 1);
        editor.putString(keys.PROXY_SOCKS, constants.CONST_PROXY_SOCKS);
        editor.putInt(keys.PROXY_SOCKS_PORT, orbotLocalConstants.mSOCKSPort);
        editor.putInt(keys.PROXY_SOCKS_VERSION,constants.CONST_PROXY_SOCKS_VERSION);
        editor.putBoolean(keys.PROXY_SOCKS_REMOTE_DNS,constants.CONST_PROXY_SOCKS_REMOTE_DNS);
        editor.apply();

    }

    /*Helper Methods*/

    public Object onTriggerCommands(List<Object> pData, pluginEnums.eOrbotManager pCommands){
        if(pCommands.equals(pluginEnums.eOrbotManager.M_GET_NOTIFICATION_STATUS)){
            return orbotLocalConstants.mNotificationStatus;
        }
        else if(pCommands.equals(pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION)){
            if(OrbotService.getServiceObject()!=null){
                OrbotService.getServiceObject().enableNotification();
            }
        }
        else if(pCommands.equals(pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION)){
            if(OrbotService.getServiceObject()!=null){
                OrbotService.getServiceObject().disableNotification();
            }
        }
        else if(pCommands.equals(pluginEnums.eOrbotManager.M_UPDATE_BRIDGES)){
            Prefs.putBridgesEnabled((boolean)pData.get(0));
        }
        else if(pCommands.equals(pluginEnums.eOrbotManager.M_UPDATE_VPN)){
            Prefs.putUseVpn((boolean)pData.get(0));
        }
        else if(pCommands.equals(pluginEnums.eOrbotManager.M_NEW_CIRCUIT)){
            if(OrbotService.getServiceObject()!=null){
                OrbotService.getServiceObject().newIdentity();
            }
        }
        else if(pCommands.equals(pluginEnums.eOrbotManager.M_GET_ORBOT_STATUS)){
            if(OrbotService.getServiceObject()!=null){
                return OrbotService.getServiceObject().getProxyStatus();
            }
        }

        return null;
    }


    private void onDestroy(boolean pThemeApplying){
        if(!pThemeApplying) {
            Intent mServiceIntent = new Intent(mAppContext.get(), OrbotService.class);
            mServiceIntent.setAction(ACTION_STOP);
            mAppContext.get().startService(mServiceIntent);
            mAppContext.get().stopService(mServiceIntent);
        }
    }

    private String getLogs()
    {
        return (String) mLogManger.onTrigger(Collections.singletonList(orbotLocalConstants.mTorLogsStatus), M_GET_CLEANED_LOGS);
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, pluginEnums.eOrbotManager pEventType) {
        if(pEventType.equals(pluginEnums.eOrbotManager.M_GET_NOTIFICATION_STATUS) || pEventType.equals(pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION) || pEventType.equals(pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION) || pEventType.equals(pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION_NO_BANDWIDTH) || pEventType.equals(pluginEnums.eOrbotManager.M_IS_ORBOT_RUNNING) || pEventType.equals(pluginEnums.eOrbotManager.M_GET_ORBOT_STATUS) || pEventType.equals(pluginEnums.eOrbotManager.M_UPDATE_VPN) || pEventType.equals(pluginEnums.eOrbotManager.M_UPDATE_BRIDGES) || pEventType.equals(pluginEnums.eOrbotManager.M_SHOW_NOTIFICATION_STATUS) || pEventType.equals(pluginEnums.eOrbotManager.M_ORBOT_RUNNING) || pEventType.equals(pluginEnums.eOrbotManager.M_NEW_CIRCUIT))
        {
            return onTriggerCommands(pData, pEventType);
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_GET_LOGS))
        {
            return getLogs();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_UPDATE_PRIVACY))
        {
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_START_ORBOT))
        {
            onInitlizeOrbot((String) pData.get(0),(boolean) pData.get(1),(String) pData.get(2),(boolean) pData.get(3), (String) pData.get(6));
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_DESTROY))
        {
            onDestroy((boolean) pData.get(0));
        }
        return null;
    }

}
