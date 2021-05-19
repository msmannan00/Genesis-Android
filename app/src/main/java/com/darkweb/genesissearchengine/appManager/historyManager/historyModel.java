package com.darkweb.genesissearchengine.appManager.historyManager;

import com.darkweb.genesissearchengine.dataManager.models.historyRowModel;

import java.util.ArrayList;

class historyModel
{
    /*Private Variables*/

    private ArrayList<historyRowModel> mModelList = new ArrayList<>();

    /*Helper Methods*/

    void setList(ArrayList<historyRowModel> model)
    {
        mModelList = model;
    }

    private void removeFromMainList(int index)
    {
        mModelList.remove(index);
    }

    void onManualClear(int index){
         removeFromMainList(index);
    }

    void clearList(){
        mModelList.clear();
    }

    ArrayList<historyRowModel> getList()
    {
        return mModelList;
    }

}