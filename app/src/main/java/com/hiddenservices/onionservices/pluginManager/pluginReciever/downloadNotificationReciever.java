package com.hiddenservices.onionservices.pluginManager.pluginReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import java.util.Collections;

import static com.hiddenservices.onionservices.constants.constants.*;

public class downloadNotificationReciever extends BroadcastReceiver {
    public void onReceive (Context context , Intent intent) {
        int mCommand = intent.getExtras().getInt(CONST_DOWNLOAD_COMMAND);
        if(mCommand==1){
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt(CONST_DOWNLOAD_INTENT_KEY)), pluginEnums.eDownloadManager.M_TRIGGER);
        }else if(mCommand==0 || mCommand==2) {
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt(CONST_DOWNLOAD_INTENT_KEY)), pluginEnums.eDownloadManager.M_CANCEL);
        }else if(mCommand==3) {
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt(CONST_DOWNLOAD_INTENT_KEY)), pluginEnums.eDownloadManager.M_CANCEL);
        }
    }
}
