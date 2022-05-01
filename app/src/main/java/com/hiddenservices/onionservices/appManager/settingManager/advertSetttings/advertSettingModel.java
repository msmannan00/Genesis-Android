package com.hiddenservices.onionservices.appManager.settingManager.advertSetttings;

import com.hiddenservices.onionservices.eventObserver;
import java.util.List;

class advertSettingModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    advertSettingModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    public Object onTrigger(advertSettingEnums.eAdvertSettingModel pCommands, List<Object> pData){
        return null;
    }

}
