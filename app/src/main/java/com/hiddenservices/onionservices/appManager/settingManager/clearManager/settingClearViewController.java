package com.hiddenservices.onionservices.appManager.settingManager.clearManager;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;

import java.util.ArrayList;
import java.util.List;

class settingClearViewController {
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;
    private ArrayList<CheckBox> mCheckBoxList;

    /*Initializations*/

    settingClearViewController(settingClearController pContext, eventObserver.eventListener pEvent, ArrayList<CheckBox> pCheckBoxList) {
        this.mEvent = pEvent;
        this.mContext = pContext;
        this.mCheckBoxList = pCheckBoxList;

        initPostUI();
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

    private void onClearCheckbox(int pIndex, boolean pStatus) {
        mCheckBoxList.get(pIndex).setChecked(pStatus);
        if (pStatus) {
            mCheckBoxList.get(pIndex).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_checkbox_tint)));
        } else {
            mCheckBoxList.get(pIndex).setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_checkbox_tint_default)));
        }
    }

    public Object onTrigger(settingClearEnums.eClearViewController pCommands, List<Object> pData) {
        if (pCommands.equals(settingClearEnums.eClearViewController.M_CHECK_INVOKE)) {
            onClearCheckbox(Integer.parseInt(pData.get(0).toString()), (boolean) pData.get(1));
        }
        return null;
    }
}
