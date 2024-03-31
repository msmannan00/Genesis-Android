package com.hiddenservices.onionservices.appManager.settingManager.privacyManager;

import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_STRICT_POLICY_JAVASCRIPT;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hiddenservices.onionservices.pluginManager.pluginController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_ALL;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_NONE;
import static org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS;

import org.mozilla.geckoview.ContentBlocking;

class settingPrivacyViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;
    private SwitchMaterial mJavaScript;
    private SwitchMaterial mDoNotTrack;
    private LinearLayout mOption0;
    private SwitchMaterial mClearDataOnExit;
    private ArrayList<RadioButton> mCookie;
    private SwitchMaterial mPopup;

    /*Initializations*/

    settingPrivacyViewController(settingPrivacyController pContext, eventObserver.eventListener ignoredPEvent, SwitchMaterial pJavaScript, SwitchMaterial pDoNotTrack, SwitchMaterial pClearDataOnExit, ArrayList<RadioButton> pCookie, SwitchMaterial pPopup, LinearLayout pOption0) {
        this.mContext = pContext;
        this.mJavaScript = pJavaScript;
        this.mClearDataOnExit = pClearDataOnExit;
        this.mCookie = pCookie;
        this.mDoNotTrack = pDoNotTrack;
        this.mPopup = pPopup;
        this.mOption0 = pOption0;
    }

    protected void onInit(){
        initViews();
        initPostUI();
    }

    private void initPostUI() {
        Window window = mContext.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
        } else {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
        }
    }

    private void initViews() {
        if(status.sSettingTrackingProtection == ContentBlocking.AntiTracking.STRICT){
            mOption0.setAlpha(0.3f);
        }

        if (status.sSettingJavaStatus) {
            this.mJavaScript.setChecked(true);
        } else {
            this.mJavaScript.setChecked(false);
        }

        if (status.sSettingPopupStatus) {
            this.mPopup.setChecked(true);
        } else {
            this.mPopup.setChecked(false);
        }

        if (status.sClearOnExit) {
            this.mClearDataOnExit.setChecked(true);
        } else {
            this.mClearDataOnExit.setChecked(false);
        }

        if (status.sStatusDoNotTrack) {
            this.mDoNotTrack.setChecked(true);
        } else {
            this.mDoNotTrack.setChecked(false);
        }

        clearCookieSetting();
        if (status.sSettingCookieStatus == ACCEPT_ALL) {
            mCookie.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(0).setChecked(true);
        }
        if (status.sSettingCookieStatus == ACCEPT_NON_TRACKERS) {
            mCookie.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(1).setChecked(true);
        }
        if (status.sSettingCookieStatus == ACCEPT_FIRST_PARTY) {
            mCookie.get(2).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(2).setChecked(true);
        }
        if (status.sSettingCookieStatus == ACCEPT_NONE) {
            mCookie.get(3).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(3).setChecked(true);
        }

    }

    private void clearCookieSetting() {
        for (int mCounter = 0; mCounter < mCookie.size(); mCounter++) {
            mCookie.get(mCounter).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
            mCookie.get(mCounter).setChecked(false);
        }
    }

    private void setCookieStatus(View pView) {
        clearCookieSetting();

        if (pView.getId() == R.id.pCookieOption1) {
            mCookie.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(0).setChecked(true);
        } else if (pView.getId() == R.id.pCookieOption2) {
            mCookie.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(1).setChecked(true);
        } else if (pView.getId() == R.id.pCookieOption3) {
            mCookie.get(2).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(2).setChecked(true);
        } else if (pView.getId() == R.id.pCookieOption4) {
            mCookie.get(3).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mCookie.get(3).setChecked(true);
        }
    }

    public Object onTrigger(settingPrivacyEnums.ePrivacyViewController pCommands, List<Object> pData) {
        if (pCommands.equals(settingPrivacyEnums.ePrivacyViewController.M_SET_COOKIE_STATUS)) {
            setCookieStatus((View) pData.get(0));
        }
        return null;
    }

}
