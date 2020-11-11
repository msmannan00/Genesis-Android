package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

class orbotViewController
{
    /*Private Variables*/

    private AppCompatActivity mContext;
    private SwitchMaterial mBridgeSwitch;
    private SwitchMaterial mVpnSwitch;
    private LinearLayout mCustomizableBridgeMenu;

    /*Initializations*/

    orbotViewController(SwitchMaterial pBridgeSwitch, SwitchMaterial pVpnSwitch, AppCompatActivity pContext, LinearLayout pCustomizableBridgeMenu)
    {
        this.mContext = pContext;
        this.mBridgeSwitch = pBridgeSwitch;
        this.mCustomizableBridgeMenu = pCustomizableBridgeMenu;
        this.mVpnSwitch = pVpnSwitch;

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

    private void bridgeSettingsStatus(boolean pStatus){
        updateBridgeViews(pStatus, true);
    }

    private void updateVPN(boolean pStatus){
        mVpnSwitch.setChecked(pStatus);
    }

    private void updateBridgeViews(boolean pStatus,boolean pIsInvoked){
        mBridgeSwitch.setChecked(pStatus);
        if(pStatus){
            mCustomizableBridgeMenu.setClickable(true);
            mCustomizableBridgeMenu.setAlpha(0);
            mCustomizableBridgeMenu.setVisibility(View.VISIBLE);
            if(pIsInvoked){
                mCustomizableBridgeMenu.animate().alpha(1);
            }else {
                mCustomizableBridgeMenu.setAlpha(1);
            }
        }else {
            mCustomizableBridgeMenu.setClickable(false);
            if(pIsInvoked){
                mCustomizableBridgeMenu.animate().alpha(0).withEndAction(() -> mCustomizableBridgeMenu.setVisibility(View.GONE));
            }else {
                mCustomizableBridgeMenu.setAlpha(0);
                mCustomizableBridgeMenu.setVisibility(View.GONE);
            }
        }
    }

    private void initViews(boolean pVPNStatus, boolean pGatewayStatus){
        updateBridgeViews(pGatewayStatus, false);
        updateVPN(pVPNStatus);
    }

    public void onTrigger(orbotEnums.eOrbotViewCommands pCommands, List<Object> pData){
        if(pCommands == orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGE_SETTINGS_VIEWS){
            bridgeSettingsStatus((boolean) pData.get(0));
        }
        else if(pCommands == orbotEnums.eOrbotViewCommands.M_INIT_POST_UI){
            initPostUI();
        }
        else if(pCommands == orbotEnums.eOrbotViewCommands.M_INIT_UI){
            initViews((boolean)pData.get(0),(boolean) pData.get(1));
        }
        else if(pCommands == orbotEnums.eOrbotViewCommands.M_UPDATE_VPN){
            updateVPN((boolean) pData.get(0));
        }
        else if(pCommands == orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGES){
            updateBridgeViews((boolean) pData.get(1), (boolean) pData.get(1));
        }
    }
}
