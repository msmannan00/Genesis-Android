package com.darkweb.genesissearchengine.appManager.languageManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;

class languageViewController
{
    /*ViewControllers*/
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    void initialization(eventObserver.eventListener event, AppCompatActivity context){
        this.mContext = context;
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
                mContext.getWindow().setStatusBarColor(mContext.getColor(R.color.white));
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            }
        }
    }
}
