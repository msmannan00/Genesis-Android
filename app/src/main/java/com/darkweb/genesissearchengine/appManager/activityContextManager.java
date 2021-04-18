package com.darkweb.genesissearchengine.appManager;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.bridgeManager.bridgeController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController.homeController;
import com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogController;
import com.darkweb.genesissearchengine.appManager.settingManager.generalManager.settingGeneralController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingHomeController;
import com.darkweb.genesissearchengine.appManager.tabManager.tabController;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class activityContextManager
{
    /*Private Variables*/

    private static activityContextManager ourInstance = new activityContextManager();
    public static activityContextManager getInstance()
    {
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

    private activityContextManager()
    {
        mStackList = new ArrayList<>();
    }

    /*List ContextGetterSetters*/
    public historyController getHistoryController(){
        if(pHistoryController==null){
            return null;
        }
        return pHistoryController.get();
    }
    public void setHistoryController(historyController history_controller){
        this.pHistoryController = new WeakReference(history_controller);
    }

    public bookmarkController getBookmarkController(){
        if(pBookmarkController==null){
            return null;
        }
        return pBookmarkController.get();
    }
    public void setBookmarkController(bookmarkController bookmark_controller){
        this.pBookmarkController = new WeakReference(bookmark_controller);
    }

    public bridgeController getBridgeController(){
        if(pBridgeController==null){
            return null;
        }
        return pBridgeController.get();
    }
    public void setBridgeController(bridgeController bridge_controller){
        this.pBridgeController = new WeakReference(bridge_controller);
    }

    public homeController getHomeController(){
        if(pHomeController==null){
            return null;
        }
        return pHomeController.get();
    }

    public void setApplicationContext(Context pContext){
        this.pApplicationContext = new WeakReference(pContext);
    }

    public Context getApplicationController(){
        return pApplicationContext.get();
    }

    public void setHomeController(homeController home_controller){
        this.pHomeController = new WeakReference(home_controller);
    }

    public tabController getTabController(){
        if(pTabController==null){
            return null;
        }
        return pTabController.get();
    }
    public void setTabController(tabController tab_controller){
        this.pTabController = new WeakReference(tab_controller);
    }

    public orbotLogController getOrbotLogController(){
        if(pOrbotLogController==null){
            return null;
        }
        return pOrbotLogController.get();
    }
    public void setOrbotLogController(orbotLogController pOrbotLogController){
        this.pOrbotLogController = new WeakReference(pOrbotLogController);
    }


    public settingGeneralController getSettingGeneralController(){
        if(pSettingGeneralController==null){
            return null;
        }
        return pSettingGeneralController.get();
    }
    public void setSettingGeneralController(settingGeneralController pSettingGeneralController){
        this.pSettingGeneralController = new WeakReference(pSettingGeneralController);
    }

    public settingHomeController getSettingController(){
        if(pSettingController==null){
            return null;
        }
        return pSettingController.get();
    }
    public void setSettingController(settingHomeController pSettingController){
        this.pSettingController = new WeakReference(pSettingController);
    }

    public void setCurrentActivity(android.app.Activity pCurrentActivity){
        this.pCurrentActivity = new WeakReference(pCurrentActivity);
    }
    public android.app.Activity getCurrentActivity(){
        if(pCurrentActivity==null){
            return null;
        }
        return pCurrentActivity.get();
    }

    public void onStack(AppCompatActivity pActivity) {
        try{
            if (mStackList.size() > 0) {
                if (!mStackList.get(mStackList.size() - 1).get().getLocalClassName().equals(pActivity.getLocalClassName())) {
                    mStackList.add(new WeakReference(pActivity));
                }
            }else {
                mStackList.add(new WeakReference(pActivity));
            }
        }catch (Exception ignored){}
    }

    public void onRemoveStack(AppCompatActivity pActivity){
        try{
            for(int mCounter=0;mCounter<mStackList.size();mCounter++){
                if(mStackList.get(mCounter).get().getLocalClassName().equals(pActivity.getLocalClassName())){
                    mStackList.remove(mCounter);
                    mCounter-=1;
                }
            }
        }catch (Exception ignored){}
    }

    public void onClearStack(){
        for(int mCounter=0;mCounter<mStackList.size();mCounter++){
            try{
                if(!mStackList.get(mCounter).get().isFinishing()){
                    mStackList.get(mCounter).get().finish();
                    mStackList.remove(mCounter);
                    mCounter-=1;
                }
            }catch (Exception ignored){}
        }
    }
}
