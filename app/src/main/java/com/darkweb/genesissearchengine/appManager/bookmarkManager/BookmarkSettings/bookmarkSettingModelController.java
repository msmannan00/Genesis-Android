package com.darkweb.genesissearchengine.appManager.bookmarkManager.BookmarkSettings;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesissearchengine.dataManager.dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK_FROM_MENU;

class bookmarkSettingModelController
{
    /* Local Variables Variables */

    private boolean mBookmarkUpdateStatus = false;
    private int mBookmarkID;
    private String mBookmarkURL;

    /* Private Variables */

    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    public bookmarkSettingModelController(AppCompatActivity pContext, eventObserver.eventListener pEvent, int pBookmarkID, String pBookmarkURL){
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mBookmarkID = pBookmarkID;
        this.mBookmarkURL = pBookmarkURL;
    }

    /* Helper Methods */

    private void onUpdateBookmark(String pBookmarkName){
        dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_UPDATE_BOOKMARK, Arrays.asList(pBookmarkName, mBookmarkURL, mBookmarkID));
    }

    private boolean validateForm(String pBookmarkName){
        if(pBookmarkName.equals(strings.GENERIC_EMPTY_STR)){
            return false;
        }else {
            return true;
        }
    }

    private boolean getBookmarkUpdateStatus(){
        return mBookmarkUpdateStatus;
    }

    private void setBookmarkUpdateStatus(boolean pBookmarkUpdateStatus){
        mBookmarkUpdateStatus = pBookmarkUpdateStatus;
    }

    private void onDeleteBookmark(){
        dataController.getInstance().invokeBookmark(M_DELETE_BOOKMARK_FROM_MENU, Collections.singletonList(mBookmarkURL));
    }

    /* Event Observer */

    public Object onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands pCommands, List<Object> pData){
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_UPDATE_BOOKMARK.equals(pCommands)){
            onUpdateBookmark((String) pData.get(0));
        }
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_VALIDATE_FORM.equals(pCommands)){
            return validateForm((String) pData.get(0));
        }
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_SET_BOOOKMARK_CHANGED_STATUS.equals(pCommands)){
            setBookmarkUpdateStatus((boolean)pData.get(0));
        }
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_DELETE_BOOKMARK.equals(pCommands)){
            onDeleteBookmark();
        }
        return null;
    }

    public Object onTrigger(bookmarkSettingEnums.eBookmarkSettingModelCommands pCommands){
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_DELETE_BOOKMARK.equals(pCommands)){
            onDeleteBookmark();
        }
        if(bookmarkSettingEnums.eBookmarkSettingModelCommands.M_GET_UPDATE_STATUS.equals(pCommands)){
            return getBookmarkUpdateStatus();
        }
        return null;
    }

}