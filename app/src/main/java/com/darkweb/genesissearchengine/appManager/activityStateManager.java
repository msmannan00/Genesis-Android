package com.darkweb.genesissearchengine.appManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.darkweb.genesissearchengine.constants.status;

public class activityStateManager extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        status.sSettingIsAppStarted = false;
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        status.sSettingIsAppStarted = false;
        stopSelf();
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}