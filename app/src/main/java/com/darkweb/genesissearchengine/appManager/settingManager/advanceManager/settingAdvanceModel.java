package com.darkweb.genesissearchengine.appManager.settingManager.advanceManager;

import android.view.View;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
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
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_RESTORE_TAB,pStatus));
    }

    private void onShowImages(View pView) {

        if(pView.getId() == R.id.pAdvanceOption1){
            status.sShowImages = 0;
        }
        else if(pView.getId() == R.id.pAdvanceOption2){
            status.sShowImages = 2;
        }

        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_INT, Arrays.asList(keys.SETTING_SHOW_IMAGES,status.sShowImages));
    }

    private void onShowWebFonts(boolean pStatus) {
        status.sShowWebFonts = pStatus;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_SHOW_FONTS,pStatus));
    }

    private void onAllowAutoPlay(boolean pStatus) {
        status.sAutoPlay = pStatus;
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_AUTO_PLAY,pStatus));
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
