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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
            }
            else {
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.landing_ease_blue));
            }
        }
    }
}
