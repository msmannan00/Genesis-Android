package com.hiddenservices.onionservices.appManager;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
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
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        cleanup();
        super.onDestroy();
        System.exit(1);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        cleanup();
        stopSelf();
        super.onTaskRemoved(rootIntent);
        System.exit(1);
    }

    private void cleanup() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        orbotLocalConstants.mAppForceExit = true;
        status.sNoTorTriggered = false;
        NotificationManagerCompat.from(this).cancelAll();
        Intent mServiceIntent = new Intent(this, OrbotService.class);
        this.stopService(mServiceIntent);
        if (OrbotService.getServiceObject() != null) {
            OrbotService.getServiceObject().onDestroy();
        }
        status.sSettingIsAppStarted = false;
        orbotLocalConstants.mAppStarted = false;
    }
}
