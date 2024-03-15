package org.torproject.android.service.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.torproject.android.service.OrbotService;

public class PowerConnectionReceiver extends BroadcastReceiver {

    private final OrbotService mService;

    public PowerConnectionReceiver(OrbotService service) {
        mService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Prefs.limitSnowflakeProxyingCharging()) {
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                if (Prefs.beSnowflakeProxy()) mService.enableSnowflakeProxy();

            } else {
                intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
                mService.disableSnowflakeProxy();
            }
        }
    }

}