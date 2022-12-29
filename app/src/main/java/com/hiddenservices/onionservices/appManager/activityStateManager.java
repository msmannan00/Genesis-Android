package com.hiddenservices.onionservices.appManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.hiddenservices.onionservices.constants.status;

import org.torproject.android.service.OrbotService;
import org.torproject.android.service.wrapper.orbotLocalConstants;

public class activityStateManager extends Service {

    private final IBinder mLocalBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public activityStateManager getService() {
            return activityStateManager.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        Log.i("aaaaaaaa", "aaaaaaaa");
        NotificationManagerCompat.from(this).cancel(1025);
        NotificationManagerCompat.from(this).cancel(1030);
        Intent mServiceIntent = new Intent(this.getApplicationContext(), OrbotService.class);
        this.stopService(mServiceIntent);

        if (OrbotService.getServiceObject() != null) {
            OrbotService.getServiceObject().onDestroy();
        }

        status.sSettingIsAppStarted = false;
        orbotLocalConstants.mAppStarted = false;
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("aaaaaaaa", "aaaaaaaa");
        NotificationManagerCompat.from(this).cancel(1025);
        NotificationManagerCompat.from(this).cancel(1030);
        Intent mServiceIntent = new Intent(this.getApplicationContext(), OrbotService.class);
        this.stopService(mServiceIntent);
        if (OrbotService.getServiceObject() != null) {
            OrbotService.getServiceObject().onDestroy();
        }

        status.sSettingIsAppStarted = false;
        orbotLocalConstants.mAppStarted = false;

        stopSelf();
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}