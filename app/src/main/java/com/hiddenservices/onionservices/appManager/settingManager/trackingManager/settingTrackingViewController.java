package com.hiddenservices.onionservices.appManager.settingManager.trackingManager;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;
import org.mozilla.geckoview.ContentBlocking;
import java.util.ArrayList;
import java.util.List;

class settingTrackingViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;
    private ArrayList<RadioButton> mTracking;

    /*Initializations*/

    settingTrackingViewController(AppCompatActivity pContext, eventObserver.eventListener ignoredPEvent, ArrayList<RadioButton> pTracking) {
        this.mTracking = pTracking;
        this.mContext = pContext;
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
        clearTrackersSetting();
        if (status.sSettingTrackingProtection == ContentBlocking.AntiTracking.NONE) {
            mTracking.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTracking.get(0).setChecked(true);
        }
        if (status.sSettingTrackingProtection == ContentBlocking.AntiTracking.DEFAULT) {
            mTracking.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTracking.get(1).setChecked(true);
        }
        if (status.sSettingTrackingProtection == ContentBlocking.AntiTracking.STRICT) {
            mTracking.get(2).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTracking.get(2).setChecked(true);
        }
    }

    private void clearTrackersSetting() {
        for (int mCounter = 0; mCounter < mTracking.size(); mCounter++) {
            mTracking.get(mCounter).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
            mTracking.get(mCounter).setChecked(false);
        }
    }

    private void setCookieStatus(View pView) {
        clearTrackersSetting();

        if (pView.getId() == R.id.pTrackingOption1) {
            mTracking.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTracking.get(0).setChecked(true);
        } else if (pView.getId() == R.id.pTrackingOption2) {
            mTracking.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTracking.get(1).setChecked(true);
        } else if (pView.getId() == R.id.pTrackingOption3) {
            mTracking.get(2).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTracking.get(2).setChecked(true);
        }
    }

    public Object onTrigger(settingTrackingEnums.eTrackingViewController pCommands, List<Object> pData) {
        if (pCommands.equals(settingTrackingEnums.eTrackingViewController.M_SET_TRACKING_STATUS)) {
            setCookieStatus((View) pData.get(0));
        }
        return null;
    }

}
