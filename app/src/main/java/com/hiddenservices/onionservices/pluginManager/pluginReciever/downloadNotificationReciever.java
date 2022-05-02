package com.hiddenservices.onionservices.pluginManager.pluginReciever;

import static com.hiddenservices.onionservices.constants.constants.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;

import java.util.Collections;

public class downloadNotificationReciever extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int mCommand = intent.getExtras().getInt(CONST_DOWNLOAD_COMMAND);
        if (mCommand == 1) {
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt(CONST_DOWNLOAD_INTENT_KEY)), pluginEnums.eDownloadManager.M_URL_DOWNLOAD_REQUEST);
        } else if (mCommand == 0 || mCommand == 2) {
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt(CONST_DOWNLOAD_INTENT_KEY)), pluginEnums.eDownloadManager.M_CANCEL_DOWNLOAD);
        } else if (mCommand == 3) {
            pluginController.getInstance().onDownloadInvoke(Collections.singletonList(intent.getExtras().getInt(CONST_DOWNLOAD_INTENT_KEY)), pluginEnums.eDownloadManager.M_CANCEL_DOWNLOAD);
        }
    }
}
