package com.darkweb.genesissearchengine.pluginManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.darkweb.genesissearchengine.appManager.app_model;
import com.darkweb.genesissearchengine.appManager.application_controller;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
import com.darkweb.genesissearchengine.httpManager.serverRequestManager;
import com.example.myapplication.R;

public class message_manager
{
    /*Initializations*/
    private boolean isPopupOn = false;

    private static final message_manager ourInstance = new message_manager();

    public static message_manager getInstance()
    {
        return ourInstance;
    }

    private message_manager()
    {
    }

    /*Helper Methods*/
    public void welcomeMessage()
    {
        Context application_context = app_model.getInstance().getAppContext();
        application_controller controller = app_model.getInstance().getAppInstance();

            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle(strings.welcome_message_title)
                    .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                    .setTextColor(application_context.getResources().getColor(R.color.blue_dark))
                    .setMessage(strings.welcome_message_desc)
                    .addButton(strings.welcome_message_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.blackMarket,false);
                        controller.onloadURL(constants.blackMarket,false);
                    })
                    .addButton(strings.welcome_message_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.leakedDocument,false);
                        controller.onloadURL(constants.leakedDocument,false);
                    })
                    .addButton(strings.welcome_message_bt3, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.news,false);
                        controller.onloadURL(constants.news,false);
                    })
                    .addButton(strings.welcome_message_bt4, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.softwares,false);
                        controller.onloadURL(constants.softwares,false);
                    })
                    .addButton(strings.welcome_message_bt5, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        preference_manager.getInstance().saveBool(keys.first_time_loaded,true);
                    });
            builder.show();

    }


    public void baseURLError()
    {
        Context application_context = app_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.base_error_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.base_error_desc)
                .addButton(strings.base_error_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void abiError(String currentAbi)
    {
        Context application_context = app_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.abi_error_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .onDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        abiError(currentAbi);
                    }
                })
                .setMessage(strings.abi_error_desc)
                .addButton(strings.abi_error_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.updateUrl+currentAbi));
                    application_context.startActivity(browserIntent);
                })
                .addButton(strings.abi_error_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.playstoreUrl));
                    application_context.startActivity(browserIntent);
                });

        builder.show();
    }

    public void reportedSuccessfully()
    {
        Context application_context = app_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.report_success_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.report_success_desc)
                .addButton(strings.report_success_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void reportURL()
    {
        Context application_context = app_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.report_url_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.report_url_desc)
                .addButton(strings.report_url_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                    //String reportURL = constants.reportUrl+URL;
                    //serverRequestManager.getInstance().reportURL(reportURL);
                    dialog.dismiss();
                    reportedSuccessfully();
                })
                .addButton(strings.report_url_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void startingOrbotInfo(String url)
    {
        if(!isPopupOn)
        {
            isPopupOn = true;
            Context application_context = app_model.getInstance().getAppContext();
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle(strings.orbot_init_title)
                    .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                    .setTextColor(application_context.getResources().getColor(R.color.black))
                    .setMessage(strings.orbot_init_desc)
                    .onDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            isPopupOn = false;
                        }
                    })
                    .addButton(strings.orbot_init_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                        dialog.dismiss();
                        isPopupOn = false;
                    }).addButton(strings.orbot_init_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                        dialog.dismiss();
                        isPopupOn = false;

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                if(!url.equals(""))
                                {
                                    app_model.getInstance().getAppInstance().onloadURL(url,true);
                                }
                                else
                                {
                                    app_model.getInstance().getAppInstance().onReload();
                                }
                            }
                        }, 500);

                    });

            builder.show();
        }
    }

    public void versionWarning(String currentAbi)
    {
        Context application_context = app_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.version_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.version_desc)
                .addButton(strings.version_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.updateUrl+currentAbi));
                    application_context.startActivity(browserIntent);
                });

        builder.show();
    }

}
