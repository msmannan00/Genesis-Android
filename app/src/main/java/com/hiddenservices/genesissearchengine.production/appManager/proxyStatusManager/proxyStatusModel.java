package com.hiddenservices.genesissearchengine.production.appManager.proxyStatusManager;

import com.hiddenservices.genesissearchengine.production.eventObserver;

class proxyStatusModel
{
    /*Variable Declaration*/

    private eventObserver.eventListener mEvent;

    /*Initializations*/

    proxyStatusModel(eventObserver.eventListener mEvent){
        this.mEvent = mEvent;
    }

}
