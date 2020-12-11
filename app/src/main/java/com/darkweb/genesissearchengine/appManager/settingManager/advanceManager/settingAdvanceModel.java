package com.darkweb.genesissearchengine.appManager.settingManager.advanceManager;

import android.view.View;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;
import java.util.Arrays;
import java.util.List;

class settingAdvanceModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingAdvanceModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    private void onRestoreTabs(boolean pStatus) {
        status.sRestoreTabs = pStatus;
    }

    private void onShowImages(View pView) {

        if(pView.getId() == R.id.pAdvanceOption1){
            status.sShowImages = 0;
        }
        else if(pView.getId() == R.id.pAdvanceOption2){
            status.sShowImages = 2;
        }
    }

    private void onShowWebFonts(boolean pStatus) {
        status.sShowWebFonts = pStatus;
    }

    private void onAllowAutoPlay(boolean pStatus) {
        status.sAutoPlay = pStatus;
    }

    public Object onTrigger(settingAdvanceEnums.eAdvanceModel pCommands, List<Object> pData){
        if(pCommands.equals(settingAdvanceEnums.eAdvanceModel.M_RESTORE_TAB)){
            onRestoreTabs((boolean)pData.get(0));
        }
        else if(pCommands.equals(settingAdvanceEnums.eAdvanceModel.M_SHOW_IMAGE)){
            onShowImages((View)pData.get(0));
        }
        else if(pCommands.equals(settingAdvanceEnums.eAdvanceModel.M_SHOW_WEB_FONTS)){
            onShowWebFonts((boolean)pData.get(0));
        }
        else if(pCommands.equals(settingAdvanceEnums.eAdvanceModel.M_ALLOW_AUTOPLAY)){
            onAllowAutoPlay((boolean)pData.get(0));
        }
        return null;
    }

}
