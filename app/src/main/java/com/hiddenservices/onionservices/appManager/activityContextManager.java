package com.hiddenservices.onionservices.appManager;

import static com.hiddenservices.onionservices.constants.constants.CONST_PACKAGE_NAME;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.R;
import com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome.bookmarkController;
import com.hiddenservices.onionservices.appManager.bridgeManager.bridgeController;
import com.hiddenservices.onionservices.appManager.historyManager.historyController;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogController;
import com.hiddenservices.onionservices.appManager.settingManager.generalManager.settingGeneralController;
import com.hiddenservices.onionservices.appManager.settingManager.settingHomeManager.settingHomeController;
import com.hiddenservices.onionservices.appManager.tabManager.tabController;
import com.widget.onionservices.helperMethod.helperMethod;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class activityContextManager {
    /*Private Variables*/

    private static activityContextManager ourInstance = new activityContextManager();

    public static activityContextManager getInstance() {
        return ourInstance;
    }

    /*Private Contexts*/
    private WeakReference<bridgeController> pBridgeController;
    private WeakReference<historyController> pHistoryController;
    private WeakReference<bookmarkController> pBookmarkController;
    private WeakReference<homeController> pHomeController;
    private WeakReference<tabController> pTabController;
    private WeakReference<android.app.Activity> pCurrentActivity = null;
    private WeakReference<settingHomeController> pSettingController;
    private WeakReference<settingGeneralController> pSettingGeneralController;
    private WeakReference<orbotLogController> pOrbotLogController;
    private WeakReference<Context> pApplicationContext;
    private ArrayList<WeakReference<AppCompatActivity>> mStackList;

    /*Initialization*/

    private activityContextManager() {
        mStackList = new ArrayList<>();
    }

    /*List ContextGetterSetters*/
    public historyController getHistoryController() {
        if (pHistoryController == null) {
            return null;
        }
        return pHistoryController.get();
    }

    public void setHistoryController(historyController history_controller) {
        this.pHistoryController = new WeakReference(history_controller);
    }

    public bookmarkController getBookmarkController() {
        if (pBookmarkController == null) {
            return null;
        }
        return pBookmarkController.get();
    }

    public void setBookmarkController(bookmarkController bookmark_controller) {
        this.pBookmarkController = new WeakReference(bookmark_controller);
    }

    public bridgeController getBridgeController() {
        if (pBridgeController == null) {
            return null;
        }
        return pBridgeController.get();
    }

    public void setBridgeController(bridgeController bridge_controller) {
        this.pBridgeController = new WeakReference(bridge_controller);
    }

    public homeController getHomeController() {
        try {
            if (pHomeController == null) {
                if(pApplicationContext == null){
                    return null;
                }
                helperMethod.onStartApplication(pApplicationContext.get(), CONST_PACKAGE_NAME);
            }
            return pHomeController.get();
        }catch (Exception ex){
            return null;
        }
    }

    public void setApplicationContext(Context pContext) {
        this.pApplicationContext = new WeakReference(pContext);
    }

    public Context getApplicationController() {
        return pApplicationContext.get();
    }

    public void setHomeController(homeController home_controller) {
        this.pHomeController = new WeakReference(home_controller);
    }

    public tabController getTabController() {
        if (pTabController == null) {
            return null;
        }
        return pTabController.get();
    }

    public void setTabController(tabController tab_controller) {
        this.pTabController = new WeakReference(tab_controller);
    }

    public orbotLogController getOrbotLogController() {
        if (pOrbotLogController == null) {
            return null;
        }
        return pOrbotLogController.get();
    }

    public void setOrbotLogController(orbotLogController pOrbotLogController) {
        this.pOrbotLogController = new WeakReference(pOrbotLogController);
    }


    public settingGeneralController getSettingGeneralController() {
        if (pSettingGeneralController == null) {
            return null;
        }
        return pSettingGeneralController.get();
    }

    public void setSettingGeneralController(settingGeneralController pSettingGeneralController) {
        this.pSettingGeneralController = new WeakReference(pSettingGeneralController);
    }

    public settingHomeController getSettingController() {
        if (pSettingController == null) {
            return null;
        }
        return pSettingController.get();
    }

    public void setSettingController(settingHomeController pSettingController) {
        this.pSettingController = new WeakReference(pSettingController);
    }

    public void setCurrentActivity(android.app.Activity pCurrentActivity) {
        this.pCurrentActivity = new WeakReference(pCurrentActivity);
    }

    public android.app.Activity getCurrentActivity() {
        if (pCurrentActivity == null) {
            return null;
        }
        return pCurrentActivity.get();
    }

    public void onStack(AppCompatActivity pActivity) {
        try {
            if (mStackList.size() > 0) {
                if (!mStackList.get(mStackList.size() - 1).get().getLocalClassName().equals(pActivity.getLocalClassName())) {
                    mStackList.add(new WeakReference(pActivity));
                }
            } else {
                mStackList.add(new WeakReference(pActivity));
            }
        } catch (Exception ignored) {
        }
    }

    public void onRemoveStack(AppCompatActivity pActivity) {
        try {
            for (int mCounter = 0; mCounter < mStackList.size(); mCounter++) {
                if (mStackList.get(mCounter).get().getLocalClassName().equals(pActivity.getLocalClassName())) {
                    mStackList.remove(mCounter);
                    mCounter -= 1;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void onResetTheme() {
        for (int mCounter = 0; mCounter < mStackList.size(); mCounter++) {
            try {
                if (!mStackList.get(mCounter).get().isFinishing()) {
                    activityThemeManager.getInstance().onConfigurationChanged(mStackList.get(mCounter).get());
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void onResetLanguage() {
        for (int mCounter = 0; mCounter < mStackList.size(); mCounter++) {
            try {
                if (!mStackList.get(mCounter).get().isFinishing()) {
                    activityThemeManager.getInstance().onConfigurationChanged(mStackList.get(mCounter).get());
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void onGoHome() {
        for (int mCounter = 0; mCounter < mStackList.size(); mCounter++) {
            try {
                if (!mStackList.get(mCounter).get().isFinishing()) {
                    mStackList.get(mCounter).get().finish();
                    mStackList.remove(mCounter);
                    mCounter -= 1;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void onCheckPurgeStack() {
        if(pHomeController==null || pHomeController.get() == null){
            for (int mCounter = 0; mCounter < mStackList.size(); mCounter++) {
                try {
                    if (!mStackList.get(mCounter).get().isFinishing()) {
                        mStackList.get(mCounter).get().finish();
                        mStackList.get(mCounter).get().overridePendingTransition(R.anim.translate_fade_left_crash, R.anim.translate_fade_right_crash);
                    }
                    mStackList.remove(mCounter);
                    mCounter -= 1;

                } catch (Exception ignored) {
                }
            }
        }
    }
}
