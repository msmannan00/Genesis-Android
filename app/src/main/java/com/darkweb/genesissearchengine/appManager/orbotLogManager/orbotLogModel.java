package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import org.torproject.android.proxy.wrapper.logRowModel;
import java.util.ArrayList;
import java.util.List;

class orbotLogModel
{
    /*Private Variables*/

    private ArrayList<logRowModel> mModelList = new ArrayList<>();

    /*Helper Methods*/

    void setList(ArrayList<logRowModel> pModel)
    {
        if(pModel.size()>0){
            mModelList.clear();
            mModelList.addAll(pModel);
        }
        else {
            mModelList.add(new logRowModel(constants.CONST_LOGS_DEFAULT_MESSAGE, helperMethod.getCurrentTime()));
        }
    }

    private ArrayList<logRowModel> getList()
    {
        return mModelList;
    }

    private int getListSize()
    {
        return mModelList.size();
    }

    /*Triggers*/

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands pCommands, List<Object> pData){
    }

    public Object onTrigger(orbotLogEnums.eOrbotLogModelCommands pCommands){
        if(pCommands.equals(orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST)){
            return getList();
        }
        else if(pCommands.equals(orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST_SIZE)){
            return getListSize();
        }
        return null;
    }

}
