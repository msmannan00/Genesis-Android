package com.hiddenservices.onionservices.pluginManager.pluginReciever;

import static com.hiddenservices.onionservices.constants.constants.CONST_NOTIFICATION_COMMAND;
import static java.lang.System.exit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.core.app.NotificationManagerCompat;
import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.constants.status;

import org.torproject.android.service.OrbotService;
import org.torproject.android.service.wrapper.orbotLocalConstants;

public class defaultNotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int mCommand = intent.getExtras().getInt(CONST_NOTIFICATION_COMMAND);
        if (mCommand == 0) {
            activityContextManager.getInstance().getHomeController().resetAndRestart();
        } else if (mCommand == 1) {
            activityContextManager.getInstance().getHomeController().openNotification();
        }
    }

    protected void onDestroy(Context context) {
        orbotLocalConstants.mAppForceExit = true;

        if (!status.mThemeApplying) {
            if (!status.sSettingIsAppStarted) {
                Intent intent = new Intent(context, OrbotService.class);
                context.stopService(intent);
            } else {
                NotificationManagerCompat.from(context).cancelAll();
            }
        }

        new Handler().postDelayed(() ->
        {
            NotificationManagerCompat.from(context).cancel(1030);
            NotificationManagerCompat.from(context).cancel(1025);

            android.os.Process.killProcess(android.os.Process.myPid());
            exit(1);
        }, 100);
    }
}
