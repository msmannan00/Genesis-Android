package com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome;

import com.hiddenservices.onionservices.dataManager.models.bookmarkRowModel;

import java.util.ArrayList;

class bookmarkModel
{
    /*Private Variables*/

    private ArrayList<bookmarkRowModel> mModelList = new ArrayList<>();

    /*Helper Methods*/

    void setList(ArrayList<bookmarkRowModel> model)
    {
        mModelList = model;
    }

    private void removeFromList(int index)
    {
        mModelList.remove(index);
    }

    void onManualClear(int index){
         removeFromList(index);
    }

    void clearList(){
        mModelList.clear();
    }

    ArrayList<bookmarkRowModel> getList()
    {
        return mModelList;
    }

}