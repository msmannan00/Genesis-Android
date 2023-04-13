package com.hiddenservices.onionservices.pluginManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses.permissionHandler;
import com.hiddenservices.onionservices.appManager.unproxiedConnectionManager.unproxiedConnectionController;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogController;
import com.hiddenservices.onionservices.appManager.settingManager.privacyManager.settingPrivacyController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.downloadPluginManager.downloadManager;
import com.hiddenservices.onionservices.pluginManager.langPluginManager.langManager;
import com.hiddenservices.onionservices.pluginManager.messagePluginManager.messageManager;
import com.hiddenservices.onionservices.pluginManager.orbotPluginManager.orbotManager;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import static com.hiddenservices.onionservices.constants.constants.CONST_BRIDGES;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_CLICKED;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eLangManager.M_ACTIVITY_CREATED;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eLangManager.M_RESUME;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.*;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.*;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_CLEAR_BOOKMARK;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_CLEAR_HISTORY;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_LOAD_NEW_TAB;

public class pluginController {
    /*Plugin Instance*/

    //private appLovinManager mAdManager;
    //private analyticManager mAnalyticsManager;
    private messageManager mMessageManager;
    private activityContextManager mContextManager;
    private langManager mLangManager;
    private orbotManager mOrbotManager;
    private downloadManager mDownloadManager;

    /*Private Variables*/

    private static pluginController ourInstance = new pluginController();
    private WeakReference<AppCompatActivity> mHomeController;
    private boolean mIsInitialized = false;

    /*Initializations*/

    public static pluginController getInstance() {
        return ourInstance;
    }

    public void preInitialize(homeController context) {
        mLangManager = new langManager(context, new langCallback(), new Locale(status.sSettingLanguage), status.mSystemLocale, status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying);
    }

    public void initialize() {
        instanceObjectInitialization();
        mIsInitialized = true;
    }

    public void onRemoveInstances() {
        mHomeController = null;
    }

    private void instanceObjectInitialization() {
        mHomeController = new WeakReference(activityContextManager.getInstance().getHomeController());
        mContextManager = activityContextManager.getInstance();

        //mAdManager = new appLovinManager(new admobCallback(), ((homeController) mHomeController.get()).getBannerAd(), mHomeController.get());
        //mAnalyticsManager = new analyticManager(mHomeController, new analyticCallback());
        mMessageManager = new messageManager(new messageCallback());
        mOrbotManager = orbotManager.getInstance();
        mDownloadManager = new downloadManager(mHomeController, new downloadCallback());
    }

    /*Helper Methods*/

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public void initializeAllServices(AppCompatActivity context) {
        int mNotificationStatus = (Integer) dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS, 1));
        orbotManager.getInstance().initialize(context, new orbotCallback(), mNotificationStatus);
    }

    /*------------------------------------------------ CALLBACK LISTENERS------------------------------------------------------------*/

    /*Ad Manager*/
