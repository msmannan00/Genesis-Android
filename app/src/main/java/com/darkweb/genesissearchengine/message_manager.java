package com.darkweb.genesissearchengine;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import com.crowdfire.cfalertdialog.CFAlertDialog;

public class message_manager {
    private static final message_manager ourInstance = new message_manager();

    public static message_manager getInstance() {
        return ourInstance;
    }

    private message_manager() {
    }

    public void welcomeMessage(Context application_context, application_controller controller)
    {
        if(!helperMethod.readPrefs("FirstTimeLoaded",application_context))
        {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Welcome | Deep Web Gateway")
                    .setBackgroundColor(Color.argb(230,33,45,69))
                    .setTextColor(Color.argb(255,0,38,77))
                    .setMessage("\nWelcome to Deep Web | Dark Web Gateway. This application provide you a platform to Search and Open Dark Web urls.\n\nYou cannot open any url related to normal internet as its not the intended purpose. You can check out following urls to get yourself started\n\nHere are few Suggestions\n")

                    .addButton("Deep Web Online Market", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.initializePopupView("https://boogle.store/search?q=black+market&p_num=1&s_type=all");
                        controller.loadURLAnimate("https://boogle.store/search?q=black+market&p_num=1&s_type=all");
                    })
                    .addButton("Leaked Documents and Books", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.initializePopupView("https://boogle.store/search?q=leaked+document&p_num=1&s_type=all&p_num=1&s_type=all");
                        controller.loadURLAnimate("https://boogle.store/search?q=leaked+document&p_num=1&s_type=all&p_num=1&s_type=all");
                    })
                    .addButton("Dark Web News and Articles", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.initializePopupView("https://boogle.store/search?q=latest%20news&p_num=1&s_type=news");
                        controller.loadURLAnimate("https://boogle.store/search?q=latest%20news&p_num=1&s_type=news");
                    })
                    .addButton("Secret Softwares and Hacking Tools", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.initializePopupView("https://boogle.store/search?q=softwares+tools&p_num=1&s_type=all&p_num=1&s_type=all");
                        controller.loadURLAnimate("https://boogle.store/search?q=softwares+tools&p_num=1&s_type=all&p_num=1&s_type=all");
                    })
                    .addButton("Don't Show Again", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        helperMethod.savePrefs("FirstTimeLoaded",true,application_context);
                    });
            builder.show();
        }


        // Create Alert using Builder
        /*
        new LovelyStandardDialog(application_context)
                .setTopColorRes(R.color.header)
                .setTopTitleColor(Color.argb(255,255,255,255))
                .setTopTitle("Welcome")
                .setMessage("This software can only be used to access hidden web such as \"Onion\" and \"I2P\" \n\nFor accessing Surface Web use Google or Bing")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override/
                    public void onClick(View v)
                    {
                    }
                })
                .setNegativeButton("Don't Show Again", null)
                .show();*/
    }


    public void baseURLError(Context application_context)
    {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("Dark Web URL | Invalid URL")
                .setBackgroundColor(Color.argb(230,33,45,69))
                .setTextColor(Color.argb(255,255,255,255))
                .setMessage("This software can only be used to access hidden web such as \"Onion\" and \"I2P\" \n\nFor accessing Surface Web use Google or Bing\n")
                .addButton("Dismiss", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void illegalWarningDialog(Context application_context,application_controller controller)
    {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("Illegal Activity Detected")
                .setBackgroundColor(Color.argb(255,33,45,69))
                .setTextColor(Color.argb(255,255,255,255))
                .setMessage("This software can only be used to access hidden web such as \"Onion\" and \"I2P\" \n\nFor accessing Surface Web use Google or Bing\n")
                .addButton("Go Back", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    dialog.dismiss();
                })
                .addButton("Dismiss", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    dialog.dismiss();
                    controller.backPressed();
                });

        builder.show();
    }

    public void URLNotFoundError(Context application_context)
    {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("Dark Web URL | Invalid")
                .setBackgroundColor(Color.argb(230,33,45,69))
                .setTextColor(Color.argb(255,255,255,255))
                .setMessage("This software can only be used to access hidden web such as \"Onion\" and \"I2P\" \n\nFor accessing Surface Web use Google or Bing\n")
                .addButton("Dismiss", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void reportedSuccessfully(Context application_context)
    {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("URL Reported Successfully")
                .setBackgroundColor(Color.argb(230,33,45,69))
                .setTextColor(Color.argb(255,255,255,255))
                .setMessage("URL has been successfully reported. It will take about a week to completely remove this website from our servers\n")
                .addButton("Dismiss", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void reportURL(Context application_context,String URL)
    {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("Report This Website")
                .setBackgroundColor(Color.argb(230,33,45,69))
                .setTextColor(Color.argb(255,255,255,255))
                .setMessage("If you think url is illegal or disturbing report us so that we can update our database\n")
                .addButton("Report", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                    String reportURL = "https://boogle.store/reportus?r_key="+URL;
                    webRequestHandler.getInstance().reportURL(reportURL);
                    dialog.dismiss();
                    reportedSuccessfully(application_context);
                })
                .addButton("Dismiss", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void startingOrbotInfo(Context application_context)
    {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("Initializing Dark Web")
                .setBackgroundColor(Color.argb(230,33,45,69))
                .setTextColor(Color.argb(255,255,255,255))
                .setMessage("Please wait! While we connect you to hidden web. This might take few seconds\n")
                .addButton("Dismiss", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void versionWarning(Context application_context)
    {/*
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
        */
    }

}
