package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import java.util.Arrays;
import java.util.List;

class orbotLogModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    orbotLogModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

    /*Helper Methods*/

    public void onTrigger(orbotLogEnums.eOrbotLogModelCommands pCommands, List<Object> pData){
    }


}
