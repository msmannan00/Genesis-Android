package com.darkweb.genesissearchengine.appManager.launcherManager;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;

class launcherViewController
{
    /*Private Variables*/
    private AppCompatActivity mContext;


    /*Initializations*/
    launcherViewController(AppCompatActivity mContext, eventObserver.eventListener event){
        this.mContext = mContext;
        initUI();
    }

    private void initUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mContext.getResources().getColor(R.color.ease_blue));
        }
    }

}
