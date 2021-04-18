package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.gecko.PrefsHelper;
import org.torproject.android.service.OrbotService;
import org.torproject.android.service.util.Prefs;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.lang.ref.WeakReference;
import java.util.List;
import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import static org.torproject.android.service.TorServiceConstants.ACTION_START;

// https://github.com/guardianproject/orbot/blob/8fca5f8ecddb4da9565ac3fd8936e4f28acdd352/BUILD.md
class orbotManager
{

    /*Private Variables*/

    private WeakReference<AppCompatActivity> mAppContext;
    private boolean mLogsStarted = false;

    /*Initialization*/

    private static orbotManager sOurInstance = new orbotManager();
    public static orbotManager getInstance()
    {
        return sOurInstance;
    }

    public void initialize(AppCompatActivity pAppContext, eventObserver.eventListener pEvent, int pNotificationStatus){
        this.mAppContext = new WeakReference(pAppContext);

        onInitNotificationStatus(pNotificationStatus);
    }

    public void onRemoveInstances(){
        this.mAppContext = null;
    }

    private void onStartOrbot(){
        orbotLocalConstants.mBridges = status.sBridgeCustomBridge;
        orbotLocalConstants.mIsManualBridge = status.sBridgeGatewayManual;
        orbotLocalConstants.mManualBridgeType = status.sBridgeCustomType;
        Prefs.putBridgesEnabled(status.sBridgeStatus);
        Intent mServiceIntent = new Intent(mAppContext.get(), OrbotService.class);
        mServiceIntent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAppContext.get().getApplicationContext().startForegroundService(mServiceIntent);
        }
        else
        {
            mAppContext.get().getApplicationContext().startService(mServiceIntent);
        }

