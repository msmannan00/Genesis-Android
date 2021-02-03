package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.constants.constants;
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

    public Object onTrigger(dataEnums.eBookmarkCommands p_commands, List<Object> pData){
        if(p_commands == dataEnums.eBookmarkCommands.M_GET_BOOKMARK){
            return getBookmark();
        }
        else if(p_commands == dataEnums.eBookmarkCommands.M_ADD_BOOKMARK){
            addBookmark((String)pData.get(0), (String)pData.get(1));
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
