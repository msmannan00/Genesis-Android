package com.darkweb.genesissearchengine.appManager.tabManager;

import java.util.ArrayList;

class tabModel
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList = new ArrayList<>();

    /*Initializations*/

    void setList(ArrayList<tabRowModel> model)
    {
        mModelList = model;
    }

    ArrayList<tabRowModel> getList()
    {
        return mModelList;
    }

}