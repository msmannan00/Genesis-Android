package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.InputType;
import android.widget.EditText;
import androidx.core.content.ContextCompat;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.darkweb.genesissearchengine.appManager.home_activity.home_model;
import com.darkweb.genesissearchengine.appManager.list_manager.list_model;
import com.darkweb.genesissearchengine.appManager.home_activity.home_controller;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.dataManager.preference_manager;
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
        Context application_context = home_model.getInstance().getAppContext();
        home_controller controller = home_model.getInstance().getHomeInstance();

            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle(strings.welcome_message_title)
                    .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                    .setTextColor(application_context.getResources().getColor(R.color.blue_dark))
                    .setMessage(strings.welcome_message_desc)
                    .addButton(strings.welcome_message_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.blackMarket,false,true);
                        controller.onloadURL(constants.blackMarket,false,true);
                    })
                    .addButton(strings.welcome_message_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.leakedDocument,false,true);
                        controller.onloadURL(constants.leakedDocument,false,true);
                    })
                    .addButton(strings.welcome_message_bt3, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.news,false,true);
                        controller.onloadURL(constants.news,false,true);
                    })
                    .addButton(strings.welcome_message_bt4, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        controller.onloadURL(constants.softwares,false,true);
                        controller.onloadURL(constants.softwares,false,true);
                    })
                    .addButton(strings.welcome_message_bt5, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , (dialog, which) -> {
                        dialog.dismiss();
                        preference_manager.getInstance().setBool(keys.first_time_loaded,true);
                    });
            builder.show();

    }


    public void baseURLError()
    {
        Context application_context = home_model.getInstance().getAppContext();
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
        Context application_context = home_model.getInstance().getAppContext();
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

    public void ratedSuccessfully()
    {
        Context application_context = home_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.rate_success_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.rate_success_desc)
                .addButton(strings.rate_success_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void reportedSuccessfully(String title,String desc)
    {
        Context application_context = home_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(desc)
                .addButton(strings.report_success_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    @SuppressLint("ResourceType")
    public void bookmark(String url)
    {

        final EditText input = new EditText(home_model.getInstance().getHomeInstance());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText("");
        input.setBackground(ContextCompat.getDrawable(home_model.getInstance().getAppContext(), R.xml.search_back_default));
        input.setPadding(40,15,40,15);
        input.setHeight(80);
        input.setTextSize(17);
        input.setHint("Enter Bookmark Title");

        Context application_context = home_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                //.setTitle(strings.bookmark_url_title)
                .setHeaderView(input)
                .setMessage("Bookmark URL | " + url + "\n")
                .addButton(strings.bookmark_url_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    home_model.getInstance().addBookmark(url.replace("genesis.onion","boogle.store"),input.getText().toString());
                    dialog.dismiss();
                })
                .addButton(strings.bookmark_url_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();

    }

    public void clearData()
    {
        Context application_context = list_model.getInstance().getListContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(list_model.getInstance().getListContext())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.clear_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.clear_desc)
                .addButton(strings.clear_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    list_model.getInstance().getListInstance().onClearAll();
                    dialog.dismiss();
                })
                .addButton(strings.clear_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void reportURL()
    {
        Context application_context = home_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.report_url_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.report_url_desc)
                .addButton(strings.report_url_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                    reportedSuccessfully(strings.report_success_title,strings.report_success_desc);
                })
                .addButton(strings.report_url_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void rateApp()
    {
        Context application_context = home_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(strings.rate_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.rate_message)
                .addButton(strings.rate_positive, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    preference_manager.getInstance().setBool(keys.isAppRated,true);
                    home_model.getInstance().getHomeInstance().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine")));
                    dialog.dismiss();
                })
                .addButton(strings.rate_negative, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    preference_manager.getInstance().setBool(keys.isAppRated,true);
                    dialog.dismiss();
                    ratedSuccessfully();
                });

        builder.show();
    }

    public void downloadFile(String filename)
    {
        Context application_context = home_model.getInstance().getAppContext();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.download_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.download_message + filename)
                .addButton(strings.download_positive, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    home_model.getInstance().getHomeInstance().downloadFile();
                    dialog.dismiss();
                })
                .addButton(strings.download_negative, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    public void startingOrbotInfo(String url)
    {
        if(!isPopupOn)
        {
            isPopupOn = true;
            Context application_context = home_model.getInstance().getAppContext();
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
                                    home_model.getInstance().getHomeInstance().onloadURL(url,true,true);
                                }
                                else
                                {
                                    home_model.getInstance().getHomeInstance().onReload();
                                }
                            }
                        }, 500);

                    });

            builder.show();
        }
    }

    public void versionWarning(String currentAbi)
    {
        Context application_context = home_model.getInstance().getAppContext();
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
