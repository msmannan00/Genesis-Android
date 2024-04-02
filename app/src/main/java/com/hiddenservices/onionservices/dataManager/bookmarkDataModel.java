package com.hiddenservices.onionservices.dataManager;

import android.content.Intent;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.dataManager.models.bookmarkRowModel;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.eventObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class bookmarkDataModel {

    /* Local Variables */

    private eventObserver.eventListener mExternalEvents;
    private ArrayList<bookmarkRowModel> mBookmarks;
    private HashMap<String, Integer> mAvailableBookmark = new HashMap<>();

    /* Initializations */

    public bookmarkDataModel(eventObserver.eventListener pExternalEvents) {
        mBookmarks = new ArrayList<>();
        mExternalEvents = pExternalEvents;
    }

    void initializeBookmark(ArrayList<bookmarkRowModel> pBookmark) {
        mBookmarks = pBookmark;
        for (int mCounter = pBookmark.size()-1; mCounter >= 0; mCounter--) {
            if (mAvailableBookmark.containsKey(pBookmark.get(mCounter).getDescription())) {
                deleteBookmark(pBookmark.get(mCounter).getID());
                mCounter++;
            } else {
                mAvailableBookmark.put(pBookmark.get(mCounter).getDescription(), pBookmark.get(mCounter).getID());
            }
        }
    }

    /* Helper Methods */

    private ArrayList<bookmarkRowModel> getBookmark() {
        return mBookmarks;
    }

    void addBookmark(String pURL, String pTitle) {
        if (pURL.endsWith("about:blank"))
            pURL = "about:blank";

        if (pURL.length() > 1500 && !pURL.startsWith("resource://android/assets/")) {
            return;
        }
        int autoValue = 0;
        if (mBookmarks.size() > constants.CONST_MAX_BOOKMARK_SIZE) {
            mExternalEvents.invokeObserver(Arrays.asList("delete from bookmark where id=" + mBookmarks.get(mBookmarks.size() - 1).getID(), null), dataEnums.eBookmarkCallbackCommands.M_EXEC_SQL);
        }

        if (!mBookmarks.isEmpty()) {
            autoValue = mBookmarks.get(0).getID() + 1;
        }

        if (pTitle.isEmpty()) {
            pTitle = strings.BOOKMARK_DEFAULT_TITLE + autoValue;
        }

        String[] params = new String[2];
        params[0] = pTitle;
        params[1] = pURL;

        if (!pTitle.equals("loading")) {
            mExternalEvents.invokeObserver(Arrays.asList("REPLACE INTO bookmark(id,title,url) VALUES(" + autoValue + ",?,?);", params), dataEnums.eBookmarkCallbackCommands.M_EXEC_SQL);
        }
        mBookmarks.add(0, new bookmarkRowModel(pTitle, pURL, autoValue));
        mAvailableBookmark.put(pURL, autoValue);
    }

    void clearBookmark() {
        mBookmarks.clear();
        mAvailableBookmark.clear();
    }

    void updateBookmark(String pBookmarkName, String pBookmarkURL, int pBookmarkID) {
        for (int mCounter = 0; mCounter < mBookmarks.size(); mCounter++) {
            if (mBookmarks.get(mCounter).getID() == pBookmarkID) {
                mBookmarks.get(mCounter).setHeader(pBookmarkName);
                mBookmarks.get(mCounter).setURL(pBookmarkURL);
                mAvailableBookmark.put(pBookmarkURL, pBookmarkID);
            }
        }

        int autoValue = 0;
        String[] params = new String[2];
        params[0] = pBookmarkName;
        params[1] = pBookmarkURL;

        mExternalEvents.invokeObserver(Arrays.asList("REPLACE INTO bookmark(id,title,url) VALUES(" + autoValue + ",?,?);", params), dataEnums.eBookmarkCallbackCommands.M_EXEC_SQL);
    }

    void deleteBookmark(int pID) {
        for (int mCounter = 0; mCounter < mBookmarks.size(); mCounter++) {
            if (mBookmarks.get(mCounter).getID() == pID) {
                mAvailableBookmark.remove(mBookmarks.get(mCounter).getDescription());
                mBookmarks.remove(mCounter);
            }
        }

        mExternalEvents.invokeObserver(Arrays.asList("delete from bookmark where id=" + pID, null), dataEnums.eBookmarkCallbackCommands.M_EXEC_SQL);
    }

    boolean isURLBookmarked(String pURL) {
        return mAvailableBookmark.containsKey(pURL);
    }

    Intent getBookmarkedIntent(Intent intent, String pURL) {

        for (int mCounter = 0; mCounter < mBookmarks.size(); mCounter++) {
            if (mAvailableBookmark.get(pURL)!=null && mBookmarks.get(mCounter).getID() == mAvailableBookmark.get(pURL)) {
                bookmarkRowModel mBookmarkRowModel = mBookmarks.get(mCounter);
                intent.putExtra(keys.BOOKMARK_SETTING_NAME, mBookmarkRowModel.getHeader());
                intent.putExtra(keys.BOOKMARK_SETTING_URL, mBookmarkRowModel.getDescription());
                intent.putExtra(keys.BOOKMARK_SETTING_ID, mBookmarkRowModel.getID());
                return intent;
            }
        }
        return null;
    }

    /* External Triggers */

    public Object onTrigger(dataEnums.eBookmarkCommands pCommands, List<Object> pData) {
        if (pCommands == dataEnums.eBookmarkCommands.M_GET_BOOKMARK) {
            return getBookmark();
        } else if (pCommands == dataEnums.eBookmarkCommands.M_ADD_BOOKMARK) {
            addBookmark((String) pData.get(0), (String) pData.get(1));
        } else if (pCommands == dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK_FROM_MENU) {
            deleteBookmark(mAvailableBookmark.get((String) pData.get(0)));
        } else if (pCommands == dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK) {
            deleteBookmark((int) pData.get(0));
        } else if (pCommands == dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK) {
            clearBookmark();
        } else if (pCommands == dataEnums.eBookmarkCommands.M_UPDATE_BOOKMARK) {
            updateBookmark((String) pData.get(0), (String) pData.get(1), (int) pData.get(2));
        } else if (pCommands == dataEnums.eBookmarkCommands.M_BOOKMARK_AVAILABLE) {
            return isURLBookmarked((String) pData.get(0));
        } else if (pCommands == dataEnums.eBookmarkCommands.M_INTENT_BOOKMARK) {
            return getBookmarkedIntent((Intent) pData.get(0), (String) pData.get(1));
        }
        return null;
    }

}
