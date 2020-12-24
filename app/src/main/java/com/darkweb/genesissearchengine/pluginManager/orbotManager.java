package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.gecko.PrefsHelper;
import org.torproject.android.service.OrbotService;
import org.torproject.android.service.util.Prefs;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.Arrays;

import com.darkweb.genesissearchengine.constants.*;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;

import static org.torproject.android.service.TorServiceConstants.ACTION_START;

class orbotManager
{

    /*Private Variables*/

    private Context mAppContext;
    private boolean mLogsStarted = false;

    /*Initialization*/

    private static orbotManager sOurInstance = new orbotManager();
    public static orbotManager getInstance()
    {
        return sOurInstance;
    }

    public void initialize(AppCompatActivity app_context, eventObserver.eventListener event){
        initNotification((Integer) dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,1)));
    }

    void startOrbot(Context context){
        orbotLocalConstants.mBridges = status.sBridgeCustomBridge;
        orbotLocalConstants.mIsManualBridge = status.sBridgeGatewayManual;
        this.mAppContext = context;
        Prefs.putBridgesEnabled(status.sBridgeGatewayManual |status.sBridgeGatewayAuto);
        Intent mServiceIntent = new Intent(context, OrbotService.class);
        mServiceIntent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(mServiceIntent);
        }
        else
        {
            context.startService(mServiceIntent);
        }
        initializeProxy();
    }

    int getNotificationStatus(){
        return orbotLocalConstants.mNotificationStatus;
    }
    void initNotification(int status){
        orbotLocalConstants.mNotificationStatus = status;
    }
    void enableTorNotification(){
        OrbotService.getServiceObject().enableNotification();
    }
    void disableTorNotification(){
        OrbotService.getServiceObject().disableNotification();
    }

    void enableTorNotificationNoBandwidth(){
        OrbotService service = OrbotService.getServiceObject();
        if(service!=null){
            OrbotService.getServiceObject().enableTorNotificationNoBandwidth();
        }
    }

    public void updateBridges(boolean p_status){
        Prefs.putBridgesEnabled(p_status);
    }
    public void updateVPN(boolean p_status){
        Prefs.putUseVpn(p_status);
    }

    /*------------------------------------------------------- POST TASK HANDLER -------------------------------------------------------*/

    void setProxy(String url){
        if(url.contains("boogle.store")){
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
        }
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

        setPrivacyPrefs();
    }

    public void setPrivacyPrefs ()
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


    String getLogs()
    {
        String logs = orbotLocalConstants.mTorLogsStatus;

        if(!logs.contains("Bootstrapped") && !mLogsStarted){
            logs = "Initializing Bootstrap";
            mLogsStarted = true;
        }
        else {
            // logs = logs.replaceAll("[^a-zA-Z0-9%\\s+]", "");
            // logs = helperMethod.capitalizeString(logs);
            // logs = logs.replace("(","").replace(":","_FERROR_").replace("NOTICE","").replace(")","").replace("_FERROR_","");
        }


        if(!logs.equals(strings.GENERIC_EMPTY_STR))
        {
            String Logs = logs;
            Logs="Installing | " + Logs.replace("FAILED","Securing");
            return Logs;
        }
        return "Loading Please Wait";
    }

    boolean isOrbotRunning(){
        return orbotLocalConstants.mIsTorInitialized;
    }

}