//    private class admobCallback implements eventObserver.eventListener {
//        @Override
//        public Object invokeObserver(List<Object> data, Object event_type) {
//            if (event_type.equals(M_ON_AD_LOAD)) {
//                activityContextManager.getInstance().getHomeController().onUpdateBannerAdvert();
//            } else if (event_type.equals(M_ON_AD_CLICKED)) {
//                status.sIsBackgroundAdvertCheck = true;
//                new Handler().postDelayed(() ->
//                {
//                    status.sIsBackgroundAdvertCheck = false;
//                }, 5000);
//            }
//            return null;
//        }
//    }

    //public Object onAdsInvoke(List<Object> pData, pluginEnums.eAdManager pEventType) {

        //if (mAdManager != null) {
        //    return mAdManager.onTrigger(pEventType);
        //}

        //return null;
    //}


    /*Analytics Manager*/
    //private class analyticCallback implements eventObserver.eventListener {
    //    @Override
    //    public Object invokeObserver(List<Object> data, Object event_type) {
          //  return null;
        //}
    //}

    //public void onAnalyticsInvoke(List<Object> pData, pluginEnums.eAnalyticManager pEventType) {
        //if (mAnalyticsManager != null) {
        //    mAnalyticsManager.onTrigger(pData, pEventType);
        //}
    //}

    /*Download Manager*/
    private class downloadCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object event_type) {
            if (event_type.equals(pluginEnums.eDownloadManager.M_DOWNLOAD_FAILURE)) {
                mMessageManager.onTrigger(Arrays.asList(pData.get(0).toString(), mHomeController.get()), M_DOWNLOAD_FAILURE);
            }
            return null;
        }
    }

    public Object onDownloadInvoke(List<Object> pData, pluginEnums.eDownloadManager pEventType) {
        if (mDownloadManager != null) {
            return mDownloadManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Onion Proxy Manager*/
    private class orbotCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object event_type) {
            return null;
        }
    }

    public Object onOrbotInvoke(List<Object> pData, pluginEnums.eOrbotManager pEventType) {
        if (mOrbotManager != null) {
            return mOrbotManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Lang Manager*/
    public Object onLanguageInvoke(List<Object> pData, pluginEnums.eLangManager pEventType) {
        if (mLangManager == null) {
            return null;
        }

        if (pEventType.equals(M_RESUME) || pEventType.equals(M_ACTIVITY_CREATED)) {
            return mLangManager.onTrigger(Arrays.asList(pData.get(0), status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying), pEventType);
        }

        return mLangManager.onTrigger(pData, pEventType);
    }

    private class langCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object event_type) {
            if (event_type.equals(pluginEnums.eLangManager.M_UPDATE_LOCAL)) {
                status.mSystemLocale = (Locale) data.get(0);
            }
            return null;
        }
    }

    /*Message Manager*/
    public void onMessageManagerInvoke(List<Object> pData, pluginEnums.eMessageManager pEventType) {
        if (mMessageManager != null) {
            mMessageManager.onTrigger(pData, pEventType);
        }
    }

    private class messageCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> pData, Object pEventType) {

            if (pEventType.equals(M_GET_BRIDGES)) {
                Intent myIntent = new Intent(mContextManager.getBridgeController(), unproxiedConnectionController.class);
                Bundle bundle = new Bundle();
                bundle.putString("m_url", CONST_BRIDGES);
                bundle.putBoolean("m_bridges", true);
                myIntent.putExtras(bundle);
                mContextManager.getBridgeController().startActivity(myIntent);
            }
            if (pEventType.equals(M_PANIC_RESET)) {
                if(activityContextManager.getInstance().getSettingController()!=null){
                    activityContextManager.getInstance().getSettingController().moveTaskToBack(true);
                }
                helperMethod.onDelayHandler(mHomeController.get(), 150, () -> {
                    activityContextManager.getInstance().getHomeController().panicExitInvoked();
                    return null;
                });
            } else if (pEventType.equals(M_DOWNLOAD_SINGLE) || pEventType.equals(M_DOWNLOAD_FILE) || pEventType.equals(M_DOWNLOAD_FILE_MANUAL)) {
                permissionHandler.getInstance().checkPermission((Callable<Void>) () -> {
                    if(pEventType.equals(M_DOWNLOAD_SINGLE)){
                        if (pData != null) {
                            if (pData.size() < 3) {
                                ((homeController) mHomeController.get()).onManualDownload(pData.get(0).toString());
                            } else {
                                if (pData.get(2).toString().startsWith("https://data") || pData.get(2).toString().startsWith("http://data")) {
                                    ((homeController) mHomeController.get()).onManualDownload(pData.get(2).toString().replace("https://", "").replace("http://", ""));
                                } else {
                                    ((homeController) mHomeController.get()).onManualDownloadFileName(pData.get(2).toString(), (String) pData.get(0));
                                }
                            }
                        }
                    }
                    if(pEventType.equals(M_DOWNLOAD_FILE)){
                        ((homeController) mHomeController.get()).onDownloadFile();
                    }
                    if(pEventType.equals(M_DOWNLOAD_FILE_MANUAL)){
                        ((homeController) mHomeController.get()).onManualDownload(pData.get(0).toString());
                    }
                    return null;
                });

            } else if (pEventType.equals(M_CANCEL_WELCOME)) {
                status.sSettingIsWelcomeEnabled = false;
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED, false));
            } else if (pEventType.equals(M_OPEN_PRIVACY)) {
                helperMethod.openActivity(settingPrivacyController.class, constants.CONST_LIST_HISTORY, mHomeController.get(), true);
            } else if (pEventType.equals(M_CLEAR_BOOKMARK)) {
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK, pData);
                mContextManager.getBookmarkController().onclearData();
            } else if (pEventType.equals(M_CLEAR_HISTORY)) {
                dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY, pData);
                mContextManager.getHistoryController().onclearData();
            } else if (pEventType.equals(M_BOOKMARK)) {
                String[] dataParser = pData.get(0).toString().split("split");
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_ADD_BOOKMARK, Arrays.asList(dataParser[0], pData.get(1).toString()));
            } else if (pEventType.equals(M_APP_RATED)) {
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED, true));
            } else if (pEventType.equals(M_CUSTOM_BRIDGE)) {
                return status.sBridgeCustomBridge;
            } else if (pEventType.equals(M_OPEN_LOGS)) {
                helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, mHomeController.get(), true);
            } else if (pEventType.equals(M_BRIDGE_TYPE)) {
                return status.sBridgeCustomType;
            } else if (pEventType.equals(ON_FETCH_FAVICON)) {
                activityContextManager.getInstance().getHomeController().onGetFavIcon((ImageView) pData.get(0), (String) pData.get(1));
            } else if (pEventType.equals(M_LOAD_NEW_TAB)) {
                ((homeController) mHomeController.get()).onLoadTabHidden(false, false, true, true);
            } else if (pEventType.equals(M_OPEN_LINK_NEW_TAB)) {

                ((homeController) mHomeController.get()).postNewLinkTabAnimationInBackgroundTrigger(pData.get(0).toString());
            } else if (pEventType.equals(M_OPEN_LINK_CURRENT_TAB)) {
                ((homeController) mHomeController.get()).onLoadURL(pData.get(0).toString());
            } else if (pEventType.equals(M_COPY_LINK)) {
                helperMethod.copyURL(pData.get(0).toString(), mContextManager.getHomeController());
            } else if (pEventType.equals(M_CLEAR_TAB)) {
                dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
                ((homeController) mHomeController.get()).initTab(true);
                ((homeController) mHomeController.get()).onDisableTabViewController();
            } else if (pEventType.equals(M_REQUEST_BRIDGES)) {
                pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(constants.CONST_BACKEND_GOOGLE_URL, this), M_BRIDGE_MAIL);
            } else if (pEventType.equals(M_SET_BRIDGES)) {
                activityContextManager.getInstance().getBridgeController().onUpdateBridges((String) pData.get(0), (String) pData.get(1));
            } else if (pEventType.equals(M_UNDO_TAB)) {
                activityContextManager.getInstance().getTabController().onRestoreTab(null);
            } else if (pEventType.equals(M_SECURITY_INFO)) {
                mMessageManager.onTrigger(Arrays.asList(activityContextManager.getInstance().getHomeController().getSecurityInfo(), mHomeController.get()), M_SECURITY_INFO);
            } else if (pEventType.equals(M_ADJUST_INPUT_RESIZE)) {
                mHomeController.get().runOnUiThread(() -> mHomeController.get().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE));
            } else if (pEventType.equals(M_IMAGE_UPDATE_RESTART)) {
                helperMethod.onDelayHandler(mHomeController.get(), 250, () -> {
                    onOrbotInvoke(Collections.singletonList(status.mThemeApplying), pluginEnums.eOrbotManager.M_DESTROY);
                    helperMethod.restartAndOpen(false);
                    return null;
                });
            } else if (pEventType.equals(M_TOR_SWITCH_RESTART)) {
                activityContextManager.getInstance().getHomeController().torSwitch();
                helperMethod.onDelayHandler(mHomeController.get(), 250, () -> {
                    onOrbotInvoke(Collections.singletonList(status.mThemeApplying), pluginEnums.eOrbotManager.M_DESTROY);
                    helperMethod.restartAndOpen(false);
                    return null;
                });
                // mHomeController.get().finish();
            }
            return null;
        }
    }
}