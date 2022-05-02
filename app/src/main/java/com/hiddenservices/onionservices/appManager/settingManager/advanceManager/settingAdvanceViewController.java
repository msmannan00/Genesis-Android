package com.hiddenservices.onionservices.appManager.settingManager.advanceManager;

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
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

class settingAdvanceViewController {
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    private SwitchMaterial mRestoreTabs;
    private SwitchMaterial mShowWebFonts;
    private SwitchMaterial mToolbarTheme;
    private ArrayList<RadioButton> mImageOption;
    private ArrayList<RadioButton> mTabLayoutOption;

    /*Initializations*/

    settingAdvanceViewController(settingAdvanceController pContext, eventObserver.eventListener pEvent, SwitchMaterial pRestoreTabs, SwitchMaterial pShowWebFonts, SwitchMaterial pToolbarTheme, ArrayList<RadioButton> pImageOption, ArrayList<RadioButton> pTabLayoutOption) {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mRestoreTabs = pRestoreTabs;
        this.mShowWebFonts = pShowWebFonts;
        this.mToolbarTheme = pToolbarTheme;
        this.mImageOption = pImageOption;
        this.mTabLayoutOption = pTabLayoutOption;

        initViews();
        initPostUI();
    }

    private void initViews() {
        if (status.sRestoreTabs) {
            mRestoreTabs.setChecked(true);
        } else {
            mRestoreTabs.setChecked(false);
        }

        if (status.sShowWebFonts) {
            mShowWebFonts.setChecked(true);
        } else {
            mShowWebFonts.setChecked(false);
        }

        if (status.sToolbarTheme) {
            mToolbarTheme.setChecked(true);
        } else {
            mToolbarTheme.setChecked(false);
        }

        clearImageOptions();
        if (status.sShowImages == 0) {
            mImageOption.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mImageOption.get(0).setChecked(true);
        } else if (status.sShowImages == 2) {
            mImageOption.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mImageOption.get(1).setChecked(true);
        }

        if (status.sTabGridLayoutEnabled) {
            mTabLayoutOption.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTabLayoutOption.get(0).setChecked(true);
        } else {
            mTabLayoutOption.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTabLayoutOption.get(1).setChecked(true);
        }

    }

    private void initPostUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    }

    private void clearImageOptions() {
        mImageOption.get(0).setChecked(false);
        mImageOption.get(1).setChecked(false);
        mImageOption.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
        mImageOption.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
    }

    private void clearGridOptions() {
        mTabLayoutOption.get(0).setChecked(false);
        mTabLayoutOption.get(1).setChecked(false);
        mTabLayoutOption.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
        mTabLayoutOption.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint_default)));
    }

    private void setImageOptions(View pView) {
        clearImageOptions();
        if (pView.getId() == R.id.pAdvanceOption1) {
            mImageOption.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mImageOption.get(0).setChecked(true);
        } else if (pView.getId() == R.id.pAdvanceOption2) {
            mImageOption.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mImageOption.get(1).setChecked(true);
        }
    }

    private void setGridOptions(View pView) {
        clearGridOptions();
        if (pView.getId() == R.id.pGridOption1) {
            mTabLayoutOption.get(0).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTabLayoutOption.get(0).setChecked(true);
        } else if (pView.getId() == R.id.pGridOption2) {
            mTabLayoutOption.get(1).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mTabLayoutOption.get(1).setChecked(true);
        }
    }

    public Object onTrigger(settingAdvanceEnums.eAdvanceViewController pCommands, List<Object> pData) {
        if (pCommands.equals(settingAdvanceEnums.eAdvanceViewController.M_CLEAR_IMAGE)) {
            clearImageOptions();
        } else if (pCommands.equals(settingAdvanceEnums.eAdvanceViewController.M_CLEAR_GRID)) {
            clearGridOptions();
        } else if (pCommands.equals(settingAdvanceEnums.eAdvanceViewController.M_SET_IMAGE)) {
            setImageOptions((View) pData.get(0));
        } else if (pCommands.equals(settingAdvanceEnums.eAdvanceViewController.M_SET_GRID)) {
            setGridOptions((View) pData.get(0));
        }
        return null;
    }

}
