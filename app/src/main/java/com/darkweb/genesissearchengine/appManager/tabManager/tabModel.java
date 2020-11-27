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
        mBackupIndex.add(mModelList.remove(pIndex));
    }

    public void onClearBackup(){
        mBackupIndex.clear();
    }

    public int onLoadBackup(){
        int mSize = mBackupIndex.size();
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            mModelList.add(0,mBackupIndex.remove(mCounter));
            mCounter-=1;
        }
        return mSize;
    }

}