package com.darkweb.genesissearchengine.appManager.settingManager.privacyManager;

import android.view.View;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;

import java.util.List;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_ALL;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_NONE;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS;

class settingPrivacyModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    settingPrivacyModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    private void onJavaScript(boolean pStatus){
        status.sSettingJavaStatus = pStatus;
    }

    private void onDoNotTrack(boolean pStatus){
        status.sStatusDoNotTrack = pStatus;
    }

    private void onPopup(boolean pStatus){
        status.sSettingPopupStatus = pStatus;
    }

    private void onCookies(View pView){
        if(pView.getId() == R.id.pCookieOption1){
            status.sSettingCookieStatus = ACCEPT_ALL;
        }
        if(pView.getId() == R.id.pCookieOption2){
            status.sSettingCookieStatus = ACCEPT_NON_TRACKERS;
        }
        if(pView.getId() == R.id.pCookieOption3){
            status.sSettingCookieStatus = ACCEPT_FIRST_PARTY;
        }
        if(pView.getId() == R.id.pCookieOption4){
            status.sSettingCookieStatus = ACCEPT_NONE;
        }
    }

    private void onClearPrivateData(boolean pStatus){
        status.sClearOnExit = pStatus;
    }

    public Object onTrigger(settingPrivacyEnums.ePrivacyModel pCommands, List<Object> pData){
        if(pCommands.equals(settingPrivacyEnums.ePrivacyModel.M_SET_JAVASCRIPT)){
            onJavaScript((boolean)pData.get(0));
        }
        else if(pCommands.equals(settingPrivacyEnums.ePrivacyModel.M_SET_DONOT_TRACK)){
            onDoNotTrack((boolean)pData.get(0));
        }
        else if(pCommands.equals(settingPrivacyEnums.ePrivacyModel.M_SET_POPUP)){
            onPopup((boolean)pData.get(0));
        }
        else if(pCommands.equals(settingPrivacyEnums.ePrivacyModel.M_SET_COOKIES)){
            onCookies((View) pData.get(0));
        }
        else if(pCommands.equals(settingPrivacyEnums.ePrivacyModel.M_SET_CLEAR_PRIVATE_DATA)){
            onClearPrivateData((Boolean) pData.get(0));
        }
        return null;
    }

}
