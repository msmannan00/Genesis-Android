package com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome;

import com.hiddenservices.onionservices.dataManager.models.bookmarkRowModel;

import java.util.ArrayList;

class bookmarkModel {
    /*Private Variables*/

    private ArrayList<bookmarkRowModel> mModelList = new ArrayList<>();

    /*Helper Methods*/

    void setList(ArrayList<bookmarkRowModel> model) {
        mModelList = model;
    }

    public void clearList() {
        mModelList.clear();
    }

    ArrayList<bookmarkRowModel> getList() {
        return mModelList;
    }

}