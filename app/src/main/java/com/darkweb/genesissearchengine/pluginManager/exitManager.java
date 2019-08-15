package com.darkweb.genesissearchengine.pluginManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.dataManager.preference_manager;

public class exitManager extends Service {

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
        super.onDestroy();
        preference_manager.getInstance().setBool(keys.low_memory,false);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        preference_manager.getInstance().setBool(keys.low_memory,false);
   }
}