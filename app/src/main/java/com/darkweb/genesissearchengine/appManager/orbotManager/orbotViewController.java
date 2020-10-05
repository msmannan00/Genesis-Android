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

    private AppCompatActivity m_context;
    private SwitchMaterial m_bridge_switch;
    private SwitchMaterial m_vpn_switch;
    private LinearLayout m_customizable_bridge_menu;

    /*Initializations*/

    orbotViewController(SwitchMaterial p_bridge_switch, SwitchMaterial p_vpn_switch, AppCompatActivity p_context, LinearLayout p_customizable_bridge_menu)
    {
        this.m_context = p_context;
        this.m_bridge_switch = p_bridge_switch;
        this.m_customizable_bridge_menu = p_customizable_bridge_menu;
        this.m_vpn_switch = p_vpn_switch;

        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = m_context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(m_context.getResources().getColor(R.color.blue_dark));
            }
            else {
                m_context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                m_context.getWindow().setStatusBarColor(ContextCompat.getColor(m_context, R.color.white));
            }
        }
    }

    private void bridgeSettingsStatus(boolean p_status){
        updateBridgeViews(p_status, true);
    }

    public void updateVPN(boolean p_status){
        m_vpn_switch.setChecked(p_status);
    }

    public void updateBridgeViews(boolean p_status,boolean p_is_invoked){
        m_bridge_switch.setChecked(p_status);
        if(p_status){
            m_customizable_bridge_menu.setClickable(true);
            m_customizable_bridge_menu.setAlpha(0);
            m_customizable_bridge_menu.setVisibility(View.VISIBLE);
            if(p_is_invoked){
                m_customizable_bridge_menu.animate().alpha(1);
            }else {
                m_customizable_bridge_menu.setAlpha(1);
            }
        }else {
            m_customizable_bridge_menu.setClickable(false);
            if(p_is_invoked){
                m_customizable_bridge_menu.animate().alpha(0).withEndAction(() -> m_customizable_bridge_menu.setVisibility(View.GONE));
            }else {
                m_customizable_bridge_menu.setAlpha(0);
                m_customizable_bridge_menu.setVisibility(View.GONE);
            }
        }
    }

    private void initViews(boolean p_vpn_status, boolean p_gateway_status){
        updateBridgeViews(p_gateway_status, false);
        updateVPN(p_vpn_status);
    }

    public void onTrigger(orbotEnums.eOrbotViewCommands p_commands, List<Object> p_data){
        if(p_commands == orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGE_SETTINGS_VIEWS){
            bridgeSettingsStatus((boolean) p_data.get(0));
        }
        else if(p_commands == orbotEnums.eOrbotViewCommands.M_INIT_POST_UI){
            initPostUI();
        }
        else if(p_commands == orbotEnums.eOrbotViewCommands.M_INIT_UI){
            initViews((boolean)p_data.get(0),(boolean) p_data.get(1));
        }
    }
}
