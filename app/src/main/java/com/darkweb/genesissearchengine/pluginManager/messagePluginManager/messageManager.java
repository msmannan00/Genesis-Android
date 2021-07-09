package com.darkweb.genesissearchengine.pluginManager.messagePluginManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginEnums;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import org.mozilla.geckoview.ContentBlocking;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.constants.*;
import static com.darkweb.genesissearchengine.constants.strings.MESSAGE_PLAYSTORE_NOT_FOUND;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManager.*;
import static com.darkweb.genesissearchengine.pluginManager.pluginEnums.eMessageManagerCallbacks.*;

public class messageManager
{
    /*Private Variables*/

    private List<Object> mData;
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;
    private Dialog mDialog = null;

    /*Initializations*/

    private void onClearReference(){
        if(mContext!=null && !mContext.isDestroyed() && !mContext.isFinishing() && mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
        }
        if(mContext != null){
            mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mContext = null;
        }
    }

    private void initializeDialog(int pLayout, int pGravity){
        try {
            if(mDialog !=null && mDialog.isShowing()){
                mDialog.dismiss();
            }

            mDialog = new Dialog(mContext);
            mDialog.getWindow().setGravity(pGravity);
            mDialog.getWindow().getAttributes().windowAnimations = R.style.dialiog_animation;

            Drawable myDrawable;
            Resources res = mContext.getResources();
            try {
                myDrawable = Drawable.createFromXml(res, res.getXml(R.xml.hox_rounded_corner));
                mDialog.getWindow().setBackgroundDrawable(myDrawable);
            } catch (Exception ignored) {
            }

            mDialog.setCancelable(true);
            mDialog.setContentView(pLayout);

            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, helperMethod.pxFromDp(10),0,helperMethod.pxFromDp(10),0);
            mDialog.getWindow().setBackgroundDrawable(inset);
            mDialog.getWindow().setLayout(helperMethod.pxFromDp(350), -1);
            mDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            mDialog.show();
        }catch (Exception ignored){}
    }

    public messageManager(eventObserver.eventListener event)
    {
        this.mEvent = event;
    }

    /*Helper Methods*/

    private void languageSupportFailure()
    {
        initializeDialog(R.layout.popup_language_support, Gravity.CENTER);
        // ((TextView) mDialog.findViewById(R.id.pLanguage)).setText((mData.get(0).toString()));
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void rateFailure()
    {
        initializeDialog(R.layout.popup_rate_failure, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            mDialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                try{
                    helperMethod.sendIssueEmail(mContext);
                }
                catch (Exception ex)
                {
                    onTrigger(Arrays.asList(mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE), mContext),M_NOT_SUPPORTED);
                    onClearReference();
                }
            };
            handler.postDelayed(runnable, 1000);
        });
    }

    private void newIdentityCreated()
    {
        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        initializeDialog(R.layout.popup_new_circuit, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        handler.postDelayed(runnable, 1500);
    }

    private void mDownloadFailure()
    {
        mContext.runOnUiThread(() -> {
            final Handler handler = new Handler();
            Runnable runnable = () -> mDialog.dismiss();

            initializeDialog(R.layout.popup_download_failure, Gravity.BOTTOM);
            String mMessage;
            if(mData == null || mData.get(0) == null || (mData.get(0)).equals("0")){
                mMessage = "\"Unknown\"";
            }else {
                mMessage = (String) mData.get(0);
            }
            ((TextView)mDialog.findViewById(R.id.pDescription)).setText(("Request denied Error  " + mMessage));
            mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());

            mDialog.setOnDismissListener(dialog -> {
                handler.removeCallbacks(runnable);
                onClearReference();
            });

            handler.postDelayed(runnable, 20000);

        });
    }

    private void popupBlocked()
    {
        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        initializeDialog(R.layout.popup_block_popup, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pOpenPrivacy).setOnClickListener(v -> {
            mEvent.invokeObserver(null, M_OPEN_PRIVACY);
            mDialog.dismiss();
            handler.removeCallbacks(runnable);
        });

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        handler.postDelayed(runnable, 1500);
    }

    private void popupLoadNewTab()
    {
        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        initializeDialog(R.layout.popup_load_new_tab, Gravity.BOTTOM);
        mDialog.getWindow().setDimAmount(0.3f);
        mDialog.findViewById(R.id.pRestore).setOnClickListener(v -> {
            mEvent.invokeObserver(null, M_UNDO_SESSION);
            mDialog.dismiss();
            handler.removeCallbacks(runnable);
        });

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        handler.postDelayed(runnable, 1500);
    }

    private void popupUndo()
    {
        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        initializeDialog(R.layout.popup_undo, Gravity.BOTTOM);
        mDialog.getWindow().setDimAmount(0.3f);
        mDialog.findViewById(R.id.pUndo).setOnClickListener(v -> {
            mEvent.invokeObserver(null, M_UNDO_TAB);
            mDialog.dismiss();
            handler.removeCallbacks(runnable);
        });

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        handler.postDelayed(runnable, 1500);
    }

    private void onShowToast(int mLayout, int mDelay, String mInfo, String mTriggerText, pluginEnums.eMessageManagerCallbacks pCallback)
    {
        initializeDialog(mLayout, Gravity.BOTTOM);
        ((TextView)mDialog.findViewById(R.id.pDescription)).setText(mInfo);
        ((Button)mDialog.findViewById(R.id.pTrigger)).setText(mTriggerText);
        mDialog.getWindow().setDimAmount(0.3f);

        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> {
            if(pCallback == null){
                mEvent.invokeObserver(null, M_UNDO_TAB);
                mDialog.dismiss();
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, mDelay);
    }

    private void maxTabReached()
    {
        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        initializeDialog(R.layout.popup_max_tab, Gravity.BOTTOM);
        mDialog.getWindow().setDimAmount(0);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> {
            mDialog.dismiss();
            handler.removeCallbacks(runnable);
        });

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        handler.postDelayed(runnable, 1500);

    }

    private void notSupportMessage()
    {
        initializeDialog(R.layout.popup_not_supported, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(view -> mDialog.dismiss());
        mDialog.setOnDismissListener(dialog -> onClearReference());

        final Handler handler = new Handler();
        Runnable runnable = () -> {
            if(mDialog!=null){
                mDialog.dismiss();
                onClearReference();
            }
        };
        handler.postDelayed(runnable, 2500);
    }

    private void onPanic(){
        initializeDialog(R.layout.popup_panic, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.setOnDismissListener(dialog -> onClearReference());
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            mDialog.dismiss();
            mEvent.invokeObserver(null, M_PANIC_RESET);
        });
    }

    private void orbotLoading()
    {
        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        initializeDialog(R.layout.popup_orbot_connecting, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            mDialog.dismiss();
            handler.removeCallbacks(runnable);
            mEvent.invokeObserver(null, M_OPEN_LOGS);
        });

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        handler.postDelayed(runnable, 5000);

    }

    private void dataClearedSuccessfully()
    {
        final Handler handler = new Handler();
        Runnable runnable = () -> mDialog.dismiss();

        initializeDialog(R.layout.popup_data_cleared, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());

        mDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            onClearReference();
        });

        handler.postDelayed(runnable, 1500);
    }

    private void applicationCrashed()
    {
        initializeDialog(R.layout.application_crash, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void openSecureConnectionPopup()
    {
        initializeDialog(R.layout.secure_connection_popup, Gravity.TOP);
        Window window = mDialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 0,0,0,0);
        mDialog.getWindow().setBackgroundDrawable(inset);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> mEvent.invokeObserver(null, M_SECURE_CONNECTION));

        if((boolean) mData.get(1)){
            ((SwitchMaterial) mDialog.findViewById(R.id.pJSStatus)).setChecked(true);
        }else {
            ((SwitchMaterial) mDialog.findViewById(R.id.pJSStatus)).setChecked(false);
        }
        if((boolean) mData.get(2)){
            ((SwitchMaterial) mDialog.findViewById(R.id.pDTStatus)).setChecked(true);
        }else {
            ((SwitchMaterial) mDialog.findViewById(R.id.pDTStatus)).setChecked(false);
        }
        if((int) mData.get(3) != ContentBlocking.AntiTracking.NONE){
            ((SwitchMaterial) mDialog.findViewById(R.id.pTPStatus)).setChecked(true);
        }else {
            ((SwitchMaterial) mDialog.findViewById(R.id.pTPStatus)).setChecked(false);
        }

        ((TextView) mDialog.findViewById(R.id.pHeaderSubpart)).setText(helperMethod.getDomainName(mData.get(0).toString().replace("genesishiddentechnologies.com", "genesis.onion")));
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void bookmark()
    {
        initializeDialog(R.layout.popup_create_bookmark, Gravity.CENTER);
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        mDialog.setOnShowListener(dialog -> mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING));
        mDialog.setOnDismissListener(dialog -> {
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                helperMethod.hideKeyboard(mContext);
                dialog.dismiss();
                onClearReference();
            };
            handler.postDelayed(runnable, 50);
        });

        String pURL = mData.get(0).toString().replace("genesis.onion","genesishiddentechnologies.com");
        ((TextView)mDialog.findViewById(R.id.pURL)).setText(pURL);

        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> {
            mDialog.dismiss();
            helperMethod.hideKeyboard(mContext);
        });


        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            mDialog.dismiss();
            helperMethod.hideKeyboard(mContext);
            String mBookmarkName = ((EditText)mDialog.findViewById(R.id.pBookmarkNameInput)).getText().toString();
            mEvent.invokeObserver(Arrays.asList(pURL, mBookmarkName), M_BOOKMARK);
        });

    }

    private void onUpdateBridges()
    {
        String mCustomBridge = (String) mEvent.invokeObserver(null, M_CUSTOM_BRIDGE);
        String mBridgeType = (String) mEvent.invokeObserver(null, M_BRIDGE_TYPE);

        initializeDialog(R.layout.popup_update_bridges, Gravity.CENTER);
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        if(!mCustomBridge.equals("meek") && !mCustomBridge.equals("obfs4")){
            ((EditText)mDialog.findViewById(R.id.pBridgeInput)).setText(mCustomBridge);
            ((EditText)mDialog.findViewById(R.id.pBridgeType)).setText(mBridgeType);
        }

        mDialog.setOnShowListener(dialog -> mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING));
        mDialog.setOnDismissListener(dialog -> {
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                helperMethod.hideKeyboard(mContext);
                dialog.dismiss();
                onClearReference();
            };
            handler.postDelayed(runnable, 50);
        });
        mDialog.findViewById(R.id.pBridgeRequest).setOnClickListener(v -> {
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                try{
                    helperMethod.sendBridgeEmail(mContext);
                }
                catch (Exception ex){
                    onTrigger(Arrays.asList(mContext, mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)),M_NOT_SUPPORTED);
                }
            };
            handler.postDelayed(runnable, 200);
        });

        mDialog.findViewById(R.id.mClear).setOnClickListener(v -> {
            EditText mBridges = mDialog.findViewById(R.id.pBridgeInput);
            TextView mTextView = mDialog.findViewById(R.id.pDescriptionError);

            mBridges.setText(strings.GENERIC_EMPTY_STR);
            mTextView.animate().setDuration(250).alpha(0);
        });

        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            String mBridges = ((EditText)mDialog.findViewById(R.id.pBridgeInput)).getText().toString();

            boolean mBridgeTypeExist = !mBridges.contains("obfs3") && !mBridges.contains("obfs4") && !mBridges.contains("fle") && !mBridges.contains("meek");
            boolean mBridgeSize = mBridges.length()<10 || !mBridges.contains(".") || !mBridges.contains(" ");

            if(mBridgeTypeExist || mBridgeSize){
                TextView mTextView = mDialog.findViewById(R.id.pDescriptionError);
                if(mTextView.getAlpha()==0 || mTextView.getAlpha()==1 || mTextView.getVisibility()!=View.VISIBLE){
                    mTextView.setAlpha(0);

                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.animate().setDuration(250).alpha(1);
                    if(mBridgeTypeExist){
                        mTextView.setText("➔  Invalid bridge string");
                    }
                    else if(mBridgeSize){
                        mTextView.setText("➔  Invalid bridge type");
                    }
                }
                return;
            }

            mDialog.dismiss();
            mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            helperMethod.hideKeyboard(mContext);
            mEvent.invokeObserver(Arrays.asList(mBridges, ((EditText)mDialog.findViewById(R.id.pBridgeType)).getText().toString()), M_SET_BRIDGES);
        });
    }

    private void clearHistory()
    {
        initializeDialog(R.layout.popup_clear_history, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            mDialog.dismiss();
            mEvent.invokeObserver(null, M_CLEAR_HISTORY);
        });
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void clearBookmark()
    {
        initializeDialog(R.layout.popup_clear_bookmark, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            mEvent.invokeObserver(null, M_CLEAR_BOOKMARK);
            mDialog.dismiss();
        });
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void downloadSingle()
    {
        if(mData.size()>0){
            initializeDialog(R.layout.popup_download_url, Gravity.BOTTOM);
            String murl = mData.get(2).toString();

            if(!murl.startsWith("http")){
                murl = "https://" + murl;
            }

            ((TextView) mDialog.findViewById(R.id.pDescription)).setText(mData.get(0).toString());
            ((TextView) mDialog.findViewById(R.id.pDescriptionLong)).setText(murl);
            mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
            mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
                mDialog.dismiss();
                final Handler handler = new Handler();
                Runnable runnable = () -> {
                    mEvent.invokeObserver(mData, M_DOWNLOAD_SINGLE);
                };
                handler.postDelayed(runnable, 1000);
                onClearReference();
            });
            mDialog.setOnDismissListener(dialog -> onClearReference());
        }
    }

    private void rateApp()
    {
        initializeDialog(R.layout.popup_rate_us, Gravity.CENTER);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            RatingBar mRatingBar = mDialog.findViewById(R.id.pRating);
            if(mRatingBar.getRating()>=3){
                mEvent.invokeObserver(null, M_APP_RATED);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONST_PLAYSTORE_URL));
                try {
                    mContext.startActivity(intent);
                } catch (Exception ignored) {
                    helperMethod.showToastMessage(MESSAGE_PLAYSTORE_NOT_FOUND, mContext);
                }

                mDialog.dismiss();
            }else if(mRatingBar.getRating()>0) {
                mEvent.invokeObserver(null, M_APP_RATED);
                final Handler handler = new Handler();
                handler.postDelayed(() -> onTrigger(Arrays.asList(strings.GENERIC_EMPTY_STR, mContext),M_RATE_FAILURE), 1000);
                mDialog.dismiss();
            }
        });
    }

    private void
    downloadFileLongPress()
    {
        if(mData==null || mData.size()<1){
            return;
        }

        String title = mData.get(2).toString();

        if(title.length()>0){
            title = title + " | ";
        }

        initializeDialog(R.layout.popup_file_longpress, Gravity.CENTER);
        ((TextView) mDialog.findViewById(R.id.pDescription)).setText((title + mData.get(0).toString()));
        mEvent.invokeObserver(Arrays.asList(((ImageView) mDialog.findViewById(R.id.pFaviconLogo)), helperMethod.getDomainName(mData.get(0).toString())), enums.etype.fetch_favicon);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_DOWNLOAD_FILE_MANUAL);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_NEW_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_CURRENT_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption4).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_COPY_LINK);
                mDialog.dismiss();
            }
        });
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void openURLLongPress()
    {
        String title = mData.get(2).toString();

        initializeDialog(R.layout.popup_url_longpress, Gravity.CENTER);
        ((TextView) mDialog.findViewById(R.id.pDescription)).setText((title + mData.get(0)));
        mEvent.invokeObserver(Arrays.asList(((ImageView) mDialog.findViewById(R.id.pFaviconLogo)), helperMethod.getDomainName(mData.get(0).toString())), enums.etype.fetch_favicon);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_NEW_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_CURRENT_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_COPY_LINK);
                mDialog.dismiss();
            }
        });
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void popupDownloadFull(){
        String url = mData.get(0).toString();
        String file = mData.get(2).toString();
        String title = mData.get(3).toString();
        String altText = strings.GENERIC_EMPTY_STR;
        String mDescription = strings.GENERIC_EMPTY_STR;
        String mDescriptionShort = strings.GENERIC_EMPTY_STR;

        String data_local = mContext.getString(R.string.ALERT_LONG_URL_MESSAGE);

        if(!url.equals(strings.GENERIC_EMPTY_STR)){
            data_local = title + url;
        }
        else if(!file.equals(strings.GENERIC_EMPTY_STR)){
            data_local = file;
        }
        String mTitle = title;
        if(mTitle.length()<=1){
            mTitle = mData.get(0).toString();
        }
        if(mData.get(5) != null){
            mDescription = mData.get(5).toString();
            mDescriptionShort = data_local;
        }else {
            mDescription = data_local;
        }


        initializeDialog(R.layout.popup_download_full, Gravity.CENTER);
        mEvent.invokeObserver(Arrays.asList(((ImageView) mDialog.findViewById(R.id.pFaviconLogo)), helperMethod.getDomainName(data_local)), enums.etype.fetch_favicon);

        ((TextView) mDialog.findViewById(R.id.pDescription)).setText((mDescription));
        ((TextView) mDialog.findViewById(R.id.pDescriptionShort)).setText((mDescriptionShort));
        mDialog.findViewById(R.id.pOption1).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(url), M_OPEN_LINK_NEW_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption2).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(url), M_OPEN_LINK_CURRENT_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption3).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(url), M_COPY_LINK);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption4).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(file), M_OPEN_LINK_NEW_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption5).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(file), M_OPEN_LINK_CURRENT_TAB);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption6).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(file), M_COPY_LINK);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.pOption7).setOnClickListener(v -> {
            if(mData!=null){
                mEvent.invokeObserver(Collections.singletonList(file), M_DOWNLOAD_FILE_MANUAL);
                mDialog.dismiss();
            }
        });
        mDialog.setOnDismissListener(dialog -> onClearReference());
    }

    private void sendBridgeMail()
    {
        initializeDialog(R.layout.popup_bridge_mail, Gravity.BOTTOM);
        mDialog.findViewById(R.id.pTrigger).setOnClickListener(v -> mDialog.dismiss());
        mDialog.findViewById(R.id.pNext).setOnClickListener(v -> {
            mDialog.dismiss();
            final Handler handler = new Handler();
            Runnable runnable = () -> {
                try{
                    helperMethod.sendBridgeEmail(mContext);
                }
                catch (Exception ex){
                    onTrigger(Arrays.asList(mContext, mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)),M_NOT_SUPPORTED);
                }
            };
            handler.postDelayed(runnable, 1000);
        });
    }

    void onReset(){
        if(mContext!=null && !mContext.isDestroyed() && !mContext.isFinishing() && mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    /*External Triggers*/

    public void onTrigger(List<Object> pData, pluginEnums.eMessageManager pEventType)
    {
        if(!pEventType.equals(M_RATE_FAILURE) && !pEventType.equals(M_RATE_SUCCESS) && !pEventType.equals(M_NOT_SUPPORTED)){
            onClearReference();
            mData = null;
        }
        if(pEventType.equals(pluginEnums.eMessageManager.M_RESET)){
            onReset();
        }
        else {
            this.mContext = (AppCompatActivity) pData.get(pData.size()-1);
            this.mData = pData;

            switch (pEventType) {

                case M_RATE_FAILURE:
                    /*VERIFIED*/
                    rateFailure();
                    break;

                case M_LANGUAGE_SUPPORT_FAILURE:
                    /*VERIFIED*/
                    languageSupportFailure();
                    break;

                case M_BOOKMARK:
                    /*VERIFIED*/
                    bookmark();
                    break;

                case M_CLEAR_HISTORY:
                    /*VERIFIED*/
                    clearHistory();
                    break;

                case M_CLEAR_BOOKMARK:
                    /*VERIFIED*/
                    clearBookmark();
                    break;

                case M_RATE_APP:
                    /*VERIFIED*/
                    rateApp();
                    break;

                case M_LONG_PRESS_DOWNLOAD :
                    /*VERIFIED*/
                    downloadFileLongPress();
                    break;

                case M_LONG_PRESS_URL:
                    /*VERIFIED*/
                    openURLLongPress();
                    break;

                case M_LONG_PRESS_WITH_LINK:
                    /*VERIFIED*/
                    popupDownloadFull();
                    break;

                case M_BRIDGE_MAIL:
                    /*VERIFIED*/
                    sendBridgeMail();
                    break;

                case M_NOT_SUPPORTED:
                    /*VERIFIED*/
                    notSupportMessage();
                    break;

                case M_PANIC_RESET:
                    /*VERIFIED*/
                    onPanic();
                    break;

                case M_DATA_CLEARED:
                    /*VERIFIED*/
                    dataClearedSuccessfully();
                    break;

                case M_APPLICATION_CRASH:
                    /*VERIFIED*/
                    applicationCrashed();
                    break;

                case M_SECURE_CONNECTION:
                    /*VERIFIED*/
                    openSecureConnectionPopup();
                    break;

                case M_DOWNLOAD_SINGLE:
                    /*VERIFIED*/
                    downloadSingle();
                    break;

                case M_UPDATE_BRIDGES:
                    /*VERIFIED*/
                    onUpdateBridges();
                    break;

                case M_NEW_IDENTITY:
                    /*VERIFIED*/
                    newIdentityCreated();
                    break;

                case M_DOWNLOAD_FAILURE:
                    /*VERIFIED*/
                    mDownloadFailure();
                    break;

                case M_POPUP_BLOCKED:
                    /*VERIFIED*/
                    popupBlocked();
                    break;

                case M_MAX_TAB_REACHED:
                    /*VERIFIED*/
                    maxTabReached();
                    break;

                case M_ORBOT_LOADING:
                    /*VERIFIED*/
                    orbotLoading();
                    break;

                case M_LOAD_NEW_TAB:
                    /*VERIFIED*/
                    popupLoadNewTab();
                    break;

                case M_UNDO:
                    /*VERIFIED*/
                    popupUndo();
                    break;

                case M_DELETE_BOOKMARK:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, 2000, mContext.getString(R.string.HOME_MENU__BOOKMARK_REMOVED), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_UPDATE_BOOKMARK:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, 2000, mContext.getString(R.string.HOME_MENU__BOOKMARK_UPDATE), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;
            }
        }
    }
}
