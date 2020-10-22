package com.darkweb.genesissearchengine.appManager.settingManager.searchEngineManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

class settingSearchViewController
{
    /*Private Variables*/

    private eventObserver.eventListener mEvent;
    private AppCompatActivity mContext;

    /*Initializations*/

    settingSearchViewController(settingSearchController pContext, eventObserver.eventListener pEvent)
    {
        this.mEvent = pEvent;
        this.mContext = pContext;

        initViews();
        initPostUI();
    }

    private void initViews()
    {
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
