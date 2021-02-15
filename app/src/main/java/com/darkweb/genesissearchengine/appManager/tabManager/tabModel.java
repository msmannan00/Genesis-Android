package com.darkweb.genesissearchengine.appManager.tabManager;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
        mBackupIndex.add(mModelList.get(pIndex));
    }

    private ArrayList<tabRowModel> onGetBackup(){
        return mBackupIndex;
    }

    private void onClearBackupWithoutClose(){
        mBackupIndex.clear();
    }

    private ArrayList<tabRowModel> onLoadBackup(){
        for(int mCounter=0;mCounter<mBackupIndex.size();mCounter++){
            mModelList.add(0,mBackupIndex.get(mCounter));
        }
        return mBackupIndex;
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
        if(pCommands.equals(tabEnums.eModelCallback.M_LOAD_BACKUP)){
            return onLoadBackup();
        }

        return null;
    }
}