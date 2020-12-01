package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;

class messageManager
{
    /*Private Variables*/

    private List<Object> data;

    private AppCompatActivity mContext;
    private eventObserver.eventListener event;


    Dialog dialog = null;

    /*Initializations*/

    private void initializeDialog(int pLayout,int pGravity,int pFlag){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }

        dialog = new Dialog(mContext);
        dialog.getWindow().setGravity(pGravity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialiog_animation;

        Drawable myDrawable;
        Resources res = mContext.getResources();
        try {
            myDrawable = Drawable.createFromXml(res, res.getXml(R.xml.hox_rounded_corner));
            dialog.getWindow().setBackgroundDrawable(myDrawable);
        } catch (Exception ignored) {
        }

        dialog.setCancelable(true);
        dialog.setContentView(pLayout);
        dialog.show();
        //dialog.getWindow().clearFlags(pFlag);
    }

    messageManager(eventObserver.eventListener event)
    {
        this.event = event;
    }

    /*Helper Methods*/
    private void welcomeMessage()
    {
        initializeDialog(R.layout.popup_welcome, Gravity.CENTER, FLAG_DIM_BEHIND);
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

    @SuppressLint("QueryPermissionsNeeded")
    private void abiError()
    {
        initializeDialog(R.layout.popup_abi_error, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            dialog.dismiss();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.CONST_GENESIS_UPDATE_URL + status.sAppCurrentABI));
            if(browserIntent.resolveActivity(mContext.getPackageManager()) != null)
            {
                mContext.startActivity(browserIntent);
            }else {
                helperMethod.showToastMessage("Not Supported", mContext);
            }
        });
        dialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            dialog.dismiss();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(constants.CONST_PLAYSTORE_URL));
            if(browserIntent.resolveActivity(mContext.getPackageManager()) != null)
            {
                mContext.startActivity(browserIntent);
            }else {
                helperMethod.showToastMessage("Playstore Not Found", mContext);
            }
        });
    }

    private void rateFailure()
    {
        initializeDialog(R.layout.popup_rate_failure, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                try{
                    helperMethod.sendRateEmail(mContext);
                }
                catch (Exception ex){
                    createMessage(mContext,Collections.singletonList(mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)),enums.etype.on_not_support);
                }
            };
            handler.postDelayed(runnable, 1000);
        });
    }

    private void reportedSuccessfully()
    {
        initializeDialog(R.layout.popup_reported_successfully, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> dialog.dismiss());
    }

    private void notSupportMessage()
    {
        initializeDialog(R.layout.popup_not_supported, Gravity.BOTTOM, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
    }

    private void dataClearedSuccessfully()
    {
        initializeDialog(R.layout.popup_data_cleared, Gravity.BOTTOM, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
    }


    @SuppressLint("ResourceType")
    private void bookmark()
    {
        initializeDialog(R.layout.popup_create_bookmark, Gravity.CENTER, FLAG_DIM_BEHIND);
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
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            helperMethod.hideKeyboard(mContext);
            event.invokeObserver(Collections.singletonList(data.get(0).toString().replace("genesis.onion","boogle.store")+"split"+((EditText)dialog.findViewById(R.id.pBookmark)).getText().toString()), enums.etype.bookmark);
        });
    }

    private void clearHistory()
    {
        initializeDialog(R.layout.popup_clear_history, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            event.invokeObserver(null, enums.etype.clear_history);
        });
    }

    private void clearBookmark()
    {
        initializeDialog(R.layout.popup_clear_bookmark, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            event.invokeObserver(null, enums.etype.clear_bookmark);
        });
    }

    private void reportURL()
    {
        initializeDialog(R.layout.popup_report_url, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> createMessage(mContext,Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.reported_success);
            handler.postDelayed(runnable, 1000);
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void rateApp()
    {
        initializeDialog(R.layout.popup_rate_us, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            RatingBar mRatingBar = dialog.findViewById(R.id.pRating);
            if(mRatingBar.getRating()>=3){
                event.invokeObserver(null, enums.etype.app_rated);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesissearchengine"));
                if(intent.resolveActivity(mContext.getPackageManager()) != null)
                {
                    mContext.startActivity(intent);
                }else {
                    helperMethod.showToastMessage("Playstore Not Found", mContext);
                }
                dialog.dismiss();
            }else if(mRatingBar.getRating()>0) {
                event.invokeObserver(null, enums.etype.app_rated);
                final Handler handler = new Handler();
                handler.postDelayed(() -> createMessage(mContext,Collections.singletonList(strings.GENERIC_EMPTY_STR), enums.etype.rate_failure), 1000);
                dialog.dismiss();
            }
        });
    }

    private void downloadFile()
    {
        initializeDialog(R.layout.popup_download_file, Gravity.BOTTOM, FLAG_DIM_BEHIND);
        ((TextView)dialog.findViewById(R.id.pDescription)).setText((mContext.getString(R.string.ALERT_DOWNLOAD_MESSAGE) + data.get(0)));
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
    }

    @SuppressLint("ResourceAsColor")
    private void downloadFileLongPress()
    {
        File f = new File(data.get(0).toString());
        String name = f.getName();
        String title = data.get(1).toString();

        int size = name.length();
        if(size>235){
            size = 235;
        }

        initializeDialog(R.layout.popup_file_longpress, Gravity.CENTER, FLAG_DIM_BEHIND);
        ((TextView)dialog.findViewById(R.id.pDescription)).setText((title + " | " + data.get(0).toString().substring(0,size)+"..."));
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
        int size = data.get(0).toString().length()-1;
        String title = data.get(1).toString();

        if(size>235){
            size = 235;
        }

        initializeDialog(R.layout.popup_url_longpress, Gravity.CENTER, FLAG_DIM_BEHIND);
        ((TextView)dialog.findViewById(R.id.pDescription)).setText((title + data.get(0).toString().substring(0,size)+"..."));
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
        String url = data.get(0).toString();
        String file = data.get(1).toString();
        String title = data.get(2).toString();

        String data_local = mContext.getString(R.string.ALERT_LONG_URL_MESSAGE);

        int size = url.length();
        if(size>235){
            size = 235;
        }

        int size1 = file.length();
        if(size1>235){
            size1 = 235;
        }

        if(!url.equals("")){
            data_local = title + url.substring(0,size)+"...";
        }
        else if(!file.equals("")){
            data_local = file.substring(0,size1)+"...";
        }
        String mTitle = title;
        if(mTitle.length()<=1){
            mTitle = data.get(0).toString().substring(0,size)+"...";
        }

        initializeDialog(R.layout.popup_download_full, Gravity.CENTER, FLAG_DIM_BEHIND);
        ((TextView)dialog.findViewById(R.id.pHeader)).setText(mTitle);
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
        initializeDialog(R.layout.popup_starting_orbot_info, Gravity.BOTTOM, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
    }

    private void sendBridgeMail()
    {
        initializeDialog(R.layout.popup_bridge_mail, Gravity.CENTER, FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.pDismiss).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            dialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                try{
                    helperMethod.sendBridgeEmail(mContext);
                }
                catch (Exception ex){
                    createMessage(mContext,Collections.singletonList(mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)),enums.etype.on_not_support);
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

    void createMessage(AppCompatActivity app_context, List<Object> data, enums.etype type)
    {
        this.mContext = app_context;
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

            case data_cleared:
                dataClearedSuccessfully();
                break;
        }
    }
}
