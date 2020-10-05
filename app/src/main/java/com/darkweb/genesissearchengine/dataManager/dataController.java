package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
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

    private dataModel m_data_model;
    private preferenceDataModel m_preference_model;
    private historyDataModel m_history_model;
    private imageCacheModel m_image_cache_model;

    /*Private Declarations*/

    private static final dataController sOurInstance = new dataController();
    public static dataController getInstance()
    {
        return sOurInstance;
    }

    /*Initializations*/

    public void initialize(AppCompatActivity app_context){
        m_history_model = new historyDataModel();
        m_data_model = new dataModel();
        m_preference_model = new preferenceDataModel(app_context);
        m_image_cache_model = new imageCacheModel();
        m_data_model.initializeBookmarks();
    }
    public void initializeListData(){
        m_data_model.initSuggestions();
        if(!status.sHistoryStatus)
        {
            m_history_model.onTrigger(dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY, Arrays.asList(databaseController.getInstance().selectHistory(0,constants.FETCHABLE_LIST_SIZE), databaseController.getInstance().getLargestHistoryID(),databaseController.getInstance().getLargestHistoryID()));
        }
        else
        {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
    }

    /*Recieving History*/
    public Object invokeHistory(dataEnums.eHistoryCommands p_commands, List<Object> p_data){
        if(p_commands.equals(dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY)){
            int m_history_size = (int)m_history_model.onTrigger(dataEnums.eHistoryCommands.M_HISTORY_SIZE,null);
            return m_history_model.onTrigger(p_commands, Collections.singletonList(databaseController.getInstance().selectHistory(m_history_size+1,constants.FETCHABLE_LIST_SIZE)));
        }else {
            return m_history_model.onTrigger(p_commands, p_data);
        }
    }

    /*Recieving Images*/
    public Object invokeImageCache(dataEnums.eImageCacheCommands p_commands, List<Object> p_data){
        return m_image_cache_model.onTrigger(p_commands, p_data);
    }

    /*Recieving Preferences*/
    public Object invokePrefs(dataEnums.ePreferencesCommands p_commands, List<Object> p_data){
        return m_preference_model.onTrigger(p_commands, p_data);
    }





















    /*Recieving Bookmarks*/

    public ArrayList<bookmarkRowModel> getBookmark(){
        return m_data_model.getBookmark();
    }
    public void addBookmark(String url,String title){
        m_data_model.addBookmark(url,title);
    }
    public void clearBookmark(){
        m_data_model.clearBookmark();
    }

    /*Recieving Suggestions*/

    public ArrayList<historyRowModel> getSuggestions(){
        return m_data_model.getmSuggestions();
    }

    /*Recieving Tabs*/

    public ArrayList<tabRowModel> getTab(){
        return m_data_model.getTab();
    }
    public void addTab(geckoSession mSession,boolean isHardCopy){
        m_data_model.addTabs(mSession,isHardCopy);
    }
    public void clearTabs(){
        m_data_model.clearTab();
    }
    public void closeTab(geckoSession session){
        m_data_model.closeTab(session);
    }
    public void moveTabToTop(geckoSession session){
        m_data_model.moveTabToTop(session);
    }
    public tabRowModel getCurrentTab(){
        return m_data_model.getCurrentTab();
    }
    public int getTotalTabs(){
        return m_data_model.getTotalTabs();
    }

    public void updateSuggestionURL(String url,String title) {
        url = helperMethod.removeLastSlash(url);
        m_data_model.updateSuggestionURL(url,title,false);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void addSuggesion(String url,String title) {
        url = helperMethod.removeLastSlash(url);
        m_data_model.addSuggenstions(url,title,false);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }


}

