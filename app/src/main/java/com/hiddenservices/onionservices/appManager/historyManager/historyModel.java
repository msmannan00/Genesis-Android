package com.hiddenservices.onionservices.appManager.historyManager;

import com.hiddenservices.onionservices.dataManager.models.historyRowModel;

import java.util.ArrayList;

public class historyModel {
    /*Private Variables*/

    private ArrayList<historyRowModel> mModelList = new ArrayList<>();

    /*Helper Methods*/

    void setList(ArrayList<historyRowModel> model) {
        mModelList = model;
    }

    private void removeFromMainList(int index) {
        try {
            mModelList.remove(index);
        }catch (Exception ex){}
    }

    public void onManualClear(int index) {
        removeFromMainList(index);
    }

    public void clearList() {
        mModelList.clear();
    }

    ArrayList<historyRowModel> getList() {
        return mModelList;
    }

}