package com.hiddenservices.onionservices.appManager.orbotManager;

import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.helperManager.sharedUIMethod;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

class orbotViewController {
    /*Private Variables*/

    private AppCompatActivity mContext;
    private SwitchMaterial mOrbotSettingBridgeSwitch;
    private LinearLayout mOrbotSettingWarning;

    /*Initializations*/

    orbotViewController(SwitchMaterial pOrbotSettingBridgeSwitch, AppCompatActivity pContext, LinearLayout pOrbotSettingWarning) {
        this.mContext = pContext;
        this.mOrbotSettingBridgeSwitch = pOrbotSettingBridgeSwitch;
        this.mOrbotSettingWarning = pOrbotSettingWarning;
    }

    protected void onInit(){
        initPostUI();
    }

    private void initPostUI() {
        sharedUIMethod.updateStatusBar(mContext);
    }

    private void bridgeSettingsStatus(boolean pStatus) {
        updateBridgeViews(pStatus, true);
    }

    private void updateBridgeViews(boolean pStatus, boolean pIsInvoked) {
        mOrbotSettingBridgeSwitch.setChecked(pStatus);
        if (pStatus) {
            mOrbotSettingWarning.setClickable(true);
            mOrbotSettingWarning.setAlpha(0);
            mOrbotSettingWarning.setVisibility(View.VISIBLE);
            if (pIsInvoked) {
                mOrbotSettingWarning.animate().alpha(1);
            } else {
                mOrbotSettingWarning.setAlpha(1);
            }
        } else {
            mOrbotSettingWarning.setClickable(false);
            if (pIsInvoked) {
                mOrbotSettingWarning.animate().alpha(0).withEndAction(() -> mOrbotSettingWarning.setVisibility(View.GONE));
            } else {
                mOrbotSettingWarning.setAlpha(0);
                mOrbotSettingWarning.setVisibility(View.GONE);
            }
        }
    }

    private void initViews(boolean pGatewayStatus, boolean pSnowFlakeStatus) {
        updateBridgeViews(pGatewayStatus, false);
    }

    public void onTrigger(orbotEnums.eOrbotViewCommands pCommands, List<Object> pData) {
        if (pCommands == orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGE_SETTINGS_VIEWS) {
            bridgeSettingsStatus((boolean) pData.get(0));
        }
        else if (pCommands == orbotEnums.eOrbotViewCommands.M_INIT_POST_UI) {
            initPostUI();
        } else if (pCommands == orbotEnums.eOrbotViewCommands.M_INIT_UI) {
            initViews((boolean) pData.get(0), (boolean) pData.get(1));
        } else if (pCommands == orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGES) {
            updateBridgeViews((boolean) pData.get(1), (boolean) pData.get(1));
        }
    }
}
