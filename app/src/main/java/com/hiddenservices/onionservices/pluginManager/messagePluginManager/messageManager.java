package com.hiddenservices.onionservices.pluginManager.messagePluginManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.hiddenservices.onionservices.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import org.mozilla.geckoview.ContentBlocking;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hiddenservices.onionservices.constants.constants.*;
import static com.hiddenservices.onionservices.constants.strings.BRIDGE_CUSTOM_INVALID_TYPE;
import static com.hiddenservices.onionservices.constants.strings.BRIDGE_CUSTOM_INVALID_TYPE_STRING;
import static com.hiddenservices.onionservices.constants.strings.GENERIC_EMPTY_DOT;
import static com.hiddenservices.onionservices.constants.strings.MESSAGE_NOT_SECURE_HTTPS_SERVICE;
import static com.hiddenservices.onionservices.constants.strings.MESSAGE_PLAYSTORE_NOT_FOUND;
import static com.hiddenservices.onionservices.constants.strings.MESSAGE_SECURE_ONION_SERVICE;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.*;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.*;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_CLEAR_BOOKMARK;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_CLEAR_HISTORY;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManagerCallbacks.M_LOAD_NEW_TAB;

public class messageManager implements View.OnClickListener, DialogInterface.OnDismissListener {
    /*Private Variables*/

    private List<Object> mData;
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;
    private Dialog mDialog = null;
    private pluginEnums.eMessageManagerCallbacks mCallbackInstance;
    private Handler mToastHandler = new Handler();

    /*Initializations*/

