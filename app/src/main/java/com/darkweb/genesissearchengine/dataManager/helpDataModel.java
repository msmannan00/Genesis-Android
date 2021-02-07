package com.darkweb.genesissearchengine.dataManager;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("CommitPrefEdits")
class helpDataModel
{
    private ArrayList<helpDataModel> mHelpListModel;

    helpDataModel(){
        mHelpListModel = new ArrayList<>();
    }

    ArrayList<helpDataModel> getHelpModel(){
        return mHelpListModel;
    }

    private void setModel(ArrayList<helpDataModel> pHelpListModel){
        mHelpListModel.clear();
        mHelpListModel.addAll(pHelpListModel);
    }

    /*List Tabs*/


    /*List Suggestion*/
    public Object onTrigger(dataEnums.eHelpCommands pCommands, List<Object> pData){
        if(pCommands.equals(dataEnums.eHelpCommands.M_GET_HELP)){
            return getHelpModel();
        }
        else if(pCommands.equals(dataEnums.eHelpCommands.M_SET_HELP)){
            setModel((ArrayList<helpDataModel>)pData.get(0));
        }

        return null;
    }

}
