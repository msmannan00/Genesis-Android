package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.io.File;
import java.util.Collections;
import java.util.List;

class messageManager
{
    /*Private Variables*/

    private boolean is_popup_open = false;
    private CFAlertDialog.Builder popup_instance;
    private List<String> data;
    private boolean isDialogDismissed = true;

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;
    private CFAlertDialog dialog_main = null;

    /*Initializations*/

    messageManager(eventObserver.eventListener event)
    {
        this.event = event;
        initialize();
    }

    private void initialize()
    {
        popup_instance = new CFAlertDialog.Builder(app_context);
    }

    private void onDismissListener()
    {
        popup_instance.onDismissListener(dialogInterface -> is_popup_open = false);
    }

    /*Helper Methods*/
    private void welcomeMessage()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(app_context.getString(R.string.WELCOME_MESSAGE_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha_v1))
                .setTextColor(app_context.getResources().getColor(R.color.blue_dark))
                .setMessage(app_context.getString(R.string.WELCOME_MESSAGE_DESC))
                .onDismissListener(dialog -> is_popup_open = false)
                .addButton(app_context.getString(R.string.WELCOME_MESSAGE_BT_1), -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (DialogInterface tempDialog, int which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.BLACK_MARKET_URL), enums.etype.welcome);
                })
                .addButton(app_context.getString(R.string.WELCOME_MESSAGE_BT_2), -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.LEAKED_DOCUMENT_URL), enums.etype.welcome);
                })
                .addButton(app_context.getString(R.string.WELCOME_MESSAGE_BT_3), -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.NEWS_URL), enums.etype.welcome);
                })
                .addButton(app_context.getString(R.string.WELCOME_MESSAGE_BT_4), -1, Color.rgb(77,136,255 ), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    tempDialog.dismiss();
                    event.invokeObserver(Collections.singletonList(constants.SOFTWARE_URL), enums.etype.welcome);
                })
                .addButton(app_context.getString(R.string.WELCOME_MESSAGE_BT_5), -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.cancel_welcome);
                    tempDialog.dismiss();
                });
    }

    private void abiErrorRestart()
    {
        is_popup_open = false;
        final Handler handler = new Handler();
        Runnable runnable = () ->
        {
            is_popup_open = false;
            createMessage(app_context,Collections.singletonList(strings.EMPTY_STR), enums.etype.abi_error);
        };
        handler.postDelayed(runnable, 1500);

    }

    private void abiError()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.ABI_ERROR_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> abiErrorRestart())
                .setMessage(app_context.getString(R.string.ABI_ERROR_DESC))
                .addButton(app_context.getString(R.string.ABI_ERROR_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.GENESIS_UPDATE_URL + status.current_ABI));
                    if(browserIntent.resolveActivity(app_context.getPackageManager()) != null)
                    {
                        app_context.startActivity(browserIntent);
                    }else {
                        helperMethod.showToastMessage("Not Supported",app_context);
                    }
                })
                .addButton(app_context.getString(R.string.ABI_ERROR_BT_2), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.PLAYSTORE_URL));
                    if(browserIntent.resolveActivity(app_context.getPackageManager()) != null)
                    {
                        app_context.startActivity(browserIntent);
                    }else {
                        helperMethod.showToastMessage("Playstore Not Found",app_context);
                    }
                })
                /*
                .addButton(app_context.getString(R.string.ABI_ERROR_BT_3, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.ignore_abi);
                    tempDialog.dismiss();
                })*/;


    }

    private void ratedSuccessfully()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.RATE_SUCCESS_TITLE))
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(app_context.getString(R.string.RATE_SUCCESS_DESC))
                .addButton(app_context.getString(R.string.RATE_SUCCESS_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    tempDialog.dismiss();
                    onFinish();
                    final Handler handler = new Handler();
                    Runnable runnable = () ->
                    {
                        try{
                            helperMethod.sendRateEmail(app_context);
                        }
                        catch (Exception ex){
                            createMessage(app_context,Collections.singletonList(app_context.getString(R.string.NOT_SUPPORTED_MESSAGE)),enums.etype.on_not_support);
                        }
                    };
                    handler.postDelayed(runnable, 250);
                });
    }

    private void reportedSuccessfully()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.REPORT_SUCCESS_TITLE))
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(app_context.getString(R.string.REPORT_SUCCESS_DESC))
                .addButton(app_context.getString(R.string.REPORT_SUCCESS_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                        onFinish());
    }

    private void notSupportMessage()
    {
        String message = data.get(0);
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.NOT_SUPPORTED_TITLE))
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(message)
                .addButton(app_context.getString(R.string.NOT_SUPPORTED_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                        onFinish());
    }

    @SuppressLint("ResourceType")
    private void bookmark()
    {
        final EditText input = new EditText(app_context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText("");
        input.setBackground(ContextCompat.getDrawable(app_context, R.xml.sc_popup_input));
        input.setPadding(40, 15, 40, 15);
        input.setHeight(80);
        input.setTextSize(17);
        input.setHint("Title...");

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setHeaderView(input)
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage("Bookmark URL | " + data.get(0) + "\n")
                .addButton(app_context.getString(R.string.BOOKMARK_URL_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0).replace("genesis.onion","boogle.store")+"split"+input.getText().toString()), enums.etype.bookmark);

                    onFinish();
                });
    }

    private void clearHistory()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.CLEAR_HISTORY_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(app_context.getString(R.string.CLEAR_HISTORY_DESC))
                .addButton(app_context.getString(R.string.CLEAR_HISTORY_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    onFinish();
                    final Handler handler = new Handler();
                    Runnable runnable = () ->
                    {
                        is_popup_open = false;
                        event.invokeObserver(null, enums.etype.clear_history);
                    };
                    handler.postDelayed(runnable, 250);
                });
    }

    private void clearTabs()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.CLEAR_TAB_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(app_context.getString(R.string.CLEAR_TAB_DESC))
                .addButton(app_context.getString(R.string.CLEAR_TAB_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    onFinish();
                    final Handler handler = new Handler();
                    Runnable runnable = () ->
                    {
                        is_popup_open = false;
                        event.invokeObserver(null, enums.etype.clear_tab);
                    };
                    handler.postDelayed(runnable, 250);
                });
    }

    private void clearBookmark()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.clear_bookmark_title))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(app_context.getString(R.string.clear_bookmark_desc))
                .addButton(app_context.getString(R.string.clear_bookmark_bt1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    is_popup_open = false;
                    event.invokeObserver(null, enums.etype.clear_bookmark);
                    onFinish();
                });
    }

    private void reportURL()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.REPORT_URL_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(app_context.getString(R.string.REPORT_URL_DESC))
                .addButton(app_context.getString(R.string.REPORT_URL_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    onFinish();

                    final Handler handler = new Handler();
                    Runnable runnable = () -> createMessage(app_context,Collections.singletonList(strings.EMPTY_STR), enums.etype.reported_success);
                    handler.postDelayed(runnable, 1000);

                });


    }

    private void rateApp()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(app_context.getString(R.string.RATE_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha_v1))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .onDismissListener(dialog -> is_popup_open = false)
                .setMessage(app_context.getString(R.string.RATE_MESSAGE))
                .addButton(app_context.getString(R.string.RATE_POSITIVE), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.app_rated);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine"));
                    if(intent.resolveActivity(app_context.getPackageManager()) != null)
                    {
                        app_context.startActivity(intent);
                    }else {
                        helperMethod.showToastMessage("Playstore Not Found",app_context);
                    }
                    onFinish();
                })
                .addButton(app_context.getString(R.string.RATE_NEGATIVE), -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.app_rated);
                    onFinish();
                    final Handler handler = new Handler();
                    handler.postDelayed(() ->
                            createMessage(app_context,Collections.singletonList(strings.EMPTY_STR), enums.etype.rate_success), 1000);
                });
    }

    private void downloadFile()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.DOWNLOAD_TITLE))
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(app_context.getString(R.string.DOWNLOAD_MESSAGE) + data.get(0))
                .addButton(app_context.getString(R.string.DOWNLOAD_POSITIVE), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) ->
                {
                    event.invokeObserver(null, enums.etype.download_file);
                    onFinish();
                });
    }

    @SuppressLint("ResourceAsColor")
    private void downloadFileLongPress()
    {
        File f = new File(data.get(0));
        String name = f.getName();
        String title = data.get(1);

        int size = name.length();
        if(size>35){
            size = 35;
        }


        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(title + f.getName().substring(0,size)+"...")
                .setTextGravity(Gravity.START)
                .addButton(app_context.getString(R.string.LONG_URL_OPTION_4), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.download_file_manual);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_OPTION_1), -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_new_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_OPTION_2), -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_current_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_OPTION_3), -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
                    onFinish();
                });
    }

    private void openURLLongPress()
    {

        int size = data.get(0).length()-1;
        String title = data.get(1);

        if(size>35){
            size = 35;
        }

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(title + data.get(0).substring(0,size)+"...")
                .addButton(app_context.getString(R.string.LONG_URL_OPTION_1), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_new_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_OPTION_2), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_current_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_OPTION_3), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
                    onFinish();
                });
    }

    private void onResetApp()
    {
        is_popup_open = true;
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.ORBOT_CLEAR_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(app_context.getString(R.string.ORBOT_CLEAR_DESC))
                .onDismissListener(dialog -> is_popup_open = false)
                .addButton(app_context.getString(R.string.ORBOT_CLEAR_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                        onFinish());
    }

    private void popupDownloadFull(){

        String url = data.get(0);
        String file = data.get(1);
        String title = data.get(2);

        String data_local = app_context.getString(R.string.LONG_URL_MESSAGE);

        int size = url.length();
        if(size>35){
            size = 35;
        }

        int size1 = file.length();
        if(size1>35){
            size1 = 35;
        }

        if(!url.equals("")){
            data_local = title + url.substring(0,size)+"...";
        }
        else if(!file.equals("")){
            data_local = title + file.substring(0,size1)+"...";
        }

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)

                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(data_local)

                .addButton(app_context.getString(R.string.LONG_URL_FULL_OPTION_1), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(url), enums.etype.open_link_new_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_FULL_OPTION_2), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(url), enums.etype.open_link_current_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_FULL_OPTION_3), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(url), enums.etype.copy_link);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_FULL_OPTION_7), -1, Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.download_file_manual);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_FULL_OPTION_4), -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.open_link_new_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_FULL_OPTION_5), -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.open_link_current_tab);
                    onFinish();
                })
                .addButton(app_context.getString(R.string.LONG_URL_FULL_OPTION_6), -1,  Color.rgb(242,242,242 ), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
                {
                    event.invokeObserver(Collections.singletonList(file), enums.etype.copy_link);
                    onFinish();
                });
    }

    private void startingOrbotInfo()
    {
            is_popup_open = true;
            popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle(app_context.getString(R.string.ORBOT_INIT_TITLE))
                    .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                    .setTextColor(app_context.getResources().getColor(R.color.black))
                    .setMessage(app_context.getString(R.string.ORBOT_INIT_DESC))
                    .onDismissListener(dialog -> is_popup_open = false)
                    .addButton(app_context.getString(R.string.ORBOT_INIT_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                            onFinish()).addButton(app_context.getString(R.string.ORBOT_INIT_BT_2), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) ->
            {
                onFinish();

                final Handler handler = new Handler();
                handler.postDelayed(() ->
                        event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.reload), 250);

            });
    }

    private void sendBridgeMail()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.BRIDGE_MAIL_TITLE))
                .onDismissListener(dialog -> is_popup_open = false)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(app_context.getString(R.string.BRIDGE_MAIL_MESSAGE))
                .addButton(app_context.getString(R.string.BRIDGE_MAIL_POSITIVE), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) ->
                {
                    onFinish();
                    final Handler handler = new Handler();
                    Runnable runnable = () ->
                    {
                        try
                        {
                            helperMethod.sendBridgeEmail(app_context);
                            onFinish();
                        }catch (Exception ex){
                            createMessage(app_context,Collections.singletonList(app_context.getString(R.string.NOT_SUPPORTED_MESSAGE)),enums.etype.on_not_support);
                        }
                    };
                    handler.postDelayed(runnable, 250);
                });
    }

    private void versionWarning()
    {
        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .onDismissListener(dialog -> is_popup_open = false)
                .setTitle(app_context.getString(R.string.VERSION_TITLE))
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(app_context.getString(R.string.VERSION_DESC))
                .addButton(app_context.getString(R.string.VERSION_BT_1), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (tempDialog, which) ->
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.GENESIS_UPDATE_URL + data.get(0)));
                    app_context.startActivity(browserIntent);
                });
    }

    private void torBanned()
    {
        /*isDialogDismissed = true;

        popup_instance.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(app_context.getString(R.string.BANNED_TITLE)
                .setBackgroundColor(app_context.getResources().getColor(R.color.holo_dark_gray_alpha))
                .setTextColor(app_context.getResources().getColor(R.color.black))
                .setMessage(app_context.getString(R.string.BANNED_DESC)
                .onDismissListener(dialog -> is_popup_open = false)
                .onDismissListener(dialog -> startHome());

                String btn_text;

                if(status.sGateway){
                    btn_text = "Disable Tor Gateway";
                }
                else {
                    btn_text = "Enable Tor Gateway";
                }

                popup_instance.addButton(btn_text, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (tempDialog, which) ->
                {
                    isDialogDismissed = false;
                    onFinish();
                    event.invokeObserver(Collections.singletonList(!status.sGateway), enums.etype.connect_vpn);
                });*/
    }

    private void startHome(){
        if(!isDialogDismissed && data.get(0)==null){
            event.invokeObserver(null, enums.etype.start_home);
        }
        is_popup_open = false;
    }

    void onReset(){
        onFinish();
        dialog_main = null;
    }

    private void onFinish(){
        if(dialog_main!=null && dialog_main.isShowing() && !app_context.isFinishing() && !app_context.isFinishing()){
            dialog_main.dismiss();
        }
    }

    /*External Helper Methods*/

    void createMessage(AppCompatActivity app_context, List<String> data, enums.etype type)
    {
        this.app_context = app_context;
        this.data = data;
        if (!is_popup_open && !app_context.isDestroyed())
        {
            is_popup_open = true;
            popup_instance = new CFAlertDialog.Builder(app_context);

            onDismissListener();
            switch (type)
            {
                case welcome:
                    welcomeMessage();
                    break;

                case abi_error:
                    abiError();
                    break;

                case rate_success:
                    ratedSuccessfully();
                    break;

                case reported_success:
                    reportedSuccessfully();
                    break;

                case bookmark:
                    bookmark();
                    break;

                case clear_history:
                    clearHistory();
                    break;

                case clear_tab:
                    clearTabs();
                    break;

                case clear_bookmark:
                    clearBookmark();
                    break;

                case report_url:
                    reportURL();
                    break;

                case rate_app:
                    rateApp();
                    break;

                case download_file:
                    downloadFile();
                    break;

                case start_orbot:
                    startingOrbotInfo();
                    break;

                case version_warning:
                    versionWarning();
                    break;

                case tor_banned:
                    torBanned();
                    break;

                case download_file_long_press:
                    downloadFileLongPress();
                    break;

                case on_long_press_url:
                    openURLLongPress();
                    break;

                case on_long_press_with_link:
                    popupDownloadFull();
                    break;

                case on_bridge_mail:
                    sendBridgeMail();
                    break;

                case on_not_support:
                    notSupportMessage();
                    break;
            }

            dialog_main = popup_instance.show();
        }
    }
}
