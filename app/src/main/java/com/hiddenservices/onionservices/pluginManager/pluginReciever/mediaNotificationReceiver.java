package com.hiddenservices.onionservices.pluginManager.pluginReciever;

import static com.hiddenservices.onionservices.constants.constants.CONST_NOTIFICATION_COMMAND;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.enums;

public class mediaNotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if(activityContextManager.getInstance().getHomeController()!=null){
            int mCommand = intent.getExtras().getInt(CONST_NOTIFICATION_COMMAND);
            if (mCommand == enums.MediaNotificationReceiver.PLAY) {
                activityContextManager.getInstance().getHomeController().onPlayMedia();
            } else if (mCommand == enums.MediaNotificationReceiver.PAUSE) {
                activityContextManager.getInstance().getHomeController().onPauseMedia();
            } else if (mCommand == enums.MediaNotificationReceiver.SKIP_FORWARD) {
                activityContextManager.getInstance().getHomeController().onSkipForwardMedia();
            } else if (mCommand == enums.MediaNotificationReceiver.SKIP_BACKWARD) {
                activityContextManager.getInstance().getHomeController().onSkipBackwardMedia();
            }
        }
    }
}
