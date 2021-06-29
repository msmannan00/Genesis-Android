package com.darkweb.genesissearchengine.pluginManager;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.appManager.settingManager.privacyManager.settingPrivacyController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.adPluginManager.adManager;
import com.darkweb.genesissearchengine.pluginManager.analyticPluginManager.analyticManager;
import com.darkweb.genesissearchengine.pluginManager.downloadPluginManager.downloadManager;
import com.darkweb.genesissearchengine.pluginManager.langPluginManager.langManager;
import com.darkweb.genesissearchengine.pluginManager.messagePluginManager.messageManager;
import com.darkweb.genesissearchengine.pluginManager.notificationPluginManager.notifictionManager;
import com.darkweb.genesissearchengine.pluginManager.orbotPluginManager.orbotManager;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_SHOW_LOADED_ADS;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eLangManager.M_ACTIVITY_CREATED;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eLangManager.M_RESUME;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.*;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManagerCallbacks.*;

public class pluginController
{
    /*Plugin Instance*/

    private adManager mAdManager;
    private com.darkweb.genesissearchengine.pluginManager.analyticPluginManager.analyticManager mAnalyticsManager;
    private messageManager mMessageManager;
    private notifictionManager mNotificationManager;
    private activityContextManager mContextManager;
    private langManager mLangManager;
    private orbotManager mOrbotManager;
    private downloadManager mDownloadManager;

    /*Private Variables*/

