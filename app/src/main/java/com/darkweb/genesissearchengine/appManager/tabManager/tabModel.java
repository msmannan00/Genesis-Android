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

    public void onClearBackup(){
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            mBackupIndex.get(mCounter).getSession().closeSession();
        }
        mBackupIndex.clear();
    }

    public int onLoadBackup(){
        int mSize = mBackupIndex.size();
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            mModelList.add(0,mBackupIndex.remove(mBackupIndex.size()-1));
            mCounter-=1;
        }
        return mSize;
    }

}