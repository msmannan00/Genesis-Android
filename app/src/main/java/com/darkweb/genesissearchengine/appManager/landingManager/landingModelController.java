package com.darkweb.genesissearchengine.appManager.landingManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.eventObserver;

import java.util.Arrays;

public class landingModelController {

    /* Private Variables */

    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    public landingModelController(AppCompatActivity pContext, eventObserver.eventListener pEvent){
        this.mContext = pContext;
        this.mEvent = pEvent;
    }

    private void onUpdateLandingPageStatus(){
        status.sLandingPageStatus = true;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.sLandingPageStatus,true));
    }


    public Object onTrigger(landingEnums.eLandingModelCommands pCommands){

        if(pCommands.equals(landingEnums.eLandingModelCommands.M_UPDATE_LANDING_PAGE_SHOWN_STATUS)){
            onUpdateLandingPageStatus();
        }

        return null;
    }

}