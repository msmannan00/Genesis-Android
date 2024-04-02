package com.hiddenservices.onionservices.pluginManager.pluginReciever;

import static com.hiddenservices.onionservices.constants.constants.CONST_NOTIFICATION_COMMAND;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.enums;

public class mediaNotificationReciever extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int mCommand = intent.getExtras().getInt(CONST_NOTIFICATION_COMMAND);
        if (mCommand == enums.MediaNotificationReciever.PLAY) {
            activityContextManager.getInstance().getHomeController().onPlayMedia();
        } else if (mCommand == enums.MediaNotificationReciever.PAUSE) {
            activityContextManager.getInstance().getHomeController().onPauseMedia();
        } else if (mCommand == enums.MediaNotificationReciever.SKIP_FORWARD) {
            activityContextManager.getInstance().getHomeController().onSkipForwardMedia();
        } else if (mCommand == enums.MediaNotificationReciever.SKIP_BACKWARD) {
            activityContextManager.getInstance().getHomeController().onSkipBackwardMedia();
        }
    }
}
