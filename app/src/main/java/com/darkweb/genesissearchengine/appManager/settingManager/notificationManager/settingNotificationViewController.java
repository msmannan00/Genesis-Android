package com.darkweb.genesissearchengine.appManager.settingManager.notificationManager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.appManager.settingManager.settingHomePage.settingController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import static com.darkweb.genesissearchengine.constants.status.sSettingHistoryStatus;
import static com.darkweb.genesissearchengine.constants.status.sSettingJavaStatus;

class settingNotificationViewController
{
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    private SwitchMaterial mNotificationManual;

    /*Initializations*/

    settingNotificationViewController(settingNotificationController pContext, eventObserver.eventListener pEvent, SwitchMaterial pNotificationManual)
    {
        this.mNotificationManual = pNotificationManual;
        this.mEvent = pEvent;
        this.mContext = pContext;

        initViews();
        initPostUI();
    }

    private void initViews()
    {
        if(status.sBridgeNotificationManual == 1){
            mNotificationManual.setChecked(true);
        }else {
            mNotificationManual.setChecked(false);
        }
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }
            else {
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }
    }

}