        initializeProxy();
    }

    /*Helper Methods*/

    private int onGetNotificationStatus(){
        return orbotLocalConstants.mNotificationStatus;
    }
    private void onInitNotificationStatus(int status){
        orbotLocalConstants.mNotificationStatus = status;
    }
    private void onEnableTorNotification(){
        OrbotService.getServiceObject().enableNotification();
    }
    private void onDisableTorNotification(){
        OrbotService.getServiceObject().disableNotification();
    }
    private void onEnableTorNotificationNoBandwidth(){
        OrbotService service = OrbotService.getServiceObject();
        if(service!=null){
            OrbotService.getServiceObject().enableTorNotificationNoBandwidth();
        }
    }

    private  void onUpdateBridges(boolean p_status){
        Prefs.putBridgesEnabled(p_status);
    }
    private  void onUpdateVPN(boolean p_status){
        Prefs.putUseVpn(p_status);
    }

    /*Proxy Manager*/

    private void onSetProxy(String url){
        /* if(url.contains("boogle.store")){
            PrefsHelper.setPref(keys.PROXY_TYPE, 0);
            PrefsHelper.setPref(keys.PROXY_SOCKS,null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,null);
            PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,null);
        }
        else {
            PrefsHelper.setPref(keys.PROXY_TYPE, 1);
            PrefsHelper.setPref(keys.PROXY_SOCKS,constants.CONST_PROXY_SOCKS);
            PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, 9050);
            PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,constants.CONST_PROXY_SOCKS_VERSION);
            PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,constants.CONST_PROXY_SOCKS_REMOTE_DNS);
        } */

        PrefsHelper.setPref(keys.PROXY_TYPE, 1);
        PrefsHelper.setPref(keys.PROXY_SOCKS,constants.CONST_PROXY_SOCKS);
        PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, 9050);
        PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,constants.CONST_PROXY_SOCKS_VERSION);
        PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,constants.CONST_PROXY_SOCKS_REMOTE_DNS);
    }

    private void initializeProxy()
    {
        PrefsHelper.setPref(keys.PROXY_TYPE, 0);
        PrefsHelper.setPref(keys.PROXY_SOCKS,null);
        PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, null);
        PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,null);
        PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,null);

        PrefsHelper.setPref(keys.PROXY_TYPE, 1);
        PrefsHelper.setPref(keys.PROXY_SOCKS,constants.CONST_PROXY_SOCKS);
        PrefsHelper.setPref(keys.PROXY_SOCKS_PORT, 9050);
        PrefsHelper.setPref(keys.PROXY_SOCKS_VERSION,constants.CONST_PROXY_SOCKS_VERSION);
        PrefsHelper.setPref(keys.PROXY_SOCKS_REMOTE_DNS,constants.CONST_PROXY_SOCKS_REMOTE_DNS);

        PrefsHelper.setPref(keys.PROXY_CACHE,constants.CONST_PROXY_CACHE);
        PrefsHelper.setPref(keys.PROXY_MEMORY,constants.CONST_PROXY_MEMORY);
        PrefsHelper.setPref(keys.PROXY_DO_NOT_TRACK_HEADER_ENABLED,constants.CONST_PROXY_DO_NOT_TRACK_HEADER_ENABLED);
        PrefsHelper.setPref(keys.PROXY_DO_NOT_TRACK_HEADER_VALUE,constants.CONST_PROXY_DO_NOT_TRACK_HEADER_VALUE);

        PrefsHelper.setPref("browser.cache.disk.enable",true);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref("browser.cache.disk.capacity",1000);

        onUpdatePrivacyPreferences();
    }

    private void onUpdatePrivacyPreferences()
    {
        PrefsHelper.setPref(keys.PROXY_IMAGE, status.sShowImages);
        PrefsHelper.setPref("browser.display.show_image_placeholders",true);
        PrefsHelper.setPref("browser.cache.disk.enable",false);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref("browser.cache.disk.capacity",0);
        PrefsHelper.setPref("privacy.resistFingerprinting",true);
        PrefsHelper.setPref("privacy.clearOnShutdown.cache",status.sClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.downloads",status.sClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.formdata",status.sClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.history",status.sClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.offlineApps",status.sClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.passwords",status.sClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.sessions",status.sClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.siteSettings",status.sClearOnExit);
        PrefsHelper.setPref("privacy.donottrackheader.enabled",false);
        PrefsHelper.setPref("privacy.donottrackheader.value",1);
        PrefsHelper.setPref("network.http.sendRefererHeader", 0);
        PrefsHelper.setPref("security.OCSP.require", true);
        PrefsHelper.setPref("security.checkloaduri",true);
        PrefsHelper.setPref("security.mixed_content.block_display_content", true);
        PrefsHelper.setPref("media.peerconnection.enabled",false); //webrtc disabled
    }

    /*Log Manager*/

    private String getLogs()
    {
        String logs = orbotLocalConstants.mTorLogsStatus;

        if(!logs.contains("Bootstrapped") && !mLogsStarted){
            logs = "Initializing Bootstrap";
            mLogsStarted = true;
        }

        if(!logs.equals(strings.GENERIC_EMPTY_STR))
        {
            String Logs = logs;
            Logs="Installing | " + Logs.replace("FAILED","Securing");
            return Logs;
        }
        return "Loading Please Wait";
    }

    private boolean isOrbotRunning(){
        return orbotLocalConstants.mIsTorInitialized;
    }

    private void newCircuit(){
        OrbotService.getServiceObject().newIdentity();
    }

    private String getOrbotStatus(){
        return OrbotService.getServiceObject().getProxyStatus();
    }

    /*External Triggers*/

    Object onTrigger(List<Object> pData, pluginEnums.eOrbotManager pEventType) {
        if(pEventType.equals(pluginEnums.eOrbotManager.M_GET_NOTIFICATION_STATUS))
        {
            return onGetNotificationStatus();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_ENABLE_NOTIFICATION))
        {
            onEnableTorNotification();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION))
        {
            onDisableTorNotification();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_DISABLE_NOTIFICATION_NO_BANDWIDTH))
        {
            onEnableTorNotificationNoBandwidth();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_GET_LOGS))
        {
            return getLogs();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_UPDATE_PRIVACY))
        {
            onUpdatePrivacyPreferences();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_START_ORBOT))
        {
            onStartOrbot();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_IS_ORBOT_RUNNING))
        {
            isOrbotRunning();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_GET_ORBOT_STATUS))
        {
            return getOrbotStatus();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_UPDATE_VPN))
        {
            onUpdateVPN((boolean)pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_UPDATE_BRIDGES))
        {
            onUpdateBridges((boolean)pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_SET_PROXY))
        {
            onSetProxy((String)pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_SHOW_NOTIFICATION_STATUS))
        {
            onInitNotificationStatus((int)pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_ORBOT_RUNNING))
        {
            return isOrbotRunning();
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_NEW_CIRCUIT))
        {
            newCircuit();
        }
        return null;
    }

}
