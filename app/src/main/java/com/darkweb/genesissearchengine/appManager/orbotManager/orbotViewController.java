package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
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
                mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }
    }

    private void bridgeSettingsStatus(boolean p_status){
        updateBridgeViews(p_status, true);
    }

    public void updateVPN(boolean p_status){
        mVpnSwitch.setChecked(p_status);
    }

    public void updateBridgeViews(boolean p_status,boolean p_is_invoked){
        mBridgeSwitch.setChecked(p_status);
        if(p_status){
            mCustomizableBridgeMenu.setClickable(true);
            mCustomizableBridgeMenu.setAlpha(0);
            mCustomizableBridgeMenu.setVisibility(View.VISIBLE);
            if(p_is_invoked){
                mCustomizableBridgeMenu.animate().alpha(1);
            }else {
                mCustomizableBridgeMenu.setAlpha(1);
            }
        }else {
            mCustomizableBridgeMenu.setClickable(false);
            if(p_is_invoked){
                mCustomizableBridgeMenu.animate().alpha(0).withEndAction(() -> mCustomizableBridgeMenu.setVisibility(View.GONE));
            }else {
                mCustomizableBridgeMenu.setAlpha(0);
                mCustomizableBridgeMenu.setVisibility(View.GONE);
            }
        }
    }

    private void initViews(boolean p_vpn_status, boolean p_gateway_status){
        updateBridgeViews(p_gateway_status, false);
        updateVPN(p_vpn_status);
    }

    public void onTrigger(orbotEnums.eOrbotViewCommands p_commands, List<Object> p_data){
        if(p_commands == orbotEnums.eOrbotViewCommands.S_UPDATE_BRIDGE_SETTINGS_VIEWS){
            bridgeSettingsStatus((boolean) p_data.get(0));
        }
        else if(p_commands == orbotEnums.eOrbotViewCommands.S_INIT_POST_UI){
            initPostUI();
        }
        else if(p_commands == orbotEnums.eOrbotViewCommands.S_INIT_UI){
            initViews((boolean)p_data.get(0),(boolean) p_data.get(1));
        }
    }
}
