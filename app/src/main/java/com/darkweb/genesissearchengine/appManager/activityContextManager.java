package com.darkweb.genesissearchengine.appManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.homeManager.homeController;
import com.darkweb.genesissearchengine.appManager.orbotLogManager.orbotLogController;
import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingController;
import com.darkweb.genesissearchengine.appManager.tabManager.tabController;
import java.util.ArrayList;

public class activityContextManager
{
    /*Private Variables*/

    private static final activityContextManager ourInstance = new activityContextManager();
    public static activityContextManager getInstance()
    {
        return ourInstance;
    }

    /*Private Contexts*/
    private historyController pHistoryController;
    private bookmarkController pBookmarkController;
    private homeController pHomeController;
    private tabController pTabController;
    private android.app.Activity pCurrentActivity = null;
    private settingController pSettingController;
    private orbotLogController pOrbotLogController;
    private ArrayList<AppCompatActivity> mStackList;

    /*Initialization*/

    private activityContextManager()
    {
        mStackList = new ArrayList<>();
    }

    /*List ContextGetterSetters*/
    public historyController getHistoryController(){
        return pHistoryController;
    }
    public void setHistoryController(historyController history_controller){
        this.pHistoryController = history_controller;
    }

    public bookmarkController getBookmarkController(){
        return pBookmarkController;
    }
    public void setBookmarkController(bookmarkController bookmark_controller){
        this.pBookmarkController = bookmark_controller;
    }

    public homeController getHomeController(){
        return pHomeController;
    }
    public void setHomeController(homeController home_controller){
        this.pHomeController = home_controller;
    }

    public tabController getTabController(){
        return pTabController;
    }
    public void setTabController(tabController tab_controller){
        this.pTabController = tab_controller;
    }

    public orbotLogController getOrbotLogController(){
        return pOrbotLogController;
    }
    public void setOrbotLogController(orbotLogController pOrbotLogController){
        this.pOrbotLogController = pOrbotLogController;
    }

    public settingController getSettingController(){
        return pSettingController;
    }
    public void setSettingController(settingController pSettingController){
        this.pSettingController = pSettingController;
    }

    public void setCurrentActivity(android.app.Activity pCurrentActivity){
        this.pCurrentActivity = pCurrentActivity;
    }
    public android.app.Activity getCurrentActivity(){
        return pCurrentActivity;
    }

    public void onStack(AppCompatActivity pActivity){
        mStackList.add(pActivity);
    }

    public void onRemoveStack(AppCompatActivity pActivity){
        if(mStackList.size()>0 && mStackList.get(mStackList.size()-1).equals(pActivity)){
            mStackList.remove(mStackList.size()-1);
        }
    }

    public void onClearStack(){
        for(int mCounter=0;mCounter<mStackList.size();mCounter++){
            if(!mStackList.get(mCounter).isFinishing()){
                mStackList.get(mCounter).finish();
                mStackList.remove(mCounter);
            }
        }
    }
}
