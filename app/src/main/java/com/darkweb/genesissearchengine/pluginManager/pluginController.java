package com.darkweb.genesissearchengine.pluginManager;

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
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eAdManagerCallbacks.M_SHOW_LOADED_ADS;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.*;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManagerCallbacks.*;

public class pluginController
{
    /*Plugin Instance*/

    private adManager mAdManager;
    private com.darkweb.genesissearchengine.pluginManager.analyticManager mAnalyticsManager;
    private messageManager mMessageManager;
    private notifictionManager mNotificationManager;
    private activityContextManager mContextManager;
    private langManager mLangManager;
    private orbotManager mOrbotManager;
    private downloadManager mDownloadManager;

    /*Private Variables*/

    private static pluginController ourInstance = new pluginController();
    private homeController mHomeController;
    private boolean mIsInitialized = false;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }

    public void preInitialize(homeController context){
        mLangManager = new langManager(context,new langCallback(), new Locale(status.sSettingLanguage));
    }

    public void initialize(){
        instanceObjectInitialization();
        mIsInitialized = true;
    }

    private void instanceObjectInitialization()
    {
        mHomeController = activityContextManager.getInstance().getHomeController();
        mContextManager = activityContextManager.getInstance();

        mNotificationManager = new notifictionManager(mHomeController,new notificationCallback());
        mAdManager = new adManager(mHomeController,new admobCallback(), mHomeController.getBannerAd());
        mAnalyticsManager = new com.darkweb.genesissearchengine.pluginManager.analyticManager(mHomeController,new analyticManager());
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
            mHomeController.onSetBannerAdMargin();
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
    private class analyticManager implements eventObserver.eventListener{
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
        public Object invokeObserver(List<Object> data, Object event_type)
        {
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
        return mLangManager.onTrigger(pData, pEventType);
    }

    private class langCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            return null;
        }
    }

    /*Message Manager*/
    public void onMessageManagerInvoke(List<Object> pData, pluginEnums.eMessageManager pEventType){
        mMessageManager.onTrigger(pData,pEventType);
    }

    private class messageCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> pData, Object pEventType)
        {
            if(pEventType.equals(enums.etype.welcome))
            {
                mHomeController.onLoadURL(pData.get(0).toString());
            }
            else if(pEventType.equals(M_DOWNLOAD_SINGLE)){
                activityContextManager.getInstance().getHomeController().onManualDownloadFileName((String)pData.get(2),(String)pData.get(0));
            }
            else if(pEventType.equals(M_SECURE_CONNECTION)){
                helperMethod.openActivity(settingPrivacyController.class, constants.CONST_LIST_HISTORY, mHomeController,true);
            }
            else if(pEventType.equals(M_CANCEL_WELCOME)){
                status.sSettingIsWelcomeEnabled = false;
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,false));
            }
            else if(pEventType.equals(enums.etype.reload)){
                if((Boolean) mOrbotManager.onTrigger(null, pluginEnums.eOrbotManager.M_IS_ORBOT_RUNNING))
                {
                    mHomeController.onReload(null);
                }
                else {
                    mMessageManager.onTrigger(Arrays.asList(mHomeController, Collections.singletonList(pData.get(0).toString())),M_START_ORBOT);
                }
            }
            else if(pEventType.equals(M_CLEAR_HISTORY)){
                dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY ,null);
                mContextManager.getHistoryController().onclearData();
                mHomeController.onClearSession();
                dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
                mHomeController.initTab(false);
            }
            else if(pEventType.equals(M_CLEAR_BOOKMARK)){
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK ,pData);
                mContextManager.getBookmarkController().onclearData();
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
            else if(pEventType.equals(M_DOWNLOAD_FILE)){
                mHomeController.onDownloadFile();
            }
            else if(pEventType.equals(M_DOWNLOAD_FILE_MANUAL)){
                mHomeController.onManualDownload(pData.get(0).toString());
            }
            else if(pEventType.equals(M_OPEN_LINK_NEW_TAB)){
                mHomeController.onOpenLinkNewTab(pData.get(0).toString());
            }
            else if(pEventType.equals(M_OPEN_LINK_CURRENT_TAB)){
                mHomeController.onLoadURL(pData.get(0).toString());
            }
            else if(pEventType.equals(M_COPY_LINK)){
                helperMethod.copyURL(pData.get(0).toString(),mContextManager.getHomeController());
            }
            else if(pEventType.equals(M_CLEAR_TAB)){
                dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
                mHomeController.initTab(true);
                mHomeController.onDisableTabViewController();
            }
            else if(pEventType.equals(M_REQUEST_BRIDGES)){
                pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(constants.CONST_BACKEND_GOOGLE_URL, this), M_BRIDGE_MAIL);
            }
            else if(pEventType.equals(M_SET_BRIDGES)){
                activityContextManager.getInstance().getBridgeController().onUpdateBridges((String) pData.get(0));
            }
            return null;
        }
    }
}
