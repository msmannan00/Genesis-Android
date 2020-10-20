package com.darkweb.genesissearchengine.pluginManager;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
//import org.torproject.android.service.wrapper.orbotLocalConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class pluginController
{
    /*Plugin Instance*/

    private adManager mAdManager;
    private analyticManager mAnalyticManager;
    private fabricManager mFabricManager;
    private firebaseManager mFirebaseManager;
    private messageManager mMessageManager;
    private activityContextManager mContextManager;
    private boolean mIsInitialized = false;
    private langManager mLangManager;
    private boolean mIsServiceInitialized = false;

    /*Private Variables*/

    private static pluginController ourInstance = new pluginController();
    private homeController mHomeController;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }

    public void initialize(){
        instanceObjectInitialization();
        mIsInitialized = true;
    }

    public void initializeWithAbiError(){
        mMessageManager = new messageManager(new messageCallback());
        mIsInitialized = true;
    }

    public void preInitialize(homeController context){
        mLangManager = new langManager(context,new langCallback());
        mLangManager.setDefaultLanguage(new Locale(status.sSettingLanguage));

        mFabricManager = new fabricManager(context,new fabricCallback());
    }

    private void instanceObjectInitialization()
    {
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager = activityContextManager.getInstance();

        mAdManager = new adManager(getAppContext(),new admobCallback(), mHomeController.getBannerAd());
        mAnalyticManager = new analyticManager(getAppContext(),new analyticCallback());
        mFirebaseManager = new firebaseManager(getAppContext(),new firebaseCallback());
        mMessageManager = new messageManager(new messageCallback());

    }

    public void initializeAllServices(AppCompatActivity context){
        orbotManager.getInstance().initialize(context,new orbotCallback());
    }

    /*Helper Methods*/

    private AppCompatActivity getAppContext()
    {
        return mHomeController;
    }

    public boolean isInitialized(){
        return mIsInitialized;
    }
    void proxyManagerExitInvoke(){

    }

    /*---------------------------------------------- EXTERNAL REQUEST LISTENER-------------------------------------------------------*/

    /*Message Manager*/
    public void MessageManagerHandler(AppCompatActivity app_context,List<String> data,enums.etype type){
        mMessageManager.createMessage(app_context,data,type);
    }
    public void onResetMessage(){
        if(mMessageManager!=null){
            mMessageManager.onReset();
        }
    }

    /*Firebase Manager*/
    public void logEvent(String value){
        if(mFirebaseManager!=null){
            mFirebaseManager.logEvent(value);
        }
    }

    /*Ad Manager*/
    public void initializeBannerAds(){
        mAdManager.loadAds();
    }
    public boolean isAdvertLoaded(){
       return mAdManager.isAdvertLoaded();
    }

    /*Onion Proxy Manager*/
    public void initializeOrbot(){
        orbotManager.getInstance().startOrbot(getAppContext());
    }
    public boolean isOrbotRunning(){
        return orbotManager.getInstance().isOrbotRunning();
    }
    public void setProxy(String url){
        orbotManager.getInstance().setProxy(url);
    }
    public String orbotLogs(){
        return orbotManager.getInstance().getLogs();
    }
    public void enableTorNotification(){
        orbotManager.getInstance().enableTorNotification();
    }
    public void disableTorNotification(){
        orbotManager.getInstance().disableTorNotification();
    }
    public void enableTorNotificationNoBandwidth(){
        orbotManager.getInstance().enableTorNotificationNoBandwidth();
    }
    public void setNotificationStatus(int status){
        orbotManager.getInstance().initNotification(status);
    }
    public int getNotificationStatus(){
        return orbotManager.getInstance().getNotificationStatus();
    }
    public void updateCookiesStatus(){
    }
    public void updateBridges(boolean p_status){
        orbotManager.getInstance().updateBridges(p_status);
    }
    public void updateVPN(boolean p_status){
        orbotManager.getInstance().updateVPN(p_status);
    }

    /*------------------------------------------------ CALLBACK LISTENERS------------------------------------------------------------*/

    /*Ad Manager*/
    private class admobCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype event_type)
        {
            mHomeController.onSetBannerAdMargin();
            return null;
        }
    }

    /*Analytics Manager*/
    private class analyticCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype event_type)
        {
            mAnalyticManager.logUser();
            return null;
        }
    }

    /*Fabric Manager*/
    private class fabricCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype event_type)
        {
            return null;
        }
    }

    /*Firebase Manager*/
    private class firebaseCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype event_type)
        {
            return null;
        }
    }

    /*Lang Manager*/
    private class langCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype event_type)
        {
            return null;
        }
    }

    /*Onion Proxy Manager*/
    private class orbotCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype event_type)
        {
            return null;
        }
    }

    /*Lang Manager*/
    public void setLanguage(AppCompatActivity context){
        mLangManager.setDefaultLanguage(new Locale(status.sSettingLanguage));
    }
    public void onCreate(Activity activity) {
        mLangManager.onCreate(activity);
    }
    public void onResume(Activity activity) {
        mLangManager.onResume(activity);
    }

    /*Message Manager*/
    private class messageCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, enums.etype event_type)
        {
            if(event_type.equals(enums.etype.welcome))
            {
                mHomeController.onLoadURL(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.cancel_welcome)){
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,false));
            }
            else if(event_type.equals(enums.etype.ignore_abi)){
                //mHomeController.ignoreAbiError();
            }
            else if(event_type.equals(enums.etype.reload)){
                if(orbotManager.getInstance().isOrbotRunning())
                {
                    mHomeController.onReload(null);
                }
                else {
                    mMessageManager.createMessage(mHomeController, Collections.singletonList(data.get(0).toString()),enums.etype.start_orbot);
                }
            }
            else if(event_type.equals(enums.etype.clear_history)){
                dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY ,null);
                mContextManager.getHistoryController().onclearData();
                mHomeController.onClearSession();
                dataController.getInstance().clearTabs();
                mHomeController.initTab(false);
            }
            else if(event_type.equals(enums.etype.clear_bookmark)){
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK ,data);
                mContextManager.getBookmarkController().onclearData();
            }
            else if(event_type.equals(enums.etype.bookmark)){
                String [] dataParser = data.get(0).toString().split("split");
                if(dataParser.length>1){
                    logEvent(strings.EVENT_URL_BOOKMARKED);
                    dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_ADD_BOOKMARK ,Arrays.asList(dataParser[0],dataParser[1]));
                }else {
                    dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_ADD_BOOKMARK ,Arrays.asList(dataParser[0],""));
                }
            }
            else if(event_type.equals(enums.etype.app_rated)){
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
            }
            else if(event_type.equals(enums.etype.download_file)){
                mHomeController.onDownloadFile();
            }
            else if(event_type.equals(enums.etype.download_file_manual)){
                mHomeController.onManualDownload(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.connect_vpn)){
                //orbotLocalConstants.sIsTorInitialized = (boolean)data.get(0);
            }
            else if(event_type.equals(enums.etype.open_link_new_tab)){
                mHomeController.onOpenLinkNewTab(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.open_link_current_tab)){
                mHomeController.onLoadURL(data.get(0).toString());
            }
            else if(event_type.equals(enums.etype.copy_link)){
                helperMethod.copyURL(data.get(0).toString(),mContextManager.getHomeController());
            }
            else if(event_type.equals(enums.etype.clear_tab)){
                dataController.getInstance().clearTabs();
                mHomeController.initTab(true);
                activityContextManager.getInstance().getTabController().finish();
            }
            return null;
        }
    }
}
