package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
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

    private List<String> data;

    private AppCompatActivity app_context;
    private eventObserver.eventListener event;


    Dialog dialog = null;

    /*Initializations*/

    messageManager(eventObserver.eventListener event)
    {
        this.event = event;
    }

    /*Helper Methods*/
    private void welcomeMessage()
    {
        initializeDialog(R.layout.popup_welcome, Gravity.CENTER);
        dialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(constants.CONST_BLACK_MARKET_URL), enums.etype.welcome);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(constants.CONST_LEAKED_DOCUMENT_URL), enums.etype.welcome);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(constants.CONST_NEWS_URL), enums.etype.welcome);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption4).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(constants.CONST_SOFTWARE_URL), enums.etype.welcome);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption5).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(constants.CONST_SOFTWARE_FINANCE), enums.etype.welcome);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption6).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(constants.CONST_COMMUNITIES), enums.etype.welcome);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pDontShowAgain).setOnClickListener(v -> {
            event.invokeObserver(null, enums.etype.cancel_welcome);
            dialog.dismiss();
        });
    }

    private void abiError()
    {
        initializeDialog(R.layout.popup_abi_error, Gravity.CENTER);
        dialog.findViewById(R.id.pOption1).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            dialog.dismiss();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.CONST_GENESIS_UPDATE_URL + status.sAppCurrentABI));
            if(browserIntent.resolveActivity(app_context.getPackageManager()) != null)
            {
                app_context.startActivity(browserIntent);
            }else {
                helperMethod.showToastMessage("Not Supported",app_context);
            }
        });
        dialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            dialog.dismiss();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.CONST_PLAYSTORE_URL));
            if(browserIntent.resolveActivity(app_context.getPackageManager()) != null)
            {
                app_context.startActivity(browserIntent);
            }else {
                helperMethod.showToastMessage("Playstore Not Found",app_context);
            }
        });
    }

    private void rateFailure()
    {
        initializeDialog(R.layout.popup_rate_failure, Gravity.CENTER);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                try{
                    helperMethod.sendRateEmail(app_context);
                }
                catch (Exception ex){
                    createMessage(app_context,Collections.singletonList(app_context.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)),enums.etype.on_not_support);
                }
            };
            handler.postDelayed(runnable, 1000);
        });
    }

    private void reportedSuccessfully()
    {
        initializeDialog(R.layout.popup_reported_successfully, Gravity.CENTER);
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> dialog.dismiss());
    }

    private void notSupportMessage()
    {
        initializeDialog(R.layout.popup_not_supported, Gravity.BOTTOM);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
    }

    @SuppressLint("ResourceType")
    private void bookmark()
    {
        initializeDialog(R.layout.popup_create_bookmark, Gravity.CENTER);
        EditText mBoomMarkTitle = dialog.findViewById(R.id.pBookmark);
        dialog.setOnDismissListener(dialog -> {
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                helperMethod.hideKeyboard(activityContextManager.getInstance().getHomeController());
                dialog.dismiss();
            };
            handler.postDelayed(runnable, 50);
        });
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> {
            dialog.dismiss();
            helperMethod.hideKeyboard(activityContextManager.getInstance().getHomeController());
        });

        mBoomMarkTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) app_context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            helperMethod.hideKeyboard(app_context);
            event.invokeObserver(Collections.singletonList(data.get(0).replace("genesis.onion","boogle.store")+"split"+((EditText)dialog.findViewById(R.id.pBookmark)).getText().toString()), enums.etype.bookmark);
        });
    }

    private void initializeDialog(int pLayout,int pGravity){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }

        dialog = new Dialog(app_context);
        dialog.getWindow().setGravity(pGravity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialiog_animation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(pLayout);
        dialog.show();
    }

    private void clearHistory()
    {
        initializeDialog(R.layout.popup_clear_history, Gravity.CENTER);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            event.invokeObserver(null, enums.etype.clear_history);
        });
    }

    private void clearBookmark()
    {
        initializeDialog(R.layout.popup_clear_bookmark, Gravity.CENTER);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            event.invokeObserver(null, enums.etype.clear_bookmark);
        });
    }

    private void reportURL()
    {
        initializeDialog(R.layout.popup_report_url, Gravity.CENTER);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> createMessage(app_context,Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.reported_success);
            handler.postDelayed(runnable, 1000);
        });
    }

    private void rateApp()
    {
        initializeDialog(R.layout.popup_rate_us, Gravity.CENTER);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            RatingBar mRatingBar = dialog.findViewById(R.id.pRating);
            if(mRatingBar.getRating()>=3){
                event.invokeObserver(null, enums.etype.app_rated);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine"));
                if(intent.resolveActivity(app_context.getPackageManager()) != null)
                {
                    app_context.startActivity(intent);
                }else {
                    helperMethod.showToastMessage("Playstore Not Found",app_context);
                }
                dialog.dismiss();
            }else if(mRatingBar.getRating()>0) {
                event.invokeObserver(null, enums.etype.app_rated);
                final Handler handler = new Handler();
                handler.postDelayed(() -> createMessage(app_context,Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.rate_failure), 1000);
                dialog.dismiss();
            }
        });
    }

    private void downloadFile()
    {
        initializeDialog(R.layout.popup_download_file, Gravity.BOTTOM);
        ((TextView)dialog.findViewById(R.id.pDescription)).setText((app_context.getString(R.string.ALERT_DOWNLOAD_MESSAGE) + data.get(0)));
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
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

        initializeDialog(R.layout.popup_file_longpress, Gravity.CENTER);
        ((TextView)dialog.findViewById(R.id.pDescription)).setText((title + data.get(0).substring(0,size)+"..."));
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.download_file_manual);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_current_tab);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
    }

    private void openURLLongPress()
    {
        int size = data.get(0).length()-1;
        String title = data.get(1);

        if(size>35){
            size = 35;
        }

        initializeDialog(R.layout.popup_url_longpress, Gravity.CENTER);
        ((TextView)dialog.findViewById(R.id.pDescription)).setText((title + data.get(0).substring(0,size)+"..."));
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_new_tab);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_current_tab);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
    }

    private void popupDownloadFull(){

        String url = data.get(0);
        String file = data.get(1);
        String title = data.get(2);

        String data_local = app_context.getString(R.string.ALERT_LONG_URL_MESSAGE);

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


        initializeDialog(R.layout.popup_url_longpress, Gravity.CENTER);
        ((TextView)dialog.findViewById(R.id.pHeader)).setText((title + data.get(0).substring(0,size)+"..."));
        ((TextView)dialog.findViewById(R.id.pDescription)).setText((data_local));
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_new_tab);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.open_link_current_tab);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            event.invokeObserver(Collections.singletonList(data.get(0)), enums.etype.copy_link);
            dialog.dismiss();
        });
    }

    private void startingOrbotInfo()
    {
        initializeDialog(R.layout.popup_starting_orbot_info, Gravity.BOTTOM);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
    }

    private void sendBridgeMail()
    {
        initializeDialog(R.layout.popup_bridge_mail, Gravity.CENTER);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                try{
                    helperMethod.sendBridgeEmail(app_context);
                }
                catch (Exception ex){
                    createMessage(app_context,Collections.singletonList(app_context.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)),enums.etype.on_not_support);
                }
            };
            handler.postDelayed(runnable, 1000);
        });
    }

    void onReset(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    /*External Helper Methods*/

    void createMessage(AppCompatActivity app_context, List<String> data, enums.etype type)
    {
        this.app_context = app_context;
        this.data = data;

        switch (type)
        {
            case welcome:
                welcomeMessage();
                break;

            case abi_error:
                abiError();
                break;

            case rate_failure:
                rateFailure();
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
    }
}
