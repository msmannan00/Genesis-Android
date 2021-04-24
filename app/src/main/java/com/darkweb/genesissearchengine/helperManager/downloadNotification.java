package com.darkweb.genesissearchengine.helperManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import java.util.Collections;

public class downloadNotification extends BroadcastReceiver {
    public void onReceive (Context context , Intent intent) {
        int mCommand = intent.getExtras().getInt("N_COMMAND");
        if(mCommand==1){
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt("N_ID")), pluginEnums.eDownloadManager.M_TRIGGER);
        }else if(mCommand==0 || mCommand==2) {
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt("N_ID")), pluginEnums.eDownloadManager.M_CANCEL);
        }
    }

}
