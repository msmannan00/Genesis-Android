package com.darkweb.genesissearchengine.dataManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.status.mThemeApplying;

public class dataController
{
    /*Private Variables*/

    private tabDataModel mTabModel;
    private preferenceDataModel mPreferenceModel;
    private historyDataModel mHistoryModel;
    private imageDataModel mImageDataModel;
    private bookmarkDataModel mBookmarkDataModel;
    private suggestionDataModel mSuggestionDataModel;
    private helpDataModel mHelpDataModel;
    private referenceWebsiteDataModel mReferenceWebsiteDataModel;

    /*Private Declarations*/

    private static final dataController sOurInstance = new dataController();
    public static dataController getInstance()
    {
        return sOurInstance;
    }

    /*Initializations*/

    public void initialize(AppCompatActivity pAppContext){
        mHistoryModel = new historyDataModel();
        mTabModel = new tabDataModel();
        mPreferenceModel = new preferenceDataModel(pAppContext);
        mImageDataModel = new imageDataModel();
        mBookmarkDataModel = new bookmarkDataModel();
        mSuggestionDataModel = new suggestionDataModel(pAppContext);
        mHelpDataModel = new helpDataModel();
        mReferenceWebsiteDataModel = new referenceWebsiteDataModel();
    }
    public void initializeListData(){
        mReferenceWebsiteDataModel.onTrigger(dataEnums.eReferenceWebsiteCommands.M_LOAD,Collections.singletonList(activityContextManager.getInstance().getHomeController()));
        mBookmarkDataModel.initializebookmark(databaseController.getInstance().selectBookmark());
        if(!status.sClearOnExit)
        {
            mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY, Arrays.asList(databaseController.getInstance().selectHistory(0,constants.CONST_FETCHABLE_LIST_SIZE), databaseController.getInstance().getLargestHistoryID(),databaseController.getInstance().getLargestHistoryID()));
        }
        else {
            databaseController.getInstance().execSQL("delete from history where 1",null);
        }
        if(status.sRestoreTabs || mThemeApplying){
            mTabModel.initializeTab(databaseController.getInstance().selectTabs());
            activityContextManager.getInstance().getHomeController().initTabCount();
        }else{
            invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
        }
    }

    /*Recieving History*/
    public Object invokeHistory(dataEnums.eHistoryCommands pCommands, List<Object> pData){

        if(pCommands == dataEnums.eHistoryCommands.M_ADD_HISTORY){
            mTabModel.onTrigger(dataEnums.eTabCommands.M_UPDATE_TAB, pData);
        }

        if(pCommands.equals(dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY)){
            int m_history_size = (int) mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_HISTORY_SIZE,null) - 1;
            return mHistoryModel.onTrigger(pCommands, Collections.singletonList(databaseController.getInstance().selectHistory(m_history_size+1,constants.CONST_FETCHABLE_LIST_SIZE)));
        }else {
            return mHistoryModel.onTrigger(pCommands, pData);
        }
    }

    public Object invokeSuggestions(dataEnums.eSuggestionCommands pCommands, List<Object> pData){
        return mSuggestionDataModel.onTrigger(pCommands, Arrays.asList(pData.get(0), mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_GET_HISTORY, null), mBookmarkDataModel.onTrigger(dataEnums.eBookmarkCommands.M_GET_BOOKMARK, null)));
    }

    public Object invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands pCommands, List<Object> pData){
        return mReferenceWebsiteDataModel.onTrigger(pCommands, null);
    }

    /*Recieving Preferences*/
    public Object invokePrefs(dataEnums.ePreferencesCommands pCommands, List<Object> pData){
        return mPreferenceModel.onTrigger(pCommands, pData);
    }

    /*Recieving Help*/
    public Object invokeHelp(dataEnums.eHelpCommands pCommands, List<Object> pData){
        return mHelpDataModel.onTrigger(pCommands, pData);
    }

    /*Recieving History*/
    public Object invokeBookmark(dataEnums.eBookmarkCommands pCommands, List<Object> pData){
        return mBookmarkDataModel.onTrigger(pCommands, pData);
    }

       public Object invokeTab(dataEnums.eTabCommands pCommands, List<Object> pData){
        return mTabModel.onTrigger(pCommands, pData);
    }

    public Object invokeImage(dataEnums.eImageCommands pCommands, List<Object> pData){
        return mImageDataModel.onTrigger(pCommands, pData);
    }
}

