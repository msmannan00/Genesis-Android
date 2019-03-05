package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import android.util.Log;
import info.guardianproject.netcipher.proxy.StatusCallback;

public class torProxyServer {
    private static final torProxyServer ourInstance = new torProxyServer();

    public static torProxyServer getInstance() {
        return ourInstance;
    }

    private torProxyServer() {
    }

    public void initialize(Context applicationContext, final Activity activity)
    {
        if (OrbotHelper.isOrbotInstalled(activity))
        {
            NetCipher.useTor();
            OrbotHelper.get(activity).statusTimeout(60000).addStatusCallback(new StatusCallback(){
                @Override
                public void onEnabled(Intent intent){ Log.d("Log Started","Log Started"); }

                @Override
                public void onStarting() { }

                @Override
                public void onStopping(){ }

                @Override
                public void onDisabled(){ }

                @Override
                public void onStatusTimeout(){ }

                @Override
                public void onNotYetInstalled(){ }
            }).init();
        }
        else
        {
            Intent intent = OrbotHelper.getOrbotInstallIntent(activity);
            activity.startActivityForResult(intent,0);
        }
    }
}
