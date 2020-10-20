package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class dataController
{
    /*Private Variables*/

    private dataModel mDataModel;
    private preferenceDataModel mPreferenceModel;
    private historyDataModel mHistoryModel;
    private imageCacheModel mImageCacheModel;
    private bookmarkDataModel mBookMarkDataModel;

    /*Private Declarations*/

    private static final dataController sOurInstance = new dataController();
    public static dataController getInstance()
    {
        return sOurInstance;
    }

    /*Initializations*/

    public void initialize(AppCompatActivity app_context){
        mHistoryModel = new historyDataModel();
        mDataModel = new dataModel();
        mPreferenceModel = new preferenceDataModel(app_context);
        mImageCacheModel = new imageCacheModel();
        mBookMarkDataModel = new bookmarkDataModel();
    }
    public void initializeListData(){
        mDataModel.initSuggestions();
        mBookMarkDataModel.initializebookmark(databaseController.getInstance().selectBookmark());
        if(!status.sSettingHistoryStatus)
        {
            mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY, Arrays.asList(databaseController.getInstance().selectHistory(0,constants.CONST_FETCHABLE_LIST_SIZE), databaseController.getInstance().getLargestHistoryID(),databaseController.getInstance().getLargestHistoryID()));
        }
        else
        {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
    }

    /*Recieving History*/
    public Object invokeHistory(dataEnums.eHistoryCommands p_commands, List<Object> p_data){
        if(p_commands.equals(dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY)){
            int m_history_size = (int) mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_HISTORY_SIZE,null);
            return mHistoryModel.onTrigger(p_commands, Collections.singletonList(databaseController.getInstance().selectHistory(m_history_size+1,constants.CONST_FETCHABLE_LIST_SIZE)));
        }else {
            return mHistoryModel.onTrigger(p_commands, p_data);
        }
    }

    /*Recieving Images*/
    public Object invokeImageCache(dataEnums.eImageCacheCommands p_commands, List<Object> p_data){
        return mImageCacheModel.onTrigger(p_commands, p_data);
    }

    /*Recieving Preferences*/
    public Object invokePrefs(dataEnums.ePreferencesCommands p_commands, List<Object> p_data){
        return mPreferenceModel.onTrigger(p_commands, p_data);
    }


    /*Recieving History*/
    public Object invokeBookmark(dataEnums.eBookmarkCommands p_commands, List<Object> p_data){
        return mBookMarkDataModel.onTrigger(p_commands, p_data);
    }



















    /*Recieving Suggestions*/

    public ArrayList<historyRowModel> getSuggestions(){
        return mDataModel.getmSuggestions();
    }

    /*Recieving Tabs*/

    public ArrayList<tabRowModel> getTab(){
        return mDataModel.getTab();
    }
    public void addTab(geckoSession mSession,boolean isHardCopy){
        mDataModel.addTabs(mSession,isHardCopy);
    }
    public void clearTabs(){
        mDataModel.clearTab();
    }
    public void closeTab(geckoSession session){
        mDataModel.closeTab(session);
    }
    public void moveTabToTop(geckoSession session){
        mDataModel.moveTabToTop(session);
    }
    public tabRowModel getCurrentTab(){
        return mDataModel.getCurrentTab();
    }
    public int getTotalTabs(){
        return mDataModel.getTotalTabs();
    }

    public void updateSuggestionURL(String url,String title) {
        url = helperMethod.removeLastSlash(url);
        mDataModel.updateSuggestionURL(url,title,false);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void addSuggesion(String url,String title) {
        url = helperMethod.removeLastSlash(url);
        mDataModel.addSuggenstions(url,title,false);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }


}

