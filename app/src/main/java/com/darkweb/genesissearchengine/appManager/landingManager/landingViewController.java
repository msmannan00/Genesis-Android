package com.darkweb.genesissearchengine.appManager.landingManager;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;

class landingViewController
{
    /*Private Variables*/

    private AppCompatActivity mContext;

    /*Initializations*/

    landingViewController(AppCompatActivity mContext, eventObserver.eventListener event){
        this.mContext = mContext;


        initPostUI();
    }

    private void initPostUI(){
        mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
    }
}
