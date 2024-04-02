package com.hiddenservices.onionservices.appManager.settingManager.proxyStatusManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.hiddenservices.onionservices.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;

import java.util.List;

class proxyStatusViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;

    /*Initializations*/
    private TextView mOrbotStatus;
    private SwitchMaterial mBridgeStatus;
    private SwitchMaterial mSnowflakeStatus;

    proxyStatusViewController(AppCompatActivity pContext, TextView pOrbotStatus, SwitchMaterial pBridgeStatus, SwitchMaterial pSnowflakeStatus) {
        this.mContext = pContext;
        this.mOrbotStatus = pOrbotStatus;
        this.mBridgeStatus = pBridgeStatus;
        this.mSnowflakeStatus = pSnowflakeStatus;
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
    }

    private void initViews(String pOrbotStatus, boolean pGatewayStatus, boolean pSnowflakeStatus) {
        if (!status.sTorBrowsing) {
            mOrbotStatus.setText(strings.DISABLED);
        } else {
            mOrbotStatus.setText(pOrbotStatus);
        }
        mBridgeStatus.setChecked(pGatewayStatus);
        mSnowflakeStatus.setChecked(pSnowflakeStatus);
    }

    public void onTrigger(proxyStatusEnums.eProxyStatusViewCommands pCommands, List<Object> pData) {
        if (pCommands.equals(proxyStatusEnums.eProxyStatusViewCommands.M_INIT_VIEWS)) {
            initViews((String) pData.get(0), (boolean) pData.get(1), (boolean) pData.get(2));
        }
    }
}
