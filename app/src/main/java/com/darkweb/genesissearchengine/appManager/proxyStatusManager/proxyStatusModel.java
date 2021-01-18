package com.darkweb.genesissearchengine.appManager.proxyStatusManager;

import com.darkweb.genesissearchengine.helperManager.eventObserver;

class proxyStatusModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    proxyStatusModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

}
