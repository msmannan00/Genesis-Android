package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class dataController
{
    /*Private Variables*/

    private tabDataModel mTabModel;
    private preferenceDataModel mPreferenceModel;
    private historyDataModel mHistoryModel;
    private imageCacheModel mImageCacheModel;
    private bookmarkDataModel mBookMarkDataModel;
    private suggestionDataModel mSuggestionDataModel;

    /*Private Declarations*/

    private static final dataController sOurInstance = new dataController();
    public static dataController getInstance()
    {
        return sOurInstance;
    }

    /*Initializations*/

    public void initialize(AppCompatActivity app_context){
        mHistoryModel = new historyDataModel();
        mTabModel = new tabDataModel();
        mPreferenceModel = new preferenceDataModel(app_context);
        mImageCacheModel = new imageCacheModel();
        mBookMarkDataModel = new bookmarkDataModel();
        mSuggestionDataModel = new suggestionDataModel();
    }
    public void initializeListData(){
        invokeSuggestion(dataEnums.eSuggestionCommands.M_INIT_SUGGESTION, null);
        mBookMarkDataModel.initializebookmark(databaseController.getInstance().selectBookmark());
        if(!status.sClearOnExit)
        {
            mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY, Arrays.asList(databaseController.getInstance().selectHistory(0,constants.CONST_FETCHABLE_LIST_SIZE), databaseController.getInstance().getLargestHistoryID(),databaseController.getInstance().getLargestHistoryID()));
        }
        else
        {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
        if(status.sRestoreTabs){
            mTabModel.initializeTab(databaseController.getInstance().selectTabs());
            activityContextManager.getInstance().getHomeController().initTabCount();
        }else{
            invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
        }
    }

    /*Recieving History*/
    public Object invokeHistory(dataEnums.eHistoryCommands p_commands, List<Object> p_data){

        if(p_commands == dataEnums.eHistoryCommands.M_ADD_HISTORY){
            mTabModel.onTrigger(dataEnums.eTabCommands.M_UPDATE_TAB, p_data);
        }

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

    public Object invokeSuggestion(dataEnums.eSuggestionCommands p_commands, List<Object> p_data){
        if(dataEnums.eSuggestionCommands.M_UPDATE_SUGGESTION.equals(p_commands)){
            mSuggestionDataModel.onTrigger(p_commands, p_data);
            activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
        }
        else if(dataEnums.eSuggestionCommands.M_ADD_SUGGESTION.equals(p_commands)){
            mSuggestionDataModel.onTrigger(p_commands, p_data);
            activityContextManager.getInstance().getHomeController().onSuggestionUpdate();
        }else {
            return mSuggestionDataModel.onTrigger(p_commands, p_data);
        }
        return null;
    }

    public Object invokeTab(dataEnums.eTabCommands p_commands, List<Object> p_data){
        return mTabModel.onTrigger(p_commands, p_data);
    }
}