    private void onClearReference() {
        if (mContext != null && !mContext.isDestroyed() && !mContext.isFinishing() && mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (mContext != null) {
            mEvent.invokeObserver(null, M_ADJUST_INPUT_RESIZE);
            mContext = null;
        }
    }

    private void onDismiss() {
        if (mContext != null && !mContext.isFinishing() && !mContext.isDestroyed()) {
            mDialog.dismiss();
        }
    }

    void onReset() {
        if (mContext != null && !mContext.isDestroyed() && !mContext.isFinishing() && mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    boolean m_app_closed = true;

    void onClose() {
        m_app_closed = false;
    }

    private void initializeDialog(int pLayout, int pGravity) {
        if (!m_app_closed) {
            return;
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
        InsetDrawable inset = new InsetDrawable(back, helperMethod.pxFromDp(10), 0, helperMethod.pxFromDp(10), 0);
        mDialog.getWindow().setBackgroundDrawable(inset);
        mDialog.getWindow().setLayout(helperMethod.pxFromDp(350), -1);
        mDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        if (mDialog != null && mContext != null && !mContext.isDestroyed() && !mContext.isFinishing()) {
            mDialog.show();
        }
    }

    public messageManager(eventObserver.eventListener event) {
        this.mEvent = event;
    }

    /*Helper Methods*/

    private void rateFailure() {
        initializeDialog(R.layout.popup_rate_failure, Gravity.BOTTOM);

        Button mPopupRateFailureDismiss = mDialog.findViewById(R.id.pPopupRateFailureDismiss);
        Button mPopupRateFailureNext = mDialog.findViewById(R.id.pPopupRateFailureNext);

        mPopupRateFailureDismiss.setOnClickListener(this);
        mPopupRateFailureNext.setOnClickListener(this);
    }

    private void onShowToast(int pLayout, int pBackground, int pDelay, String pInfo, String pTriggerText, pluginEnums.eMessageManagerCallbacks pCallback) {
        initializeDialog(pLayout, Gravity.BOTTOM);
        mDialog.getWindow().setDimAmount(0.3f);
        mCallbackInstance = pCallback;

        ConstraintLayout mPopupToastContainer = mDialog.findViewById(R.id.pPopupToastContainer);
        TextView mPopupToastInfo = mDialog.findViewById(R.id.pPopupToastInfo);
        Button mPopupToastNext = mDialog.findViewById(R.id.pPopupToastTrigger);

        mPopupToastInfo.setText(pInfo);
        mPopupToastNext.setText(pTriggerText);
        mPopupToastNext.setOnClickListener(this);
        mDialog.setOnDismissListener(this);
        mPopupToastContainer.setBackground(helperMethod.getDrawableXML(mContext, pBackground));
        if (pBackground == R.xml.ax_background_important) {
            mPopupToastInfo.setTextColor(Color.WHITE);
            mPopupToastNext.setTextColor(Color.WHITE);
        }

        mToastHandler.postDelayed(() ->
        {
            onDismiss();
        }, pDelay);
    }

    private void onPanic() {
        initializeDialog(R.layout.popup_panic, Gravity.BOTTOM);

        Button mPopupRateFailureDismiss = mDialog.findViewById(R.id.pPopupPanicDismiss);
        Button mPopupRateFailureNext = mDialog.findViewById(R.id.pPopupPanicReset);

        mPopupRateFailureDismiss.setOnClickListener(this);
        mPopupRateFailureNext.setOnClickListener(this);
        mDialog.setOnDismissListener(this);
    }

    private void onDefaultBrowser() {
        initializeDialog(R.layout.popup_default_browser, Gravity.CENTER);

        Button mDismiss = mDialog.findViewById(R.id.pPopupDefaultBrowserDismiss);
        Button mNext = mDialog.findViewById(R.id.pPopupDefaultBrowserNext);

        mDismiss.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mDialog.setCancelable(false);
        mDialog.setOnDismissListener(this);
    }

    private void openSecurityInfo() {
        String mInfo = mData.get(0).toString();
        initializeDialog(R.layout.certificate_info, Gravity.TOP);
        InsetDrawable inset = new InsetDrawable(new ColorDrawable(Color.TRANSPARENT), 0, 0, 0, -1);
        mDialog.getWindow().setBackgroundDrawable(inset);
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        TextView mCertificateDesciption = mDialog.findViewById(R.id.pCertificateDesciption);
        ImageView mCertificateRootBackground = mDialog.findViewById(R.id.pCertificateRootBackground);
        ScrollView mCertificateScrollView = mDialog.findViewById(R.id.pCertificateScrollView);
        ImageView mCertificateRootBlocker = mDialog.findViewById(R.id.pCertificateRootBlocker);
        String message = Html.fromHtml((String) mData.get(0))+"";
        TextView mCertificateRootHeader = mDialog.findViewById(R.id.pCertificateRootHeader);
        if(message.equals("Connection Not Secured")){
            mCertificateRootHeader.setTextColor(Color.RED);
        }

        mCertificateDesciption.setText(message);
        mCertificateRootBackground.animate().setStartDelay(100).setDuration(400).alpha(1);

        mDialog.setOnDismissListener(this);
        mCertificateRootBackground.setOnClickListener(this);
        mCertificateDesciption.setOnClickListener(this);

        if (mInfo.equals(MESSAGE_SECURE_ONION_SERVICE) || mInfo.equals(MESSAGE_NOT_SECURE_HTTPS_SERVICE)) {
            ViewGroup.LayoutParams params = mCertificateScrollView.getLayoutParams();
            params.height = helperMethod.pxFromDp(60);
            mCertificateScrollView.requestLayout();
            mCertificateScrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            mCertificateRootBlocker.setVisibility(View.GONE);
        }
    }

    private void rateApp() {
        initializeDialog(R.layout.popup_rate_us, Gravity.CENTER);
        mDialog.setCancelable(true);

        LinearLayout mPopupRate1 = mDialog.findViewById(R.id.pPopupRateus1);
        LinearLayout mPopupRate2 = mDialog.findViewById(R.id.pPopupRateus2);
        LinearLayout mPopupRate3 = mDialog.findViewById(R.id.pPopupRateus3);
        LinearLayout mPopupRate4 = mDialog.findViewById(R.id.pPopupRateus4);
        LinearLayout mPopupRate5 = mDialog.findViewById(R.id.pPopupRateus5);
        ImageButton mPopupRateusClose = mDialog.findViewById(R.id.pPopupRateusClose);


        mPopupRate1.setOnClickListener(this);
        mPopupRate2.setOnClickListener(this);
        mPopupRate3.setOnClickListener(this);
        mPopupRate4.setOnClickListener(this);
        mPopupRate5.setOnClickListener(this);
        mPopupRate5.setOnClickListener(this);
        mPopupRateusClose.setOnClickListener(this);
    }

    private void sendBridgeMail() {
        initializeDialog(R.layout.popup_bridge_mail, Gravity.BOTTOM);

        Button mBridgeMailPopupDismiss = mDialog.findViewById(R.id.pBridgeMailPopupDismiss);
        Button mBridgeMailPopupNext = mDialog.findViewById(R.id.pBridgeMailPopupNext);

        mBridgeMailPopupDismiss.setOnClickListener(this);
        mBridgeMailPopupNext.setOnClickListener(this);
    }

    private void switchTorBrowsing() {
        initializeDialog(R.layout.popup_tor_change, Gravity.BOTTOM);

        Button mBridgeMailPopupDismiss = mDialog.findViewById(R.id.pTorSwtichPopupDismiss);
        Button mBridgeMailPopupNext = mDialog.findViewById(R.id.pTorSwtichPopupNext);

        mBridgeMailPopupDismiss.setOnClickListener(this);
        mBridgeMailPopupNext.setOnClickListener(this);
    }

    private void bookmark() {
        initializeDialog(R.layout.popup_create_bookmark, Gravity.CENTER);
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        TextView mPopupCreateBookmarkURL = mDialog.findViewById(R.id.pPopupCreateBookmarkURL);
        Button mPopupCreateBookmarkDismiss = mDialog.findViewById(R.id.pPopupCreateBookmarkDismiss);
        Button mPopupCreateBookmarkNext = mDialog.findViewById(R.id.pPopupCreateBookmarkNext);

        mDialog.setOnShowListener(dialog -> mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING));
        String mURL = mData.get(0).toString().replace(CONST_GENESIS_ONION, CONST_GENESIS_ONION_V2);

        if (mURL.startsWith(constants.CONST_PRIVACY_POLICY_URL_NON_TOR)) {
            mURL = "https://orion.onion/privacy";
        }
        mPopupCreateBookmarkURL.setText(mURL);

        mDialog.setOnDismissListener(this);
        mPopupCreateBookmarkDismiss.setOnClickListener(this);
        mPopupCreateBookmarkNext.setOnClickListener(this);
    }

    private void disableLongClick(EditText pEdittext){
        pEdittext.setLongClickable(false);
        pEdittext.setOnLongClickListener(v -> false);
    }


    private void onUpdateBridges() {
        initializeDialog(R.layout.popup_bridge_setting_custom, Gravity.CENTER);
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        String mCustomBridge = (String) mEvent.invokeObserver(null, M_CUSTOM_BRIDGE);
        String mBridgeType = (String) mEvent.invokeObserver(null, M_BRIDGE_TYPE);
        EditText mBridgeSettingCustomInput = mDialog.findViewById(R.id.pBridgeSettingCustomInput);
        EditText mBridgeSettingBridgeType = mDialog.findViewById(R.id.pBridgeSettingBridgeType);
        disableLongClick(mBridgeSettingCustomInput);
        disableLongClick(mBridgeSettingBridgeType);
        Button mBridgeSettingCustomRequest = mDialog.findViewById(R.id.pBridgeSettingCustomRequest);
        ImageButton mBridgeSettingCustomClear = mDialog.findViewById(R.id.pBridgeSettingCustomClear);
        Button mBridgeSettingCustomNext = mDialog.findViewById(R.id.pBridgeSettingCustomNext);

        mDialog.setOnDismissListener(this);
        mBridgeSettingCustomRequest.setOnClickListener(this);
        mBridgeSettingCustomClear.setOnClickListener(this);
        mBridgeSettingCustomNext.setOnClickListener(this);
        mDialog.setOnShowListener(dialog -> mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING));

        if (!mCustomBridge.equals("meek") && !mCustomBridge.equals("snowflake") && !mCustomBridge.equals("obfs4")) {
            mBridgeSettingCustomInput.setText(mCustomBridge);
            mBridgeSettingBridgeType.setText(mBridgeType);
        }
    }

    private void downloadSingle() {
        if (mData.size() > 0) {
            initializeDialog(R.layout.popup_download_url, Gravity.BOTTOM);

            Button mDownloadPopuInfoNext = mDialog.findViewById(R.id.pDownloadPopuInfoNext);
            Button mDownloadPopuInfoDismiss = mDialog.findViewById(R.id.pDownloadPopuInfoDismiss);
            TextView mDownloadPopuInfo = mDialog.findViewById(R.id.pDownloadPopuInfo);
            TextView mDownloadPopuInfoLong = mDialog.findViewById(R.id.pDownloadPopuInfoLong);

            mDownloadPopuInfoDismiss.setOnClickListener(this);
            mDownloadPopuInfoNext.setOnClickListener(this);
            mDialog.setOnDismissListener(this);

            mDownloadPopuInfo.setText(mData.get(0).toString());
            mDownloadPopuInfoLong.setText(mData.get(2).toString());
        }
    }

    private void openSecureConnectionPopup() {
        try{
            if((boolean)mData.get(6) && !status.sTorBrowsing){
                initializeDialog(R.layout.non_secure_connection_popup, Gravity.TOP);
            }else {
                initializeDialog(R.layout.secure_connection_popup, Gravity.TOP);
            }
            InsetDrawable inset = new InsetDrawable(new ColorDrawable(Color.TRANSPARENT), 0, 0, 0, -1);
            mDialog.getWindow().setBackgroundDrawable(inset);
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            ImageView pSecurePopupRootBlocker = mDialog.findViewById(R.id.pSecurePopupRootBlocker);
            TextView pSecurePopupSubHeader = mDialog.findViewById(R.id.pSecurePopupSubHeader);
            Button mSecurePopupCertificate = mDialog.findViewById(R.id.pSecurePopupCertificate);
            Button mSecurePopupPrivacy = mDialog.findViewById(R.id.pSecurePopupPrivacy);
            SwitchMaterial mSecureJavascriptStatus = mDialog.findViewById(R.id.pSecurePopupJavascriptStatus);
            SwitchMaterial mSecureTrackingStatus = mDialog.findViewById(R.id.pSecurePopupTrackingStatus);
            SwitchMaterial mSecureTrackingProtectionStatus = mDialog.findViewById(R.id.pSecurePopupTrackingProtectionStatus);

            pSecurePopupRootBlocker.animate().setStartDelay(100).setDuration(400).alpha(1);
            String mURL = helperMethod.getDomainName(mData.get(0).toString().replace("167.86.99.31", "orion.onion").replace(CONST_GENESIS_URL_CACHED, "orion.onion").replace(CONST_GENESIS_URL_CACHED_DARK, "orion.onion"));
            if(mURL.contains("?")){
                mURL = mURL.substring(0, mURL.indexOf("?"));
            }
            if(status.sTorBrowsing){
                //pSecurePopupHeader
                ((TextView)mDialog.findViewById(R.id.pSecurePopupHeader)).setText("Tor://");
                mURL = mURL.replace("http://","onion://").replace("https://","onion://");
            }
            pSecurePopupSubHeader.setText(mURL);

            mDialog.setOnDismissListener(this);
            pSecurePopupRootBlocker.setOnClickListener(this);
            mSecurePopupCertificate.setOnClickListener(this);
            mSecurePopupPrivacy.setOnClickListener(this);


            if ((boolean) mData.get(1)) {
                mSecureJavascriptStatus.setChecked(true);
            } else {
                mSecureJavascriptStatus.setChecked(false);
            }
            if ((boolean) mData.get(2)) {
                mSecureTrackingStatus.setChecked(true);
            } else {
                mSecureTrackingStatus.setChecked(false);
            }
            if ((int) mData.get(3) != ContentBlocking.AntiTracking.NONE) {
                mSecureTrackingProtectionStatus.setChecked(true);
            } else {
                mSecureTrackingProtectionStatus.setChecked(false);
            }
        }catch (Exception ex){}
    }

    private void downloadFileLongPress() {
        if (mData == null || mData.size() < 1) {
            return;
        }

        String title = mData.get(2).toString();
        if (title.length() > 0) {
            title = title + " | ";
        }

        initializeDialog(R.layout.popup_file_longpress, Gravity.CENTER);

        TextView mPopupLongPressDescription = mDialog.findViewById(R.id.pPopupLongPressDescription);
        ImageView mPopupLongPressImage = mDialog.findViewById(R.id.pPopupLongPressImage);
        Button mPopupLongPressDismiss = mDialog.findViewById(R.id.pPopupLongPressDismiss);
        LinearLayout mPopupLongPressOptionDownload = mDialog.findViewById(R.id.pPopupLongPressOptionDownload);
        LinearLayout mPopupLongPressOptionNewTab = mDialog.findViewById(R.id.pPopupLongPressOptionNewTab);
        LinearLayout mPopupLongPressOptionCurrentTab = mDialog.findViewById(R.id.pPopupLongPressOptionCurrentTab);
        LinearLayout mPopupLongPressOptionCopy = mDialog.findViewById(R.id.pPopupLongPressOptionCopy);

        mPopupLongPressDescription.setText((title));
        mEvent.invokeObserver(Arrays.asList((mPopupLongPressImage), helperMethod.getDomainName(mData.get(0).toString())), ON_FETCH_FAVICON);

        mDialog.setOnDismissListener(this);
        mPopupLongPressDismiss.setOnClickListener(this);

        if (mData != null) {
            mPopupLongPressOptionDownload.setOnClickListener(this);
            mPopupLongPressOptionNewTab.setOnClickListener(this);
            mPopupLongPressOptionCurrentTab.setOnClickListener(this);
            mPopupLongPressOptionCopy.setOnClickListener(this);
        }
    }

    private void openURLLongPress() {
        String title = mData.get(2).toString();
        initializeDialog(R.layout.popup_url_longpress, Gravity.CENTER);
        TextView mPopupURLLongPressHeader = mDialog.findViewById(R.id.pPopupURLLongPressHeader);
        ImageView mPopupURLLongPressImage = mDialog.findViewById(R.id.pPopupURLLongPressImage);
        LinearLayout mPopupURLLongPressNewTab = mDialog.findViewById(R.id.pPopupURLLongPressNewTab);
        LinearLayout mPopupURLLongPressCurrentTab = mDialog.findViewById(R.id.pPopupURLLongPressCurrentTab);
        LinearLayout mPopupURLLongPressClipboard = mDialog.findViewById(R.id.pPopupURLLongPressClipboard);
        Button mPopupURLLongPressDismiss = mDialog.findViewById(R.id.pPopupURLLongPressDismiss);
        String mText = title + mData.get(0);

        mPopupURLLongPressHeader.setText(mText);
        mEvent.invokeObserver(Arrays.asList(mPopupURLLongPressImage, helperMethod.getDomainName(mData.get(0).toString())), ON_FETCH_FAVICON);

        mDialog.setOnDismissListener(this);
        if (mData != null) {
            mPopupURLLongPressNewTab.setOnClickListener(this);
            mPopupURLLongPressCurrentTab.setOnClickListener(this);
            mPopupURLLongPressClipboard.setOnClickListener(this);
            mPopupURLLongPressDismiss.setOnClickListener(this);
        }
    }

    private void popupDownloadFull() {
        String url = mData.get(0).toString();
        String file = mData.get(2).toString();
        String title = mData.get(3).toString();
        String mDescription;
        String mDescriptionShort = strings.GENERIC_EMPTY_STR;

        String data_local = strings.GENERIC_EMPTY_STR;

        if (!url.equals(strings.GENERIC_EMPTY_STR)) {
            data_local = title + url;
        } else if (!file.equals(strings.GENERIC_EMPTY_STR)) {
            data_local = file;
        }

        if (mData.get(5) != null) {
            mDescription = mData.get(5).toString();
            mDescriptionShort = data_local;
        } else {
            mDescription = data_local;
        }

        if(mDescription.equals("")){
            mDescription = url;
        }
        if(mDescriptionShort.equals("")){
            mDescription = "";
            mDescriptionShort = mDescription;
        }
        if(mDescriptionShort.length()>50){
            mDescriptionShort = mDescriptionShort.substring(0, 50)+"...";
        }

        initializeDialog(R.layout.popup_download_full, Gravity.CENTER);

        TextView mPopupDownloadFullDescription = mDialog.findViewById(R.id.pPopupDownloadFullDescription);
        TextView mPopupDownloadFullDescriptionShort = mDialog.findViewById(R.id.pPopupDownloadFullDescriptionShort);
        LinearLayout mPopupDownloadFullNewTab = mDialog.findViewById(R.id.pPopupDownloadFullNewTab);
        LinearLayout mPopupDownloadFullCurrentTab = mDialog.findViewById(R.id.pPopupDownloadFullCurrentTab);
        LinearLayout mPopupDownloadFullCopy = mDialog.findViewById(R.id.pPopupDownloadFullCopy);
        ImageView mPopupDownloadFullImage = mDialog.findViewById(R.id.pPopupDownloadFullImage);
        LinearLayout mPopupDownloadFullImageNewTab = mDialog.findViewById(R.id.pPopupDownloadFullImageNewTab);
        LinearLayout mPopupDownloadFullImageCurrentTab = mDialog.findViewById(R.id.pPopupDownloadFullImageCurrentTab);
        LinearLayout mPopupDownloadFullImageCopy = mDialog.findViewById(R.id.pPopupDownloadFullImageCopy);
        LinearLayout mPopupDownloadFullImageDownload = mDialog.findViewById(R.id.pPopupDownloadFullImageDownload);

        if(mDescription.startsWith("http")){
            mPopupDownloadFullDescriptionShort.setText("static url");
        }else {
            mPopupDownloadFullDescriptionShort.setText((mDescriptionShort));
        }
        mPopupDownloadFullDescription.setText((mDescription));
        mEvent.invokeObserver(Arrays.asList((mPopupDownloadFullImage), helperMethod.getDomainName(data_local)), ON_FETCH_FAVICON);

        if (mData != null) {
            mDialog.setOnDismissListener(this);
            mPopupDownloadFullNewTab.setOnClickListener(this);
            mPopupDownloadFullCurrentTab.setOnClickListener(this);
            mPopupDownloadFullCopy.setOnClickListener(this);
            mPopupDownloadFullImageNewTab.setOnClickListener(this);
            mPopupDownloadFullImageCurrentTab.setOnClickListener(this);
            mPopupDownloadFullImageCopy.setOnClickListener(this);
            mPopupDownloadFullImageDownload.setOnClickListener(this);
        }
    }

    public String getStoreLink() {
        if (status.sStoreType == enums.StoreType.GOOGLE_PLAY) {
            return CONST_PLAYSTORE_URL;
        } else if (status.sStoreType == enums.StoreType.AMAZON) {
            return CONST_AMAZON_URL;
        } else if (status.sStoreType == enums.StoreType.HUAWEI) {
            return CONST_HUAWEI_URL;
        } else {
            return CONST_SAMSUNG_URL;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.pPopupToastTrigger ||
                view.getId() == R.id.pBridgeMailPopupDismiss ||
                view.getId() == R.id.pPopupRateFailureDismiss ||
                view.getId() == R.id.pPopupPanicDismiss ||
                view.getId() == R.id.pDownloadPopuInfoDismiss ||
                view.getId() == R.id.pTorSwtichPopupDismiss ||
                view.getId() == R.id.pPopupURLLongPressDismiss ||
                view.getId() == R.id.pPopupLongPressDismiss ||
                view.getId() == R.id.pPopupRateusClose ||
                view.getId() == R.id.pCertificateDesciption ||
                view.getId() == R.id.pPopupDefaultBrowserDismiss ||
                view.getId() == R.id.pCertificateRootBackground
        ) {
            onDismiss();
        } else if (view.getId() == R.id.pDownloadPopuInfoNext) {
            onDismiss();
            helperMethod.onDelayHandler(mContext, 1000, () -> {
                mEvent.invokeObserver(mData, M_DOWNLOAD_SINGLE);
                onClearReference();
                return null;
            });
        } else if (view.getId() == R.id.pPopupPanicReset) {
            onDismiss();
            mEvent.invokeObserver(mData, M_PANIC_RESET);
            onClearReference();
        } else if (view.getId() == R.id.pTorSwtichPopupNext) {
            onDismiss();
            mEvent.invokeObserver(null, M_TOR_SWITCH_RESTART);
        } else if (view.getId() == R.id.pPopupDefaultBrowserNext) {
            onDismiss();
            if (helperMethod.isDefaultBrowserSet(mContext)) {
                helperMethod.openDefaultBrowser(mContext);
            }
        } else if (view.getId() == R.id.pPopupCreateBookmarkDismiss) {
            onDismiss();
            helperMethod.hideKeyboard(mContext);
        } else if (view.getId() == R.id.pSecurePopupPrivacy) {
            mDialog.findViewById(R.id.pSecurePopupRootBlocker).animate().setDuration(150).alpha(0);
            helperMethod.onDelayHandler(mContext, 250, () -> {
                mEvent.invokeObserver(null, M_OPEN_PRIVACY);
                onDismiss();
                return null;
            });
        } else if (view.getId() == R.id.pPopupLongPressOptionDownload) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_DOWNLOAD_FILE_MANUAL);
            onDismiss();
        } else if (view.getId() == R.id.pPopupLongPressOptionNewTab) {
            onDismiss();
            helperMethod.onDelayHandler(mContext, 200, () -> {
                mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_NEW_TAB);
                return null;
            });
        } else if (view.getId() == R.id.pPopupURLLongPressNewTab) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_NEW_TAB);
            onDismiss();
        } else if (view.getId() == R.id.pPopupLongPressOptionCurrentTab) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_CURRENT_TAB);
            onDismiss();
        } else if (view.getId() == R.id.pPopupLongPressOptionCopy) {
            onDismiss();
            mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_COPY_LINK);

            helperMethod.onDelayHandler(mContext, 200, () -> {
                onTrigger(mData, M_COPY);
                return null;
            });
        } else if (view.getId() == R.id.pSecurePopupRootBlocker) {
            ImageView mSecurePopupRootBlocker = mDialog.findViewById(R.id.pSecurePopupRootBlocker);
            mSecurePopupRootBlocker.animate().setDuration(150).alpha(0);
            onDismiss();
        } else if (view.getId() == R.id.pSecurePopupCertificate) {
            mDialog.findViewById(R.id.pSecurePopupRootBlocker).animate().setDuration(150).alpha(0);
            onDismiss();
            new Handler().postDelayed(() ->
            {
                mEvent.invokeObserver(null, M_SECURITY_INFO);
            }, 500);
        } else if (view.getId() == R.id.pPopupCreateBookmarkNext) {
            onDismiss();
            helperMethod.hideKeyboard(mContext);
            EditText mPopupCreateBookmarkInput = mDialog.findViewById(R.id.pPopupCreateBookmarkInput);
            disableLongClick(mPopupCreateBookmarkInput);
            String mBookmarkName = mPopupCreateBookmarkInput.getText().toString();
            String mURL = mData.get(0).toString().replace(CONST_GENESIS_ONION, CONST_GENESIS_ONION_V2);
            if (mURL.startsWith(constants.CONST_PRIVACY_POLICY_URL_NON_TOR)) {
                mURL = "https://orion.onion/privacy";
            }
            mEvent.invokeObserver(Arrays.asList(mURL, mBookmarkName), M_BOOKMARK);
        } else if (view.getId() == R.id.pPopupRateus1 || view.getId() == R.id.pPopupRateus2 || view.getId() == R.id.pPopupRateus3 || view.getId() == R.id.pPopupRateus4 || view.getId() == R.id.pPopupRateus5) {
            if (view.getId() == R.id.pPopupRateus1 || view.getId() == R.id.pPopupRateus2 || view.getId() == R.id.pPopupRateus3) {
                mEvent.invokeObserver(null, M_APP_RATED);
                String mStoreURL = getStoreLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mStoreURL));
                try {
                    mContext.startActivity(intent);
                } catch (Exception ignored) {
                    helperMethod.showToastMessage(MESSAGE_PLAYSTORE_NOT_FOUND, mContext);
                }
                onDismiss();
            } else{
                mEvent.invokeObserver(null, M_APP_RATED);
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (mContext != null) {
                        onTrigger(Arrays.asList(strings.GENERIC_EMPTY_STR, mContext), M_RATE_FAILURE);
                    }
                }, 1000);
                onDismiss();
            }
        } else if (view.getId() == R.id.pBridgeSettingCustomRequest) {
            helperMethod.onDelayHandler(mContext, 200, () -> {
                try {
                    onDismiss();
                    mEvent.invokeObserver(null, M_GET_BRIDGES);
                } catch (Exception ex) {
                    onTrigger(Arrays.asList(mContext, mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)), M_NOT_SUPPORTED);
                }
                return null;
            });
        } else if (view.getId() == R.id.pBridgeSettingCustomClear) {
            EditText mBridges = mDialog.findViewById(R.id.pBridgeSettingCustomInput);
            disableLongClick(mBridges);
            TextView mTextView = mDialog.findViewById(R.id.pBridgeSettingCustomError);

            mBridges.setText(strings.GENERIC_EMPTY_STR);
            mTextView.animate().setDuration(250).alpha(0);
        } else if (view.getId() == R.id.pBridgeSettingCustomNext) {
            disableLongClick(mDialog.findViewById(R.id.pBridgeSettingCustomInput));
            String mBridges = ((EditText) mDialog.findViewById(R.id.pBridgeSettingCustomInput)).getText().toString();

            boolean mBridgeTypeExist = !mBridges.contains("obfs3") && !mBridges.contains("obfs4") && !mBridges.contains("fle") && !mBridges.contains("meek");
            boolean mBridgeSize = mBridges.length() < 10 || !mBridges.contains(GENERIC_EMPTY_DOT) || !mBridges.contains(strings.GENERIC_EMPTY_SPACE);

            if (mBridgeTypeExist || mBridgeSize) {
                TextView mTextView = mDialog.findViewById(R.id.pBridgeSettingCustomError);
                if (mTextView.getAlpha() == 0 || mTextView.getAlpha() == 1 || mTextView.getVisibility() != View.VISIBLE) {
                    mTextView.setAlpha(0);

                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.animate().setDuration(250).alpha(1);
                    if (mBridgeTypeExist) {
                        mTextView.setText(BRIDGE_CUSTOM_INVALID_TYPE_STRING);
                    } else if (mBridgeSize) {
                        mTextView.setText(BRIDGE_CUSTOM_INVALID_TYPE);
                    }
                }
                return;
            }

            onDismiss();
            mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            helperMethod.hideKeyboard(mContext);
            disableLongClick(mDialog.findViewById(R.id.pBridgeSettingBridgeType));
            mEvent.invokeObserver(Arrays.asList(mBridges, ((EditText) mDialog.findViewById(R.id.pBridgeSettingBridgeType)).getText().toString()), M_SET_BRIDGES);
        } else if (view.getId() == R.id.pPopupRateFailureNext) {
            onDismiss();
            helperMethod.onDelayHandler(mContext, 1000, () -> {
                try {
                    onDismiss();
                    helperMethod.sendIssueEmail(mContext);
                } catch (Exception ex) {
                    onTrigger(Arrays.asList(mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE), mContext), M_NOT_SUPPORTED);
                    onClearReference();
                }
                return null;
            });
        } else if (view.getId() == R.id.pBridgeMailPopupNext) {
            onDismiss();
            helperMethod.onDelayHandler(mContext, 200, () -> {
                try {
                    onDismiss();
                    mEvent.invokeObserver(null, M_GET_BRIDGES);
                } catch (Exception ex) {
                    onTrigger(Arrays.asList(mContext, mContext.getString(R.string.ALERT_NOT_SUPPORTED_MESSAGE)), M_NOT_SUPPORTED);
                }
                return null;
            });
        } else if (view.getId() == R.id.pPopupURLLongPressCurrentTab) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_OPEN_LINK_CURRENT_TAB);
            onDismiss();
        } else if (view.getId() == R.id.pPopupURLLongPressClipboard) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_COPY_LINK);
            onDismiss();

            helperMethod.onDelayHandler(mContext, 200, () -> {
                onTrigger(mData, M_COPY);
                return null;
            });
        } else if (view.getId() == R.id.pPopupDownloadFullNewTab) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0).toString()), M_OPEN_LINK_NEW_TAB);
            onDismiss();
        } else if (view.getId() == R.id.pPopupDownloadFullCurrentTab) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0).toString()), M_OPEN_LINK_CURRENT_TAB);
            onDismiss();
        } else if (view.getId() == R.id.pPopupDownloadFullCopy) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(0)), M_COPY_LINK);
            onDismiss();

            helperMethod.onDelayHandler(mContext, 200, () -> {
                onTrigger(mData, M_COPY);
                return null;
            });
        } else if (view.getId() == R.id.pPopupDownloadFullImageNewTab) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(2).toString()), M_OPEN_LINK_NEW_TAB);
            onDismiss();
        } else if (view.getId() == R.id.pPopupDownloadFullImageCurrentTab) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(2).toString()), M_OPEN_LINK_CURRENT_TAB);
            onDismiss();
        } else if (view.getId() == R.id.pPopupDownloadFullImageCopy) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(2)), M_COPY_LINK);
            onDismiss();

            helperMethod.onDelayHandler(mContext, 200, () -> {
                onTrigger(mData, M_COPY);
                return null;
            });
        } else if (view.getId() == R.id.pPopupDownloadFullImageDownload) {
            mEvent.invokeObserver(Collections.singletonList(mData.get(2).toString()), M_DOWNLOAD_FILE_MANUAL);
            onDismiss();
        }
        if (mCallbackInstance != null) {
            mEvent.invokeObserver(mData, mCallbackInstance);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        onClearReference();
    }


    /*External Triggers*/

    public void onTrigger(List<Object> pData, pluginEnums.eMessageManager pEventType) {
        mCallbackInstance = null;
        mToastHandler.removeCallbacksAndMessages(null);
        if (!pEventType.equals(M_RATE_FAILURE) && !pEventType.equals(M_RATE_SUCCESS) && !pEventType.equals(M_NOT_SUPPORTED)) {
            onClearReference();
            mData = null;
        }
        if (pEventType.equals(pluginEnums.eMessageManager.M_RESET)) {
            onReset();
        } else {
            this.mContext = (AppCompatActivity) pData.get(pData.size() - 1);
            this.mData = pData;

            switch (pEventType) {

                case M_CLOSE:
                    /*VERIFIED*/
                    onClose();
                    break;

                case M_LANGUAGE_SUPPORT_FAILURE:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, mContext.getString(R.string.ALERT_LANGUAGE_SUPPORT_FAILURE), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_BOOKMARK:
                    /*VERIFIED*/
                    bookmark();
                    break;

                case M_CLEAR_HISTORY:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 12000, mContext.getString(R.string.ALERT_CLEAR_HISTORY), mContext.getString(R.string.ALERT_CONFIRM), M_CLEAR_HISTORY);
                    break;

                case M_CLEAR_BOOKMARK:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 12000, mContext.getString(R.string.ALERT_CLEAR_BOOKMARK_INFO), mContext.getString(R.string.ALERT_CONFIRM), M_CLEAR_BOOKMARK);
                    break;

                case M_RATE_APP:
                    /*VERIFIED*/
                    rateApp();
                    break;

                case M_LONG_PRESS_DOWNLOAD:
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
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, mContext.getString(R.string.ALERT_NOT_SUPPORTED), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_PANIC_RESET:
                    /*VERIFIED*/
                    onPanic();
                    break;

                case M_DEFAULT_BROWSER:
                    /*VERIFIED*/
                    onDefaultBrowser();
                    break;

                case M_SECURE_CONNECTION:
                    /*VERIFIED*/
                    openSecureConnectionPopup();
                    break;

                case M_SECURITY_INFO:
                    /*VERIFIED*/
                    openSecurityInfo();
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
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 2000, mContext.getString(R.string.TOAST_ALERT_NEW_CIRCUIT_CREATED), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_DOWNLOAD_FAILURE:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, (String) mData.get(0), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_POPUP_BLOCKED:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 2000, mContext.getString(R.string.TOAST_ALERT_SETTING_PRIVACY_POPUP), mContext.getString(R.string.TOAST_ALERT_SETTING), M_OPEN_PRIVACY);
                    break;

                case M_MAX_TAB_REACHED:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 1000, mContext.getString(R.string.TOAST_ALERT_MAX_TAB_POPUP), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_LOW_MEMORY:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 1000, "Action failed | low memory", mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_LOW_MEMORY_AUTO:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 3000, "low memory warning", mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_ORBOT_LOADING:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, mContext.getString(R.string.TOAST_ALERT_ORBOT_LOADING), mContext.getString(R.string.TOAST_ALERT_ORBOT_LOADING_BUTTON), M_OPEN_LOGS);
                    break;

                case M_LOAD_NEW_TAB:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 2000, mContext.getString(R.string.TOAST_ALERT_OPEN_NEW_TAB), mContext.getString(R.string.TOAST_ALERT_OPEN_NEW_TAB_LOAD), M_LOAD_NEW_TAB);
                    break;

                case M_UNDO:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 2500, mContext.getString(R.string.TOAST_ALERT_UNDO_INFO), mContext.getString(R.string.TOAST_ALERT_UNDO_TRIGGER), M_UNDO_TAB);
                    break;

                case M_DATA_CLEARED:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, mContext.getString(R.string.TOAST_ALERT_CLEARED_INFO), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_DELETE_BOOKMARK:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, mContext.getString(R.string.TOAST_ALERT_BOOKMARK_REMOVED), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_COPY:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 1000, mContext.getString(R.string.TOAST_ALERT_URL_COPIED), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_GENESIS_SEARCH_DISABLED:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_warning, R.xml.ax_background_warning, 2000, "Warning! Start Browser with tor browsing enabled", mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_UPDATE_BOOKMARK:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, mContext.getString(R.string.TOAST_ALERT_BOOKMARK_UPDATE), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_SETTING_CHANGED_RESTART_REQUSTED:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 4000, mContext.getString(R.string.TOAST_ALERT_IMAGE_STATUS), mContext.getString(R.string.TOAST_ALERT_RESTART), M_IMAGE_UPDATE_RESTART);
                    break;

                case M_OPEN_CICADA:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_important, 2000, mContext.getString(R.string.TOAST_ALERT_CICADA), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;

                case M_TOR_SWITCH:
                    /*VERIFIED*/
                    switchTorBrowsing();
                    break;

                case M_RATE_FAILURE:
                    /*VERIFIED*/
                    rateFailure();
                    break;

                case M_OPEN_ACTIVITY_FAILED:
                    /*VERIFIED*/
                    onShowToast(R.layout.popup_toast_generic, R.xml.ax_background_generic, 2000, mContext.getString(R.string.TOAST_ALERT_OPEN_ACTIVITY_FAILED), mContext.getString(R.string.ALERT_DISMISS), null);
                    break;
            }
        }
    }

}
