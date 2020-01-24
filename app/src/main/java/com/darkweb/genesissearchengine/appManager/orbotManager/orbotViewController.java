package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;

class orbotViewController
{
    /*Private Variables*/

    private AppCompatActivity mContext;

    private Switch mBridgeSwitch;

    /*Initializations*/

    orbotViewController(Switch mBridgeSwitch, AppCompatActivity mContext)
    {
        this.mContext = mContext;
        this.mBridgeSwitch = mBridgeSwitch;

        initPostUI();
        initViews();
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


    private void initViews(){
        mBridgeSwitch.setChecked(status.sGateway);
    }


}
