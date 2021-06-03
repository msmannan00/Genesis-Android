package com.darkweb.genesissearchengine.pluginManager.orbotPluginManager;

import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.gecko.PrefsHelper;
import org.torproject.android.proxy.OrbotService;
import org.torproject.android.proxy.util.Prefs;
import org.torproject.android.proxy.wrapper.orbotLocalConstants;

import java.lang.ref.WeakReference;
import java.util.List;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;

import static org.torproject.android.proxy.TorServiceConstants.ACTION_START;

// https://github.com/guardianproject/orbot/blob/8fca5f8ecddb4da9565ac3fd8936e4f28acdd352/BUILD.md
public class orbotManager
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

    private void onStartOrbot(String pBridgeCustomBridge, boolean pBridgeGatewayManual, String pBridgeCustomType, boolean pBridgeStatus, int pShowImages, boolean mClearOnExit, String pBridgesDefault){
        orbotLocalConstants.mBridges = pBridgeCustomBridge;
        orbotLocalConstants.mIsManualBridge = pBridgeGatewayManual;
        orbotLocalConstants.mManualBridgeType = pBridgeCustomType;
        orbotLocalConstants.mBridgesDefault = pBridgesDefault;
        Prefs.putBridgesEnabled(pBridgeStatus);
        Intent mServiceIntent = new Intent(mAppContext.get().getApplicationContext(), OrbotService.class);
        mServiceIntent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAppContext.get().stopService(mServiceIntent);
            mAppContext.get().startForegroundService(mServiceIntent);
        }
        else
        {
            mAppContext.get().startService(mServiceIntent);
        }

        initializeProxy(pShowImages, mClearOnExit);
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

    private void onSetProxy(){

    }

    private void initializeProxy(int pShowImages, boolean mClearOnExit)
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
        PrefsHelper.setPref("Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0","en-us,en;q=0.5");

        PrefsHelper.setPref("browser.display.show_image_placeholders",true);
        PrefsHelper.setPref("browser.cache.disk.enable",false);
        PrefsHelper.setPref("browser.cache.memory.enable",true);
        PrefsHelper.setPref("browser.cache.disk.capacity",0);
        PrefsHelper.setPref("privacy.resistFingerprinting",true);
        PrefsHelper.setPref("privacy.donottrackheader.enabled",false);
        PrefsHelper.setPref("privacy.donottrackheader.value",1);
        PrefsHelper.setPref("network.http.sendRefererHeader", 0);
        PrefsHelper.setPref("security.OCSP.require", false);
        PrefsHelper.setPref("security.checkloaduri",false);
        PrefsHelper.setPref("security.mixed_content.block_active_content",false);
        PrefsHelper.setPref("security.mixed_content.block_display_content", false);
        PrefsHelper.setPref("media.peerconnection.enabled",false); //webrtc disabled
        PrefsHelper.setPref("Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0","en-us,en;q=0.5");

        PrefsHelper.setPref("browser.cache.disk_cache_ssl",true);
        PrefsHelper.setPref("signon.formlessCapture.enabled",true);
        PrefsHelper.setPref("signon.storeWhenAutocompleteOff",true);
        PrefsHelper.setPref("signon.storeWhenAutocompleteOff",true);
        PrefsHelper.setPref("dom.event.contextmenu.enabled",true);
        PrefsHelper.setPref("layout.css.visited_links_enabled",true);
        PrefsHelper.setPref("security.ssl3.ecdhe_rsa_aes_128_sha",true);
        PrefsHelper.setPref("security.ssl3.ecdhe_ecdsa_aes_128_sha",true);
        PrefsHelper.setPref("security.ssl3.dhe_rsa_aes_128_sha",true);
        PrefsHelper.setPref("security.ssl3.rsa_des_ede3_sha",true);
        PrefsHelper.setPref("security.ssl3.dhe_rsa_aes_256_sha",true);

        PrefsHelper.setPref("browser.send_pings.require_same_host",false);
        PrefsHelper.setPref("webgl.disabled",false);
        PrefsHelper.setPref("browser.safebrowsing.blockedURIs.enabled",false);
        PrefsHelper.setPref("media.gmp-provider.enabled",false);
        PrefsHelper.setPref("browser.send_pings.require_same_host",false);
        PrefsHelper.setPref("webgl.disabled",false);
        PrefsHelper.setPref("pdfjs.enableWebGL",false);
        PrefsHelper.setPref("browser.safebrowsing.malware.enabled",false);
        PrefsHelper.setPref("security.csp.experimentalEnabled",false);
        PrefsHelper.setPref("network.http.referer.spoofSource",false);
        PrefsHelper.setPref("security.OCSP.require",false);
        PrefsHelper.setPref("security.ssl.treat_unsafe_negotiation_as_broken",	false);
        PrefsHelper.setPref("security.ssl.require_safe_negotiation",false);

        onUpdatePrivacyPreferences(pShowImages, mClearOnExit);
    }

    private void onUpdatePrivacyPreferences(int pShowImages, boolean mClearOnExit)
    {
        PrefsHelper.setPref(keys.PROXY_IMAGE, pShowImages);
        PrefsHelper.setPref("privacy.clearOnShutdown.cache",mClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.downloads",mClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.formdata",mClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.history",mClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.offlineApps",mClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.passwords",mClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.sessions",mClearOnExit);
        PrefsHelper.setPref("privacy.clearOnShutdown.siteSettings",mClearOnExit);

    }

    /*Log Manager*/

    private String getLogs()
    {
        String logs = orbotLocalConstants.mTorLogsStatus;

        if(logs.equals("Starting Genesis | Please Wait ...")){
            return logs;
        }

        if(orbotLocalConstants.mTorLogsStatus.equals("No internet connection")){
            return "Warning | " + orbotLocalConstants.mTorLogsStatus;
        }

        else if(orbotLocalConstants.mTorLogsStatus.startsWith("Invalid Configuration")){
            return orbotLocalConstants.mTorLogsStatus;
        }

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

    private void onDestroy(boolean pThemeApplying){
        if(pThemeApplying) {
            OrbotService.getServiceObject().onDestroy();
        }
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, pluginEnums.eOrbotManager pEventType) {
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
            onUpdatePrivacyPreferences((int) pData.get(0),(boolean) pData.get(1));
        }
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_START_ORBOT))
        {
            onStartOrbot((String) pData.get(0),(boolean) pData.get(1),(String) pData.get(2),(boolean) pData.get(3),(int) pData.get(4),(boolean) pData.get(5),(String) pData.get(6));
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
            onSetProxy();
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
        else if(pEventType.equals(pluginEnums.eOrbotManager.M_DESTROY))
        {
            onDestroy((boolean) pData.get(0));
        }
        return null;
    }

}
