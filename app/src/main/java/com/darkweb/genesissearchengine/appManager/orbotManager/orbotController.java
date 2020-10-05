package com.darkweb.genesissearchengine.appManager.orbotManager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.bridgeManager.bridgeController;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.dataController;
import com.darkweb.genesissearchengine.dataManager.dataEnums;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.pluginController;
import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.Collections;

public class orbotController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private SwitchMaterial m_bridge_switch;
    private SwitchMaterial m_vpn_switch;
    private orbotViewController m_orbot_view_controller;
    private LinearLayout m_customizable_bridge_menu;

    /* INITIALIZATIONS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orbot_settings_view);

        viewsInitializations();
    }

    public void viewsInitializations() {
        m_bridge_switch = findViewById(R.id.p_bridge_switch);
        m_vpn_switch = findViewById(R.id.p_vpn_switch);
        m_customizable_bridge_menu = findViewById(R.id.p_customizable_bridge_menu);

        m_orbot_view_controller = new orbotViewController(m_bridge_switch,m_vpn_switch, this, m_customizable_bridge_menu);
        m_orbot_view_controller.onTrigger(orbotEnums.eOrbotViewCommands.M_INIT_UI, Arrays.asList(status.sVPNStatus,status.sBridgeStatus));
    }
    // status.sGatewayAuto || status.sGatewayManual
    /* LISTENERS */

    public void onBridgeSwitch(View view){
        status.sBridgeStatus = !m_bridge_switch.isChecked();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.S_BRIDGE_ENABLES,status.sBridgeStatus));
        pluginController.getInstance().updateBridges(status.sBridgeStatus);
        m_orbot_view_controller.onTrigger(orbotEnums.eOrbotViewCommands.M_UPDATE_BRIDGE_SETTINGS_VIEWS, Collections.singletonList(status.sBridgeStatus));
    }

    public void openBridgeSettings(View view){
        helperMethod.openActivity(bridgeController.class, constants.LIST_HISTORY, orbotController.this, true);
    }

    public void onVPNSwitch(View view){
        status.sVPNStatus = !m_vpn_switch.isChecked();
        dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.S_VPN_ENABLED,status.sVPNStatus));
        m_orbot_view_controller.updateVPN(status.sVPNStatus);
        pluginController.getInstance().updateVPN(status.sVPNStatus);
    }

    /* LOCAL OVERRIDES */

    @Override
    public void onResume()
    {
        activityContextManager.getInstance().setCurrentActivity(this);
        m_orbot_view_controller.onTrigger(orbotEnums.eOrbotViewCommands.M_INIT_POST_UI,null);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClose(View view){
        finish();
    }
}