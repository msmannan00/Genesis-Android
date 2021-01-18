package com.darkweb.genesissearchengine.appManager.tabManager;

import java.util.ArrayList;

class tabModel
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList = new ArrayList<>();
    private ArrayList<tabRowModel> mBackupIndex = new ArrayList<>();

    /*Initializations*/

    ArrayList<tabRowModel> getList()
    {
        return mModelList;
    }
    void setList(ArrayList<tabRowModel> model)
    {
        mModelList = model;
    }

    public void onRemoveTab(int pIndex){
        mBackupIndex.add(mModelList.get(pIndex));
    }

    public ArrayList<tabRowModel> onGetBackup(){
        return mBackupIndex;
    }

    public void onClearBackupWithoutClose(){
        mBackupIndex.clear();
    }

    public ArrayList<tabRowModel> onLoadBackup(){
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            mModelList.add(0,mBackupIndex.get(mCounter));
        }
        return mBackupIndex;
    }

}