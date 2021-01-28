package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;

import java.util.ArrayList;
import java.util.List;

public class bookmarkDataModel {

    private ArrayList<bookmarkRowModel> mBookmarks;

    public bookmarkDataModel(){
        mBookmarks = new ArrayList<>();
    }

    void initializebookmark(ArrayList<bookmarkRowModel> pBookmark){
        mBookmarks = pBookmark;
    }

    private ArrayList<bookmarkRowModel> getBookmark() {
        return mBookmarks;
    }

    void addBookmark(String pURL, String pTitle){
        if(pURL.endsWith("about:blank"))
            pURL = "about:blank";
        if(pURL.length()>1500){
            return;
        }
        int autoval = 0;
        if(mBookmarks.size()> constants.CONST_MAX_BOOKMARK_SIZE)
        {
            databaseController.getInstance().execSQL("delete from bookmark where id="+ mBookmarks.get(mBookmarks.size()-1).getID(),null);
        }

        if(mBookmarks.size()>0)
        {
            autoval = mBookmarks.get(0).getID()+1;
        }

        if(pTitle.equals(""))
        {
            pTitle = strings.BOOKMARK_DEFAULT_TITLE+autoval;
        }

        String[] params = new String[2];
        params[0] = pTitle;
        params[1] = pURL;

        if(!pTitle.equals("loading")){
            databaseController.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",?,?);",params);
        }
        mBookmarks.add(0,new bookmarkRowModel(pTitle, pURL,autoval));
    }

    void clearBookmark(){
        mBookmarks.clear();
    }

    void deleteBookmark(int pID) {
        for(int mCounter=0;mCounter<mBookmarks.size();mCounter++){
            if(mBookmarks.get(mCounter).getID()==pID){
                mBookmarks.remove(mCounter);
            }
        }
        databaseController.getInstance().execSQL("delete from bookmark where id="+ pID,null);
    }

    public ArrayList<historyRowModel> getSuggestions(String pQuery){
        pQuery = pQuery.toLowerCase();
        ArrayList<historyRowModel> mModel = new ArrayList<>();

        if(status.sSettingSearchHistory) {
            for (int count = 0; count <= mBookmarks.size() - 1 && mBookmarks.size() < 500; count++) {
                if (mBookmarks.get(count).getHeader().toLowerCase().contains(pQuery)) {
                    mModel.add(0, new historyRowModel(mBookmarks.get(count).getHeader(), mBookmarks.get(count).getDescription(), -1));
                } else if (mModel.size() > 0 && mBookmarks.get(count).getDescription().toLowerCase().contains(pQuery)) {
                    mModel.add(mModel.size() - 1, new historyRowModel(mBookmarks.get(count).getHeader(), mBookmarks.get(count).getDescription(), -1));
                }
            }
        }
        return mModel;
    }

    public Object onTrigger(dataEnums.eBookmarkCommands p_commands, List<Object> pData){
        if(p_commands == dataEnums.eBookmarkCommands.M_GET_BOOKMARK){
            return getBookmark();
        }
        else if(p_commands == dataEnums.eBookmarkCommands.M_ADD_BOOKMARK){
            addBookmark((String)pData.get(0), (String)pData.get(1));
        }
        else if(p_commands == dataEnums.eBookmarkCommands.M_GET_SUGGESTIONS){
            return getSuggestions((String)pData.get(0));
        }
        else if(p_commands == dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK){
            deleteBookmark((int)pData.get(0));
        }
        else if(p_commands == dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK){
            clearBookmark();
        }

        return null;
    }

}
