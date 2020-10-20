package com.darkweb.genesissearchengine.dataManager;

import com.darkweb.genesissearchengine.appManager.bookmarkManager.bookmarkRowModel;
import com.darkweb.genesissearchengine.appManager.databaseManager.databaseController;
import com.darkweb.genesissearchengine.constants.constants;

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

    void addBookmark(String url, String title){
        if(url.length()>1500){
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

        if(title.equals(""))
        {
            title = "New_Bookmark"+autoval;
        }

        String[] params = new String[2];
        params[0] = title;
        params[1] = url;

        databaseController.getInstance().execSQL("INSERT INTO bookmark(id,title,url) VALUES("+autoval+",?,?);",params);
        mBookmarks.add(0,new bookmarkRowModel(title, url,autoval));
    }

    void clearBookmark(int pID) {
        for(int mCounter=0;mCounter<mBookmarks.size();mCounter++){
            if(mBookmarks.get(mCounter).getID()==pID){
                mBookmarks.remove(mCounter);
            }
        }
        databaseController.getInstance().execSQL("delete from bookmark where id="+ pID,null);
    }

    public Object onTrigger(dataEnums.eBookmarkCommands p_commands, List<Object> p_data){
        if(p_commands == dataEnums.eBookmarkCommands.M_GET_BOOKMARK){
            return getBookmark();
        }
        if(p_commands == dataEnums.eBookmarkCommands.M_ADD_BOOKMARK){
            addBookmark((String)p_data.get(0), (String)p_data.get(1));
        }
        if(p_commands == dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK){
            clearBookmark((int)p_data.get(0));
        }

        return null;
    }

}
