package com.darkweb.genesissearchengine.appManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationManagerCompat;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;

import org.torproject.android.proxy.OrbotService;
import org.torproject.android.proxy.wrapper.orbotLocalConstants;

import java.util.Collections;

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
        Intent mServiceIntent = new Intent(this.getApplicationContext(), OrbotService.class);
        this.stopService(mServiceIntent);
        OrbotService.getServiceObject().onDestroy();

        status.sSettingIsAppStarted = false;
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent mServiceIntent = new Intent(this.getApplicationContext(), OrbotService.class);
        this.stopService(mServiceIntent);
        OrbotService.getServiceObject().onDestroy();

        status.sSettingIsAppStarted = false;

        stopSelf();
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}