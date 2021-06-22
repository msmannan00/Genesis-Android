package com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.darkweb.genesissearchengine.constants.strings.BOOKMARK_SETTING_VALIDATION_ERROR_1;
import static com.darkweb.genesissearchengine.constants.strings.BOOKMARK_SETTING_VALIDATION_ERROR_2;

class bookmarkSettingModel
{
    /* Private Variables */

    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;
    private int mBookmarkID;

    public bookmarkSettingModel(AppCompatActivity pContext, eventObserver.eventListener pEvent, int pBookmarkID){
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mBookmarkID = pBookmarkID;
    }

    /* Helper Methods */

    private int getBookarkID(){
        return mBookmarkID;
    }

    private void onUpdateBookmark(String pBookmarkName, String pBookmarkURL){
        boolean status = validateForm(pBookmarkName, pBookmarkURL);
        if(status){
            dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_UPDATE_BOOKMARK, Arrays.asList(pBookmarkName, pBookmarkURL, mBookmarkID));
            mEvent.invokeObserver(null, bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_CLOSE);
        }
    }

    private boolean validateForm(String pBookmarkName, String pBookmarkURL){
        mEvent.invokeObserver(null, bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_CLEAR_FORM);
        boolean mStatus = true;
        if(pBookmarkName.equals(strings.GENERIC_EMPTY_STR)){
            mEvent.invokeObserver(Arrays.asList(BOOKMARK_SETTING_VALIDATION_ERROR_1, false), bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_NAME_VALIDATION_ERROR);
            mStatus = false;
        }else {
            mEvent.invokeObserver(Arrays.asList(BOOKMARK_SETTING_VALIDATION_ERROR_1, true), bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_NAME_VALIDATION_ERROR);
        }
        if(pBookmarkURL.equals(strings.GENERIC_EMPTY_STR)){
            mEvent.invokeObserver(Arrays.asList(strings.BOOKMARK_SETTING_VALIDATION_ERROR_2, false), bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_URL_VALIDATION_ERROR);
            mStatus = false;
        }else {
            mEvent.invokeObserver(Arrays.asList(BOOKMARK_SETTING_VALIDATION_ERROR_2, true), bookmarkSettingEnums.eBookmarkSettingModelCallbackCommands.M_BOOKMARK_URL_VALIDATION_ERROR);
        }

        return mStatus;
    }

    /* Event Observer */

    public Object onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands pCommands, List<Object> pData){
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_GET_BOOKMARK_ID.equals(pCommands)){
            return getBookarkID();
        }
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_UPDATE_BOOKMARK.equals(pCommands)){
            onUpdateBookmark((String) pData.get(0), (String) pData.get(1));
        }
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_VALIDATE_FORM.equals(pCommands)){
            validateForm((String) pData.get(0), (String) pData.get(1));
        }
        return null;
    }

}