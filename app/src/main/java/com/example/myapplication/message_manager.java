package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

public class message_manager {
    private static final message_manager ourInstance = new message_manager();

    public static message_manager getInstance() {
        return ourInstance;
    }

    private message_manager() {
    }

    public void baseURLError(Context application_context)
    {
        new LovelyInfoDialog(application_context)
                .setTopColorRes(R.color.header)
                .setIcon(R.drawable.logo)
                .setTitle("Surface Web URL Not Allowed")
                .setMessage("This software can only be used to search hidden web such as \"Onion\" and \"I2P\" for searching in Surface Web use \"Google\" or \"Bing\"")
                .show();
    }

    public void startingOrbotInfo(Context application_context)
    {
        new LovelyInfoDialog(application_context)
                .setTopColorRes(R.color.header)
                .setIcon(R.drawable.logo)
                .setTitle("Orbot is Starting")
                .setMessage("Looks Like Orbot is Installed but not Running. Please wait while we Start Orbot for you")
                .show();
    }

    public void versionWarning(Context application_context)
    {
        new LovelyStandardDialog(application_context)
                .setTopColorRes(R.color.header)
                .setIcon(R.drawable.logo)
                .setTitle("Update Application")
                .setMessage("A newer version is availabe please install to get better experience")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        String url = "http://boogle.store/android";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        application_context.startActivity(i);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();

    }

}
