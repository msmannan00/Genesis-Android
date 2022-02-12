package com.hiddenservices.genesissearchengine.production.appManager.tabManager;

import com.hiddenservices.genesissearchengine.production.dataManager.dataController;
import com.hiddenservices.genesissearchengine.production.dataManager.dataEnums;
import com.hiddenservices.genesissearchengine.production.dataManager.models.tabRowModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private void setList(ArrayList<tabRowModel> model)
    {
        mModelList = model;
    }

    private void onRemoveTab(int pIndex){
        if(mModelList.size()>pIndex){
            mBackupIndex.add(mModelList.get(pIndex));
        }
    }

    private ArrayList<tabRowModel> onGetBackup(){
        return mBackupIndex;
    }

    private void onClearBackupWithoutClose(){
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            dataController.getInstance().invokeTab(dataEnums.eTabCommands.CLOSE_TAB, Arrays.asList(mBackupIndex.get(mCounter).getSession(), mBackupIndex.get(mCounter).getSession()));
        }
        mBackupIndex.clear();
    }

    private void onClearBackupRetainDatabase(){
        mBackupIndex.clear();
    }

    private ArrayList<tabRowModel> onLoadBackup(){
        mModelList.addAll(0, mBackupIndex);
        ArrayList<tabRowModel> mModelListTemp = new ArrayList<>();
        mModelListTemp.addAll(mBackupIndex);
        return mModelListTemp;
    }


    public Object onTrigger(tabEnums.eModelCallback pCommands, List<Object> pData){
        if(pCommands.equals(tabEnums.eModelCallback.M_SET_LIST)){
            setList((ArrayList<tabRowModel>)pData.get(0));
        }
        if(pCommands.equals(tabEnums.eModelCallback.M_GET_LIST)){
            return getList();
        }
        if(pCommands.equals(tabEnums.eModelCallback.M_REMOVE_TAB)){
            onRemoveTab((int) pData.get(0));
        }
        if(pCommands.equals(tabEnums.eModelCallback.M_GET_BACKUP)){
            return onGetBackup();
        }
        if(pCommands.equals(tabEnums.eModelCallback.M_CLEAR_BACKUP_WITHOUT_CLOSE)){
            onClearBackupWithoutClose();
        }
        if(pCommands.equals(tabEnums.eModelCallback.M_CLEAR_BACKUP_RETAIN_DATABASE)){
            onClearBackupRetainDatabase();
        }
        if(pCommands.equals(tabEnums.eModelCallback.M_LOAD_BACKUP)){
            return onLoadBackup();
        }

        return null;
    }
}