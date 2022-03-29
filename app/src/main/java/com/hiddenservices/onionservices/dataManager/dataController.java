package com.hiddenservices.onionservices.dataManager;

import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.dataManager.models.bookmarkRowModel;
import com.hiddenservices.onionservices.dataManager.models.tabRowModel;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;

import org.mozilla.geckoview.ContentBlocking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.hiddenservices.onionservices.constants.sql.*;
import static com.hiddenservices.onionservices.constants.status.mThemeApplying;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;

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
    private bridgesDataModel mBridgeWebsiteDataModel;
    private sqlCipherDataModel mSqlCipherDataModel;
    private crawlerDataModel mCrawlerDataModel;

    /*Private Declarations*/

    private static final dataController sOurInstance = new dataController();
    public static dataController getInstance()
    {
        return sOurInstance;
    }

    /*Initializations*/

    public void initialize(AppCompatActivity pAppContext){
        mHistoryModel = new historyDataModel(new invokeHistoryCallbacks());
        mTabModel = new tabDataModel(new invokeTabCallbacks());
        mPreferenceModel = new preferenceDataModel(pAppContext);
        mImageDataModel = new imageDataModel();
        mBookmarkDataModel = new bookmarkDataModel(new invokeBookmarkCallbacks());
        mSuggestionDataModel = new suggestionDataModel(pAppContext);
        mHelpDataModel = new helpDataModel();
        mReferenceWebsiteDataModel = new referenceWebsiteDataModel();
        mBridgeWebsiteDataModel = new bridgesDataModel();
        mSqlCipherDataModel = new sqlCipherDataModel();
        mCrawlerDataModel = new crawlerDataModel(pAppContext);
    }
    public void initializeListData(){
        mReferenceWebsiteDataModel.onTrigger(dataEnums.eReferenceWebsiteCommands.M_LOAD,Collections.singletonList(activityContextManager.getInstance().getHomeController()));
        mBridgeWebsiteDataModel.onTrigger(dataEnums.eBridgeWebsiteCommands.M_LOAD,Collections.singletonList(activityContextManager.getInstance().getHomeController()));

        ArrayList<bookmarkRowModel> mBookmarks = (ArrayList<bookmarkRowModel>)invokeSQLCipher(dataEnums.eSqlCipherCommands.M_SELECT_BOOKMARK, null);
        mBookmarkDataModel.initializebookmark(mBookmarks);
        if(!status.sClearOnExit)
        {
            ArrayList<Object> mHistory = (ArrayList<Object>)invokeSQLCipher(dataEnums.eSqlCipherCommands.M_SELECT_HISTORY, Arrays.asList(0,constants.CONST_FETCHABLE_LIST_SIZE));
            int mHistoryID = (int)invokeSQLCipher(dataEnums.eSqlCipherCommands.M_HISTORY_ID, null);

            mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_INITIALIZE_HISTORY, Arrays.asList(mHistory, mHistoryID, mHistoryID));
        }
        else {
            invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList("delete from history where 1",null));
        }
        if(status.sRestoreTabs || mThemeApplying){
            ArrayList<tabRowModel> mTabs = (ArrayList<tabRowModel>)invokeSQLCipher(dataEnums.eSqlCipherCommands.M_SELECT_TABS, null);
            mTabModel.initializeTab(mTabs);
            activityContextManager.getInstance().getHomeController().initTabCountForced();
        }else{
            invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
        }
    }

    /*Trigger History*/

    public Object invokeHistory(dataEnums.eHistoryCommands pCommands, List<Object> pData){

        if(pCommands == dataEnums.eHistoryCommands.M_ADD_HISTORY){
            mTabModel.onTrigger(dataEnums.eTabCommands.M_UPDATE_TAB, pData);
        }

        if(pCommands.equals(dataEnums.eHistoryCommands.M_LOAD_MORE_HISTORY)){
            int m_history_size = (int) mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_HISTORY_SIZE,null) - 1;

            ArrayList<Object> mHistory = (ArrayList<Object>)invokeSQLCipher(dataEnums.eSqlCipherCommands.M_SELECT_HISTORY, Arrays.asList(m_history_size+1,constants.CONST_FETCHABLE_LIST_SIZE));
            return mHistoryModel.onTrigger(pCommands, Collections.singletonList(mHistory));
        }else {
            return mHistoryModel.onTrigger(pCommands, pData);
        }
    }

    private class invokeHistoryCallbacks implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(dataEnums.eHistoryCallbackCommands.M_EXEC_SQL)){
                invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, data);
            }
            return null;
        }
    }

    /*Trigger Crawler*/

    public Object invokeCrawler(dataEnums.eCrawlerCommands pCommands, List<Object> pData){
        return mCrawlerDataModel.onTrigger(pCommands, pData);
    }

    /*Trigger Suggestions*/

    public Object invokeSuggestions(dataEnums.eSuggestionCommands pCommands, List<Object> pData){
        return mSuggestionDataModel.onTrigger(pCommands, Arrays.asList(pData.get(0), mHistoryModel.onTrigger(dataEnums.eHistoryCommands.M_GET_HISTORY, null), mBookmarkDataModel.onTrigger(dataEnums.eBookmarkCommands.M_GET_BOOKMARK, null)));
    }

    /*Trigger Reference Websites*/

    public Object invokeReferenceWebsite(dataEnums.eReferenceWebsiteCommands pCommands, List<Object> pData){
        return mReferenceWebsiteDataModel.onTrigger(pCommands, null);
    }

    /*Trigger Bridges*/

    public Object invokeBridges(dataEnums.eBridgeWebsiteCommands pCommands, List<Object> pData){
        return mBridgeWebsiteDataModel.onTrigger(pCommands, null);
    }

    /*Trigger Preferences*/

    public Object invokePrefs(dataEnums.ePreferencesCommands pCommands, List<Object> pData){
        if(mPreferenceModel==null){
            return null;
        }
        return mPreferenceModel.onTrigger(pCommands, pData);
    }

    /*Trigger Help*/

    public Object invokeHelp(dataEnums.eHelpCommands pCommands, List<Object> pData){
        if(mPreferenceModel==null){
            return new ArrayList<helpDataModel>();
        }
        return mHelpDataModel.onTrigger(pCommands, pData);
    }

    /*Trigger Bookmarks*/

    public Object invokeBookmark(dataEnums.eBookmarkCommands pCommands, List<Object> pData){
        return mBookmarkDataModel.onTrigger(pCommands, pData);
    }

    private class invokeBookmarkCallbacks implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(dataEnums.eBookmarkCallbackCommands.M_EXEC_SQL)){
                invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, data);
            }
            return null;
        }
    }

    /*Trigger Tabs*/

    public Object invokeTab(dataEnums.eTabCommands pCommands, List<Object> pData){
        if(mTabModel==null){
            return null;
        }
        return mTabModel.onTrigger(pCommands, pData);
    }

    private class invokeTabCallbacks implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> data, Object e_type)
        {
            if(e_type.equals(dataEnums.eTabCallbackCommands.M_EXEC_SQL)){
                invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, data);
            }
            if(e_type.equals(dataEnums.eTabCallbackCommands.M_EXEC_SQL_USING_CONTENT)){
                invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL_USING_CONTENT, data);
            }
            return null;
        }
    }

    /*Trigger Image Cache*/

    public Object invokeImage(dataEnums.eImageCommands pCommands, List<Object> pData){
        return mImageDataModel.onTrigger(pCommands, pData);
    }

    /*Trigger SQL Cipher Database*/

    public Object invokeSQLCipher(dataEnums.eSqlCipherCommands pCommands, List<Object> pData){
        return mSqlCipherDataModel.onTrigger(pCommands, pData);
    }

    /*Helper Methods*/

    public void clearData(AppCompatActivity mContext){
        dataController.getInstance().initialize(mContext);
        status.sSettingIsAppStarted = false;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_HISTORY,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SEARCH_SUGGESTION,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_JAVA_SCRIPT,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_HISTORY_CLEAR,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_GATEWAY_MANUAL,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_VPN_ENABLED,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.BRIDGE_ENABLES,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FONT_ADJUSTABLE,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_ZOOM,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_VOICE_INPUT,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_TRACKING_PROTECTION, ContentBlocking.AntiTracking.DEFAULT));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_DONOT_TRACK,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_COOKIE_ADJUSTABLE,ACCEPT_FIRST_PARTY));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_FLOAT, Arrays.asList(keys.SETTING_FONT_SIZE,100));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE, strings.SETTING_DEFAULT_LANGUAGE));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_LANGUAGE_REGION,strings.SETTING_DEFAULT_LANGUAGE_REGION));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.SETTING_SEARCH_ENGINE, constants.CONST_BACKEND_GENESIS_URL));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_BRIDGE_1,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,1));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_CHARACTER_ENCODING,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,0));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_TOOLBAR_THEME,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_FULL_SCREEN_BROWSIING,false));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_THEME, enums.Theme.THEME_DEFAULT));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_LIST_VIEW,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_TAB_GRID,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_OPEN_URL_IN_NEW_TAB,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_POPUP,true));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_STRING, Arrays.asList(keys.BRIDGE_CUSTOM_TYPE,strings.BRIDGE_CUSTOM_BRIDGE_OBFS4));
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_INSTALLED,false));

        invokeSQLCipher(dataEnums.eSqlCipherCommands.M_INIT, Collections.singletonList(mContext));
        invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList(SQL_CLEAR_HISTORY,null));
        invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList(SQL_CLEAR_BOOKMARK,null));
        invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList(SQL_CLEAR_TAB,null));

        status.initStatus(mContext);
    }
}

