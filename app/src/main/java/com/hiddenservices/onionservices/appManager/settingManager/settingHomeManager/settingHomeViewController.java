package com.hiddenservices.onionservices.appManager.settingManager.settingHomeManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;
import java.util.List;

class settingHomeViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;

    private LinearLayout mOption15;
    private LinearLayout mOption16;

    /*Initializations*/

    settingHomeViewController(settingHomeController mContext, eventObserver.eventListener ignoredMEvent, LinearLayout pOption15, LinearLayout pOption16) {
        this.mContext = mContext;

        this.mOption15 = pOption15;
        this.mOption16 = pOption16;
    }

    protected void onInit(){
        initPostUI();
    }

    private void initPostUI() {
        Window window = mContext.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.blue_dark));
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
        } else {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
        }

        if (!status.sTorBrowsing) {
            this.mOption15.setVisibility(View.GONE);
            this.mOption16.setVisibility(View.GONE);
        }

    }

    public Object onTrigger(settingHomeEnums.eHomeViewController pCommands, List<Object> ignoredPData) {
        if (pCommands.equals(settingHomeEnums.eHomeViewController.M_INIT)) {
            initPostUI();
        }
        return null;
    }

}
