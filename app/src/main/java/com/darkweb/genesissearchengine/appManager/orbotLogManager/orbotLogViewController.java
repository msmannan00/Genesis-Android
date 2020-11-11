package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

class orbotLogViewController
{
    /*Private Variables*/

    private AppCompatActivity mContext;
    private TextView mLogs;

    /*Initializations*/

    orbotLogViewController(AppCompatActivity pContext, TextView pLogs)
    {
        this.mContext = pContext;
        this.mLogs = pLogs;

        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(mContext.getResources().getColor(R.color.blue_dark));
            }
            else {
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                    mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.c_background));
            }
        }
    }


    private void onUpdateLogs(String pLogs){
        mLogs.append("\n\n~ " + (pLogs));
    }

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands pCommands, List<Object> pData){
        if(pCommands.equals(orbotLogEnums.eOrbotLogViewCommands.M_UPDATE_LOGS)){
            onUpdateLogs((String) pData.get(0));
        }

    }
}