    private static pluginController ourInstance = new pluginController();
    private WeakReference<AppCompatActivity> mHomeController;
    private boolean mIsInitialized = false;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }

    public void preInitialize(homeController context){
        mLangManager = new langManager(context,new langCallback(), new Locale(status.sSettingLanguage), status.mSystemLocale, status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying);
    }

    public void initialize(){
        instanceObjectInitialization();
        mIsInitialized = true;
    }

    public void onRemoveInstances(){
        mHomeController = null;
        mOrbotManager.onRemoveInstances();
    }

    private void instanceObjectInitialization()
    {
        mHomeController = new WeakReference(activityContextManager.getInstance().getHomeController());
        mContextManager = activityContextManager.getInstance();

        mNotificationManager = new notifictionManager(mHomeController,new notificationCallback());
        mAdManager = new adManager(new admobCallback(), ((homeController)mHomeController.get()).getBannerAd(), status.sPaidStatus);
        mAnalyticsManager = new analyticManager(mHomeController,new analyticCallback(), status.sDeveloperBuild);
        mMessageManager = new messageManager(new messageCallback());
        mOrbotManager = orbotManager.getInstance();
        mDownloadManager = new downloadManager(mHomeController,new downloadCallback());
    }

    /*Helper Methods*/

    public boolean isInitialized(){
        return mIsInitialized;
    }

    public void initializeAllServices(AppCompatActivity context){
        int mNotificationStatus = (Integer) dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,1));
        orbotManager.getInstance().initialize(context,new orbotCallback(), mNotificationStatus);
    }

    /*------------------------------------------------ CALLBACK LISTENERS------------------------------------------------------------*/

    /*Ad Manager*/
    private class admobCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            if(event_type.equals(M_SHOW_LOADED_ADS))
                ((homeController)mHomeController.get()).onSetBannerAdMargin();
            return null;
        }
    }

    public Object onAdsInvoke(List<Object> pData, pluginEnums.eAdManager pEventType){
        if(mAdManager !=null){
            return mAdManager.onTrigger(pData, pEventType);
        }
        return null;
    }


    /*Analytics Manager*/
    private class analyticCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type) { return null; }
    }

    public void onAnalyticsInvoke(List<Object> pData, pluginEnums.eAnalyticManager pEventType){
        if(mAnalyticsManager !=null){
            mAnalyticsManager.onTrigger(pData, pEventType);
        }
    }


    /*Notification Manager*/
    private class notificationCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            return null;
        }
    }

    public Object onNotificationInvoke(List<Object> pData, pluginEnums.eNotificationManager pEventType){
        if(mNotificationManager!=null){
            return mNotificationManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Download Manager*/
    private class downloadCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> pData, Object event_type)
        {
            if(event_type.equals(enums.etype.M_DOWNLOAD_FAILURE))
            {
                mMessageManager.onTrigger(Arrays.asList(pData.get(0).toString(), mHomeController.get()),M_DOWNLOAD_FAILURE);
            }
            return null;
        }
    }

    public Object onDownloadInvoke(List<Object> pData, pluginEnums.eDownloadManager pEventType){
        if(mDownloadManager!=null){
            return mDownloadManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Onion Proxy Manager*/
    private class orbotCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            return null;
        }
    }

    public Object onOrbotInvoke(List<Object> pData, pluginEnums.eOrbotManager pEventType){
        if(mOrbotManager !=null){
            return mOrbotManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Lang Manager*/
    public Object onLanguageInvoke(List<Object> pData, pluginEnums.eLangManager pEventType){
        if(mLangManager==null){
            return null;
        }

        if(pEventType.equals(M_RESUME) || pEventType.equals(M_ACTIVITY_CREATED)){
            return mLangManager.onTrigger(Arrays.asList(pData.get(0), status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying), pEventType);
        }

        return mLangManager.onTrigger(pData, pEventType);
    }

    private class langCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            if(event_type.equals(pluginEnums.eLangManager.M_UPDATE_LOCAL)){
                status.mSystemLocale = (Locale)data.get(0);
            }
            return null;
        }
    }

    /*Message Manager*/
    public void onMessageManagerInvoke(List<Object> pData, pluginEnums.eMessageManager pEventType){
        if(mMessageManager!=null){
            mMessageManager.onTrigger(pData,pEventType);
        }
    }

    private class messageCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> pData, Object pEventType)
        {
            if(pEventType.equals(enums.etype.welcome))
            {
                ((homeController)mHomeController.get()).onLoadURL(pData.get(0).toString());
            }
            else if(pEventType.equals(M_PANIC_RESET)){
                new Handler().postDelayed(() -> activityContextManager.getInstance().getHomeController().panicExitInvoked(), 300);
            }
            else if(pEventType.equals(M_DOWNLOAD_SINGLE)){
                if(pData.size()<3){
                    ((homeController)mHomeController.get()).onManualDownload(pData.get(0).toString());
                }else {
                    activityContextManager.getInstance().getHomeController().onManualDownloadFileName((String)pData.get(2),(String)pData.get(0));
                }
            }
            else if(pEventType.equals(M_SECURE_CONNECTION)){
                helperMethod.openActivity(settingPrivacyController.class, constants.CONST_LIST_HISTORY, mHomeController.get(),true);
            }
            else if(pEventType.equals(M_CANCEL_WELCOME)){
                status.sSettingIsWelcomeEnabled = false;
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,false));
            }
            else if(pEventType.equals(enums.etype.reload)){
                if((Boolean) mOrbotManager.onTrigger(null, pluginEnums.eOrbotManager.M_IS_ORBOT_RUNNING))
                {
                    ((homeController)mHomeController.get()).onReload(null);
                }
                else {
                    mMessageManager.onTrigger(Arrays.asList(mHomeController, Collections.singletonList(pData.get(0).toString())),M_START_ORBOT);
                }
            }
            else if(pEventType.equals(M_OPEN_PRIVACY)){
                helperMethod.openActivity(settingPrivacyController.class, constants.CONST_LIST_HISTORY, mHomeController.get(),true);
            }
            else if(pEventType.equals(M_CLEAR_BOOKMARK)){
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK ,pData);
                mContextManager.getBookmarkController().onclearData();
            }
            else if(pEventType.equals(M_CLEAR_HISTORY)){
                dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY ,pData);
                mContextManager.getHistoryController().onclearData();
            }
            else if(pEventType.equals(M_BOOKMARK)){
                String [] dataParser = pData.get(0).toString().split("split");
                if(dataParser.length>1){
                    dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_ADD_BOOKMARK ,Arrays.asList(dataParser[0],dataParser[1]));
                }else {
                    dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_ADD_BOOKMARK ,Arrays.asList(dataParser[0],""));
                }
            }
            else if(pEventType.equals(M_APP_RATED)){
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
            }
            else if(pEventType.equals(M_CUSTOM_BRIDGE)){
                return status.sBridgeCustomBridge;
            }
            else if(pEventType.equals(M_BRIDGE_TYPE)){
                return status.sBridgeCustomType;
            }
            else if(pEventType.equals(M_DOWNLOAD_FILE)){
                ((homeController)mHomeController.get()).onDownloadFile();
            }
            else if(pEventType.equals(M_DOWNLOAD_FILE_MANUAL)){
                ((homeController)mHomeController.get()).onManualDownload(pData.get(0).toString());
            }
            else if(pEventType.equals(M_OPEN_LINK_NEW_TAB)){
                ((homeController)mHomeController.get()).postNewLinkTabAnimationInBackgroundTrigger(pData.get(0).toString());
            }
            else if(pEventType.equals(M_OPEN_LINK_CURRENT_TAB)){
                ((homeController)mHomeController.get()).onLoadURL(pData.get(0).toString());
            }
            else if(pEventType.equals(M_COPY_LINK)){
                helperMethod.copyURL(pData.get(0).toString(),mContextManager.getHomeController());
            }
            else if(pEventType.equals(M_CLEAR_TAB)){
                dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
                ((homeController)mHomeController.get()).initTab(true);
                ((homeController)mHomeController.get()).onDisableTabViewController();
            }
            else if(pEventType.equals(M_REQUEST_BRIDGES)){
                pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(constants.CONST_BACKEND_GOOGLE_URL, this), M_BRIDGE_MAIL);
            }
            else if(pEventType.equals(M_SET_BRIDGES)){
                activityContextManager.getInstance().getBridgeController().onUpdateBridges((String) pData.get(0), (String) pData.get(1));
            }
            else if(pEventType.equals(M_UNDO_SESSION)){
                activityContextManager.getInstance().getHomeController().onLoadRecentTab(null);
            }
            else if(pEventType.equals(M_UNDO_TAB)){
                activityContextManager.getInstance().getTabController().onRestoreTab(null);
            }
            return null;
        }
    }
}
