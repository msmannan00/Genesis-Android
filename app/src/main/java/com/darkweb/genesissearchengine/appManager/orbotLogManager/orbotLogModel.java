package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import org.torproject.android.service.wrapper.logRowModel;
import java.util.ArrayList;
import java.util.Collections;

class orbotLogModel
{
    /*Private Variables*/

    private ArrayList<logRowModel> mModelList = new ArrayList<>();

    /*Helper Methods*/

    void setList(ArrayList<logRowModel> model)
    {
        if(model.size()>0){
            mModelList.clear();
            mModelList.addAll(model);
        }
        else {
            mModelList.add(new logRowModel(constants.CONST_LOGS_DEFAULT_MESSAGE, helperMethod.getCurrentTime()));
        }
    }

    ArrayList<logRowModel> getList()
    {
        return mModelList;
    }
}
