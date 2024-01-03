package com.hiddenservices.onionservices.appManager.languageManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;

import java.util.List;

class languageViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;
    private ImageView mBlocker;

    /*Initializations*/

    protected void initialization(eventObserver.eventListener ignoredPEvent, AppCompatActivity context, ImageView pBlocker) {
        this.mContext = context;
        this.mBlocker = pBlocker;
    }

    protected void onInit(){
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

    private void initBlocker(boolean pStatus) {
        if (pStatus) {
            mBlocker.setVisibility(View.VISIBLE);
        } else {
            mBlocker.setVisibility(View.GONE);
        }
    }

    public Object onTrigger(languageEnums.eLanguagevViewController pCommands, List<Object> pData) {
        if (languageEnums.eLanguagevViewController.M_UPDATE_BLOCKER.equals(pCommands)) {
            initBlocker((boolean) pData.get(0));
        }
        return null;
    }

}
