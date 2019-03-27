package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import info.guardianproject.netcipher.proxy.OrbotHelper;

public class orbot_manager {
    private static final orbot_manager ourInstance = new orbot_manager();
    boolean isOrbotRunning = false;

    public static orbot_manager getInstance() {
        return ourInstance;
    }

    private orbot_manager() {
    }

    public BroadcastReceiver orbotStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(),
                    OrbotHelper.ACTION_STATUS)) {
                String status = intent.getStringExtra(OrbotHelper.EXTRA_STATUS);
                if (status.equals(OrbotHelper.STATUS_ON))
                {
                    isOrbotRunning = true;
                }
                else if (status.equals(OrbotHelper.STATUS_OFF))
                {
                    isOrbotRunning = false;
                }
                else if (status.equals(OrbotHelper.STATUS_STARTING))
                {
                }
                else if (status.equals(OrbotHelper.STATUS_STOPPING))
                {
                }
            }
        }
    };

    public boolean reinitOrbot(Context application_context)
    {
        if(!OrbotHelper.isOrbotInstalled(application_context))
        {
            OrbotHelper.getOrbotInstallIntent(application_context);
            new LovelyStandardDialog(application_context)
                    .setTopColorRes(R.color.header)
                    .setIcon(R.drawable.logo)
                    .setTitle("Orbot Proxy Not Installed")
                    .setMessage("Hidden Web can only be access by Special Proxies. Please Install Orbot Proxy from Playstore")
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String appPackageName = "org.torproject.android"; // getPackageName() from Context or Activity object
                            try {
                                application_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                application_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        if(!isOrbotRunning)
        {
            OrbotHelper.get(application_context).init();
            message_manager.getInstance().startingOrbotInfo(application_context);
            return true;
        }
        return false;
    }

}
