package com.hiddenservices.onionservices.pluginManager.pluginReciever;

import static com.hiddenservices.onionservices.constants.constants.CONST_NOTIFICATION_COMMAND;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hiddenservices.onionservices.appManager.activityContextManager;

public class mediaNotificationReciever extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int mCommand = intent.getExtras().getInt(CONST_NOTIFICATION_COMMAND);
        if (mCommand == 0) {
            activityContextManager.getInstance().getHomeController().onPlayMedia();
        } else if (mCommand == 1) {
            activityContextManager.getInstance().getHomeController().onPauseMedia();
        } else if (mCommand == 2) {
            activityContextManager.getInstance().getHomeController().onSkipForwardMedia();
        } else if (mCommand == 3) {
            activityContextManager.getInstance().getHomeController().onSkipBackwardMedia();
        }
    }
}
