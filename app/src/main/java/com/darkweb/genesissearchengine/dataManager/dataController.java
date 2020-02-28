package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.appManager.homeManager.geckoSession;
import com.darkweb.genesissearchengine.appManager.tabManager.tabRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.helperMethod;


import java.util.ArrayList;

public class dataController
{
    /*Private Variables*/

    private dataModel mPreferencesModel;
    private historyController mHistoryController;

    /*Private Declarations*/

    private static final dataController sOurInstance = new dataController();
    public static dataController getInstance()
    {
        return sOurInstance;
    }

    /*Initializations*/

    public void initialize(AppCompatActivity app_context){
        mPreferencesModel = new dataModel(app_context);
        mPreferencesModel.initializeBookmarks();
        mPreferencesModel.setMaxHistoryID(databaseController.getInstance().getLargestHistoryID());
        mPreferencesModel.setHistorySize(databaseController.getInstance().getLargestHistoryID());
    }
    public void initializeListData(){
        mPreferencesModel.initSuggestions();
        if(!status.sHistoryStatus)
        {
            mPreferencesModel.initializeHistory(databaseController.getInstance().selectHistory(0,constants.START_LIST_SIZE));
        }
        else
        {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
    }

    /*Saving Preferences*/

    public void setString(String valueKey, String value){
        mPreferencesModel.setString(valueKey, value);
    }
    public void setBool(String valueKey, boolean value){
        if(mPreferencesModel!=null){
            mPreferencesModel.setBool(valueKey, value);
        }
    }
    public void setInt(String valueKey, int value)
    {
        mPreferencesModel.setInt(valueKey, value);
    }

    /*Recieving Preferences*/

    public String getString(String valueKey, String valueDefault){
        return mPreferencesModel.getString(valueKey, valueDefault);
    }
    public boolean getBool(String valueKey, boolean valueDefault){
        return mPreferencesModel.getBool(valueKey, valueDefault);
    }
    public int getInt(String valueKey, int valueDefault){
        return mPreferencesModel.getInt(valueKey, valueDefault);
    }
    public float getFloat(String valueKey, int valueDefault){
        return mPreferencesModel.getFloat(valueKey, valueDefault);
    }

    public void clearAllPrefs(){
        mPreferencesModel.clearPrefs();
    }
    /*Recieving History*/

    public ArrayList<historyRowModel> getHistory() {
        return mPreferencesModel.getmHistory();
    }
    public void addHistory(String url,String title) {
        mPreferencesModel.addHistory(url);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void updateSuggestionURL(String url,String title) {
        url = helperMethod.removeLastSlash(url);
        mPreferencesModel.updateSuggestionURL(url,title,false);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void addSuggesion(String url,String title) {
        url = helperMethod.removeLastSlash(url);
        mPreferencesModel.addSuggenstions(url,title,false);
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void removeHistory(String url){
        mPreferencesModel.removeHistory(url);
    }
    public void clearHistory(){
        mPreferencesModel.clearHistory();
        activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
    }
    public void loadMoreHistory(){
        ArrayList<historyRowModel> history = databaseController.getInstance().selectHistory(mPreferencesModel.getmHistory().size()-1,constants.MAX_LIST_SIZE);
        if(history.size()>0){
            mPreferencesModel.loadMoreHistory(history);
        }
        activityContextManager.getInstance().getHistoryController().updateHistory();
    }

    /*Recieving Bookmarks*/

    public ArrayList<bookmarkRowModel> getBookmark(){
        return mPreferencesModel.getBookmark();
    }
    public void addBookmark(String url,String title){
        mPreferencesModel.addBookmark(url,title);
    }
    public void clearBookmark(){
        mPreferencesModel.clearBookmark();
    }

    /*Recieving Suggestions*/

    public ArrayList<historyRowModel> getSuggestions(){
        return mPreferencesModel.getmSuggestions();
    }
    public void clearSuggestions(){
        mPreferencesModel.clearSuggestion();
    }

    /*Recieving Tabs*/

    public ArrayList<tabRowModel> getTab(){
        return mPreferencesModel.getTab();
    }
    public void addTab(geckoSession mSession,boolean isHardCopy){
        mPreferencesModel.addTabs(mSession,isHardCopy);
    }
    public void clearTabs(){
        mPreferencesModel.clearTab();
    }
    public void closeTab(geckoSession session){
        mPreferencesModel.closeTab(session);
    }
    public void moveTabToTop(geckoSession session){
        mPreferencesModel.moveTabToTop(session);
    }
    public tabRowModel getCurrentTab(){
        return mPreferencesModel.getCurrentTab();
    }
    public int getTotalTabs(){
        return mPreferencesModel.getTotalTabs();
    }
}
